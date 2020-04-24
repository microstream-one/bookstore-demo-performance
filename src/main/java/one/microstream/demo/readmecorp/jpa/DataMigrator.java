
package one.microstream.demo.readmecorp.jpa;

import static java.util.function.Function.identity;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import one.microstream.bytes.ByteMultiple;
import one.microstream.demo.readmecorp.ReadMeCorp;
import one.microstream.demo.readmecorp.data.Address;
import one.microstream.demo.readmecorp.data.Author;
import one.microstream.demo.readmecorp.data.Book;
import one.microstream.demo.readmecorp.data.City;
import one.microstream.demo.readmecorp.data.Country;
import one.microstream.demo.readmecorp.data.Customer;
import one.microstream.demo.readmecorp.data.Data;
import one.microstream.demo.readmecorp.data.Employee;
import one.microstream.demo.readmecorp.data.Genre;
import one.microstream.demo.readmecorp.data.Language;
import one.microstream.demo.readmecorp.data.Publisher;
import one.microstream.demo.readmecorp.data.Purchase;
import one.microstream.demo.readmecorp.data.Purchase.Item;
import one.microstream.demo.readmecorp.data.Shop;
import one.microstream.demo.readmecorp.data.State;
import one.microstream.demo.readmecorp.jpa.dal.BaseRepository;
import one.microstream.demo.readmecorp.jpa.dal.BaseRepositoryCustom.Dumper;
import one.microstream.demo.readmecorp.jpa.dal.BaseRepositoryImpl.PreparedStatementSetter;
import one.microstream.demo.readmecorp.jpa.dal.Repositories;
import one.microstream.demo.readmecorp.jpa.domain.BaseEntity;


public interface DataMigrator
{
	public void migrate();


	public static DataMigrator SqlFile(
		final ReadMeCorp readMeCorp,
		final Repositories repositories
	)
	{
		return new SqlFile(readMeCorp, repositories);
	}

	public static DataMigrator BatchInsert(
		final ReadMeCorp readMeCorp,
		final Repositories repositories
	)
	{
		return new BatchInsert(readMeCorp, repositories);
	}


	public static abstract class Abstract implements DataMigrator, HasLogger
	{
		static class AutoIncrement
		{
			AtomicLong value = new AtomicLong();

			long next()
			{
				return this.value.incrementAndGet();
			}
		}

		static class EntityIdMap<T> extends HashMap<T, Long>
		{
		}

		static <T> Collector<T, ?, EntityIdMap<T>> toEntityIdMap()
		{
			return toEntityIdMap(new AutoIncrement());
		}

		static <T> Collector<T, ?, EntityIdMap<T>> toEntityIdMap(final AutoIncrement id)
		{
			return Collectors.toMap(identity(), e -> id.next(), (u,v) -> u, EntityIdMap<T>::new);
		}

		static final class ForeignIdItem<T>
		{
			final T    item;
			final Long foreignId;

			ForeignIdItem(
				final T item,
				final Long foreignId
			)
			{
				super();
				this.item      = item;
				this.foreignId = foreignId;
			}
		}

		static final class InventoryItem
		{
			final long shopId;
			final long bookId;
			final int amount;

			InventoryItem(
				final long shopId,
				final long bookId,
				final int amount
			)
			{
				super();
				this.shopId = shopId;
				this.bookId = bookId;
				this.amount = amount;
			}
		}

		@SafeVarargs
		static <T> Stream<T> concat(
			final Stream<T>... streams
		)
		{
			return Stream.of(streams)
				.reduce(Stream::concat)
				.orElseGet(Stream::empty);
		}


		final ReadMeCorp   readMeCorp;
		final Repositories repositories;

		Abstract(
			final ReadMeCorp readMeCorp,
			final Repositories repositories
		)
		{
			super();
			this.readMeCorp   = readMeCorp;
			this.repositories = repositories;
		}
	}


	public static class SqlFile extends Abstract
	{
		static class BatchDumper<T> implements Dumper<T>
		{
			private final Iterator<Entry<T, Long>> iterator;
			private final int                      size;
			private final BiConsumer<Object[], T>  valueSetter;
			private final Object[]                       record;

			BatchDumper(
				final EntityIdMap<T> entityIdMap,
				final int recordSize,
				final BiConsumer<Object[], T> valueSetter
			)
			{
				super();
				this.iterator    = entityIdMap.entrySet().iterator();
				this.size        = entityIdMap.size();
				this.valueSetter = valueSetter;
				this.record      = new Object[recordSize];
			}

			@Override
			public int batchSize()
			{
				return this.size;
			}

			@Override
			public Object[] values(
				final int index
			)
			{
				final Entry<T, Long> entry  = this.iterator.next();
				this.record[0] = entry.getValue();
				this.valueSetter.accept(this.record, entry.getKey());
				return this.record;
			}
		}


		private Writer sqlWriter;

		SqlFile(
			final ReadMeCorp readMeCorp,
			final Repositories repositories
		)
		{
			super(readMeCorp, repositories);
		}

		@Override
		public void migrate()
		{
			try(PrintWriter sqlWriter = this.openSqlWriter())
			{
				this.sqlWriter = sqlWriter;

				final EntityIdMap<Address>                           addresses = this.migrateAddresses();
				final EntityIdMap<Customer>                          customers = this.migrateCustomers(addresses);
				final EntityIdMap<Book>                              books     = this.migrateBooks(addresses);
				final Pair<EntityIdMap<Shop>, EntityIdMap<Employee>> pair      = this.migrateShops(addresses, customers, books);
				final EntityIdMap<Shop>                              shops     = pair.getLeft();
				final EntityIdMap<Employee>                          employees = pair.getRight();

				addresses.clear();
				System.gc();

				this.migratePurchases(customers, employees, books, shops);

				customers.clear();
				books.clear();
				shops.clear();
				employees.clear();
				System.gc();
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		}

		private PrintWriter openSqlWriter() throws IOException
		{
			final PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(
						Paths.get(this.readMeCorp.getAppConfig().dataDir(), "readmecorp.sql").toFile()
					),
					"UTF-8"),
				(int)ByteMultiple.MiB.toBytes(100) // buffer size
			));

			writer.print("SET statement_timeout = 0;\n");
			writer.print("SET lock_timeout = 0;\n");
			writer.print("SET idle_in_transaction_session_timeout = 0;\n");
			writer.print("SET client_encoding = 'UTF8';\n");
			writer.print("SET check_function_bodies = false;\n");
			writer.print("SET xmloption = content;\n");
			writer.print("SET client_min_messages = warning;\n");
			writer.print("SET row_security = off;\n");
			writer.print("\n");

			return writer;
		}

		private EntityIdMap<Address> migrateAddresses()
		{
			this.logger().info("> addresses");

			final Data data = this.readMeCorp.data();

			final EntityIdMap<Address> addresses = concat(
					data.books().authors().map(Author::address),
					data.books().publishers().map(Publisher::address),
					data.shops().flatMap(shop -> concat(
						Stream.of(shop.address()),
						shop.employees().map(Employee::address)
					)),
					data.customers().map(Customer::address)
				)
				.distinct()
				.collect(toEntityIdMap());

			final EntityIdMap<City> cities = addresses.keySet().stream()
				.map(Address::city)
				.distinct()
				.collect(toEntityIdMap());

			final EntityIdMap<State> states = cities.keySet().stream()
				.map(City::state)
				.distinct()
				.collect(toEntityIdMap());

			final EntityIdMap<Country> countries = states.keySet().stream()
				.map(State::country)
				.distinct()
				.collect(toEntityIdMap());

			this.dump(this.repositories.countryRepository(), new BatchDumper<>(countries, 3, (record, entity) ->
			{
				record[1] = entity.name();
				record[2] = entity.code();
			}));

			this.dump(this.repositories.stateRepository(), new BatchDumper<>(states, 3, (record, entity) ->
			{
				record[1] = entity.name();
				record[2] = countries.get(entity.country());
			}));

			this.dump(this.repositories.cityRepository(), new BatchDumper<>(cities, 3, (record, entity) ->
			{
				record[1] = entity.name();
				record[2] = states.get(entity.state());
			}));

			this.dump(this.repositories.addressRepository(), new BatchDumper<>(addresses, 5, (record, entity) ->
			{
				record[1] = entity.address();
				record[2] = entity.address2();
				record[3] = entity.zipCode();
				record[4] = cities.get(entity.city());
			}));

			countries.clear();
			states.clear();
			cities.clear();

			this.logger().info("+ " + addresses.size() + " addresses");

			return addresses;
		}

		private EntityIdMap<Customer> migrateCustomers(
			final EntityIdMap<Address> addresses
		)
		{
			this.logger().info("> customers");

			final EntityIdMap<Customer> customers = this.readMeCorp.data().customers()
				.collect(toEntityIdMap());

			this.dump(this.repositories.customerRepository(), new BatchDumper<>(customers, 3, (record, entity) ->
			{
				record[1] = entity.name();
				record[2] = addresses.get(entity.address());
			}));

			this.logger().info("+ " + customers.size() + " customers");

			return customers;
		}

		private EntityIdMap<Book> migrateBooks(
			final EntityIdMap<Address> addressMap
		)
		{
			this.logger().info("> books");

			final Data data = this.readMeCorp.data();

			final EntityIdMap<Genre> genres = data.books()
				.genres()
				.collect(toEntityIdMap());

			this.dump(this.repositories.genreRepository(), new BatchDumper<>(genres, 2, (record, entity) ->
			{
				record[1] = entity.name();
			}));

			final EntityIdMap<Author> authors = data.books()
				.authors()
				.collect(toEntityIdMap());

			this.dump(this.repositories.authorRepository(), new BatchDumper<>(authors, 3, (record, entity) ->
			{
				record[1] = entity.name();
				record[2] = addressMap.get(entity.address());
			}));

			final EntityIdMap<Publisher> publishers = data.books()
				.publishers()
				.collect(toEntityIdMap());

			this.dump(this.repositories.publisherRepository(), new BatchDumper<>(publishers, 3, (record, entity) ->
			{
				record[1] = entity.name();
				record[2] = addressMap.get(entity.address());
			}));

			final EntityIdMap<Language> languages = data.books()
				.languages()
				.collect(toEntityIdMap());

			this.dump(this.repositories.languageRepository(), new BatchDumper<>(languages, 2, (record, entity) ->
			{
				record[1] = entity.locale().toLanguageTag();
			}));

			final EntityIdMap<Book> books = data.books()
				.all()
				.collect(toEntityIdMap());

			this.dump(this.repositories.bookRepository(), new BatchDumper<>(books, 8, (record, entity) ->
			{
				record[1] = entity.isbn13();
				record[2] = entity.price();
				record[3] = entity.title();
				record[4] = authors.get(entity.author());
				record[5] = genres.get(entity.genre());
				record[6] = languages.get(entity.language());
				record[7] = publishers.get(entity.publisher());
			}));

			authors.clear();
			genres.clear();
			languages.clear();
			publishers.clear();

			this.logger().info("+ " + books.size() + " books");

			return books;
		}

		private Pair<EntityIdMap<Shop>, EntityIdMap<Employee>> migrateShops(
			final EntityIdMap<Address> addresses,
			final EntityIdMap<Customer> customers,
			final EntityIdMap<Book> books
		)
		{
			this.logger().info("> shops");

			final EntityIdMap<Shop> shops = this.readMeCorp.data().shops()
				.collect(toEntityIdMap());

			this.dump(this.repositories.shopRepository(), new BatchDumper<>(shops, 3, (record, entity) ->
			{
				record[1] = entity.name();
				record[2] = addresses.get(entity.address());
			}));

			final EntityIdMap<ForeignIdItem<Employee>> employees = shops.entrySet().stream()
				.flatMap(shopEntry -> {
					final Shop shop = shopEntry.getKey();
					final Long sid  = shopEntry.getValue();
					return shop.employees().map(employee -> new ForeignIdItem<>(employee, sid));
				})
				.collect(toEntityIdMap());

			this.dump(this.repositories.employeeRepository(), new BatchDumper<>(employees, 4, (record, entity) ->
			{
				record[1] = entity.item.name();
				record[2] = addresses.get(entity.item.address());
				record[3] = entity.foreignId;
			}));

			final EntityIdMap<InventoryItem> inventoryItems = shops.entrySet().stream()
				.flatMap(shopEntry -> {
					final Shop shop = shopEntry.getKey();
					final Long sid  = shopEntry.getValue();
					return shop.inventory().slots().map(slot -> {
						final Book book = slot.getKey();
						final int amount = slot.getValue();
						return new InventoryItem(sid, books.get(book), amount);
					});
				})
				.collect(toEntityIdMap());

			this.dump(this.repositories.inventoryItemRepository(), new BatchDumper<>(inventoryItems, 4, (record, entity) ->
			{
				record[1] = entity.shopId;
				record[2] = entity.bookId;
				record[3] = entity.amount;
			}));

			this.logger().info("+ " + shops.size() + " shops");

			return Pair.of(
				shops,
				employees.keySet().stream().map(i -> i.item).collect(toEntityIdMap())
			);
		}

		private void migratePurchases(
			final EntityIdMap<Customer> customers,
			final EntityIdMap<Employee> employees,
			final EntityIdMap<Book> books,
			final EntityIdMap<Shop> shops
		)
		{
			final Data          data           = this.readMeCorp.data();
			final AutoIncrement purchaseId     = new AutoIncrement();
			final AutoIncrement purchaseItemId = new AutoIncrement();

			data.purchases().years().forEach(year ->
			{
				this.logger().info("> purchases in " + year);

				final EntityIdMap<Purchase> purchases = data.purchases().byYear(year)
					.collect(toEntityIdMap(purchaseId));

				this.dump(this.repositories.purchaseRepository(), new BatchDumper<>(purchases, 5, (record, entity) ->
				{
					record[1] = employees.get(entity.employee());
					record[2] = customers.get(entity.customer());
					record[3] = LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.timestamp()), ZoneOffset.UTC);
					record[4] = shops.get(entity.shop());
				}));

				final EntityIdMap<ForeignIdItem<Item>> purchaseItems = purchases.entrySet().stream()
					.flatMap(purchaseEntry -> {
						final Purchase purchase = purchaseEntry.getKey();
						final Long     pid      = purchaseEntry.getValue();
						return purchase.items().map(item -> new ForeignIdItem<>(item, pid));
					})
					.collect(toEntityIdMap(purchaseItemId));

				this.dump(this.repositories.purchaseItemRepository(), new BatchDumper<>(purchaseItems, 4, (record, entity) ->
				{
					record[1] = entity.foreignId;
					record[2] = books.get(entity.item.book());
					record[3] = entity.item.amount();
				}));

				this.logger().info("+ " + purchases.size() + " purchases in " + year);

				purchaseItems.clear();
				data.purchases().clear(year);
				purchases.clear();

				System.gc();
			});
		}

		private <T extends BaseEntity> void dump(
			final BaseRepository<T> repository,
			final BatchDumper<?> batchDumper
		)
		{
			try
			{
				repository.dump(batchDumper, this.sqlWriter);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		}

	}

	public static class BatchInsert extends Abstract
	{
		static class BatchInserter<T> implements BatchPreparedStatementSetter
		{
			private final Iterator<Entry<T, Long>>   iterator;
			private final int                        size;
			private final PreparedStatementSetter<T> setter;

			BatchInserter(
				final EntityIdMap<T> entityIdMap,
				final PreparedStatementSetter<T> setter
			)
			{
				super();
				this.iterator = entityIdMap.entrySet().iterator();
				this.size     = entityIdMap.size();
				this.setter   = setter;
			}

			@Override
			public void setValues(
				final PreparedStatement ps,
				final int i
			)
				throws SQLException
			{

				final Entry<T, Long> entry  = this.iterator.next();
				ps.setLong(1, entry.getValue());
				this.setter.setValues(ps, entry.getKey());
			}

			@Override
			public int getBatchSize()
			{
				return this.size;
			}
		}


		BatchInsert(
			final ReadMeCorp readMeCorp,
			final Repositories repositories
		)
		{
			super(readMeCorp, repositories);
		}

		@Override
		public void migrate()
		{
			final EntityIdMap<Address>                           addresses = this.migrateAddresses();
			final EntityIdMap<Customer>                          customers = this.migrateCustomers(addresses);
			final EntityIdMap<Book>                              books     = this.migrateBooks(addresses);
			final Pair<EntityIdMap<Shop>, EntityIdMap<Employee>> pair      = this.migrateShops(addresses, customers, books);
			final EntityIdMap<Shop>                              shops     = pair.getLeft();
			final EntityIdMap<Employee>                          employees = pair.getRight();

			addresses.clear();
			System.gc();

			this.migratePurchases(customers, employees, books, shops);

			customers.clear();
			books.clear();
			shops.clear();
			employees.clear();
			System.gc();
		}

		private EntityIdMap<Address> migrateAddresses()
		{
			this.logger().info("> addresses");

			final Data data = this.readMeCorp.data();

			final EntityIdMap<Address> addresses = concat(
					data.books().authors().map(Author::address),
					data.books().publishers().map(Publisher::address),
					data.shops().flatMap(shop -> concat(
						Stream.of(shop.address()),
						shop.employees().map(Employee::address)
					)),
					data.customers().map(Customer::address)
				)
				.distinct()
				.collect(toEntityIdMap());

			final EntityIdMap<City> cities = addresses.keySet().stream()
				.map(Address::city)
				.distinct()
				.collect(toEntityIdMap());

			final EntityIdMap<State> states = cities.keySet().stream()
				.map(City::state)
				.distinct()
				.collect(toEntityIdMap());

			final EntityIdMap<Country> countries = states.keySet().stream()
				.map(State::country)
				.distinct()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.countryRepository(), new BatchInserter<>(countries, (ps, entity) ->
			{
				ps.setString(2, entity.name());
				ps.setString(3, entity.code());
			}));

			this.batchInsert(this.repositories.stateRepository(), new BatchInserter<>(states, (ps, entity) ->
			{
				ps.setString(2, entity.name());
				ps.setLong(3, countries.get(entity.country()));
			}));

			this.batchInsert(this.repositories.cityRepository(), new BatchInserter<>(cities, (ps, entity) ->
			{
				ps.setString(2, entity.name());
				ps.setLong(3, states.get(entity.state()));
			}));

			this.batchInsert(this.repositories.addressRepository(), new BatchInserter<>(addresses, (ps, entity) ->
			{
				ps.setString(2, entity.address());
				ps.setString(3, entity.address2());
				ps.setString(4, entity.zipCode());
				ps.setLong(5, cities.get(entity.city()));
			}));

			countries.clear();
			states.clear();
			cities.clear();

			this.logger().info("+ " + addresses.size() + " addresses");

			return addresses;
		}

		private EntityIdMap<Customer> migrateCustomers(
			final EntityIdMap<Address> addresses
		)
		{
			this.logger().info("> customers");

			final EntityIdMap<Customer> customers = this.readMeCorp.data().customers()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.customerRepository(), new BatchInserter<>(customers, (ps, entity) ->
			{
				ps.setString(2, entity.name());
				ps.setLong(3, addresses.get(entity.address()));
			}));

			this.logger().info("+ " + customers.size() + " customers");

			return customers;
		}

		private EntityIdMap<Book> migrateBooks(
			final EntityIdMap<Address> addressMap
		)
		{
			this.logger().info("> books");

			final Data data = this.readMeCorp.data();

			final EntityIdMap<Genre> genres = data.books()
				.genres()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.genreRepository(), new BatchInserter<>(genres, (ps, entity) ->
			{
				ps.setString(2, entity.name());
			}));

			final EntityIdMap<Author> authors = data.books()
				.authors()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.authorRepository(), new BatchInserter<>(authors, (ps, entity) ->
			{
				ps.setString(2, entity.name());
				ps.setLong(3, addressMap.get(entity.address()));
			}));

			final EntityIdMap<Publisher> publishers = data.books()
				.publishers()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.publisherRepository(), new BatchInserter<>(publishers, (ps, entity) ->
			{
				ps.setString(2, entity.name());
				ps.setLong(3, addressMap.get(entity.address()));
			}));

			final EntityIdMap<Language> languages = data.books()
				.languages()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.languageRepository(), new BatchInserter<>(languages, (ps, entity) ->
			{
				ps.setString(2, entity.locale().toLanguageTag());
			}));

			final EntityIdMap<Book> books = data.books()
				.all()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.bookRepository(), new BatchInserter<>(books, (ps, entity) ->
			{
				ps.setString(2, entity.isbn13());
				ps.setDouble(3, entity.price());
				ps.setString(4, entity.title());
				ps.setLong(5, authors.get(entity.author()));
				ps.setLong(6, genres.get(entity.genre()));
				ps.setLong(7, languages.get(entity.language()));
				ps.setLong(8, publishers.get(entity.publisher()));
			}));

			authors.clear();
			genres.clear();
			languages.clear();
			publishers.clear();

			this.logger().info("+ " + books.size() + " books");

			return books;
		}

		private Pair<EntityIdMap<Shop>, EntityIdMap<Employee>> migrateShops(
			final EntityIdMap<Address> addresses,
			final EntityIdMap<Customer> customers,
			final EntityIdMap<Book> books
		)
		{
			this.logger().info("> shops");

			final EntityIdMap<Shop> shops = this.readMeCorp.data().shops()
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.shopRepository(), new BatchInserter<>(shops, (ps, entity) ->
			{
				ps.setString(2, entity.name());
				ps.setLong(3, addresses.get(entity.address()));
			}));

			final EntityIdMap<ForeignIdItem<Employee>> employees = shops.entrySet().stream()
				.flatMap(shopEntry -> {
					final Shop shop = shopEntry.getKey();
					final Long sid  = shopEntry.getValue();
					return shop.employees().map(employee -> new ForeignIdItem<>(employee, sid));
				})
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.employeeRepository(), new BatchInserter<>(employees, (ps, entity) ->
			{
				ps.setString(2, entity.item.name());
				ps.setLong(3, addresses.get(entity.item.address()));
				ps.setLong(4, entity.foreignId);
			}));

			final EntityIdMap<InventoryItem> inventoryItems = shops.entrySet().stream()
				.flatMap(shopEntry -> {
					final Shop shop = shopEntry.getKey();
					final Long sid  = shopEntry.getValue();
					return shop.inventory().slots().map(slot -> {
						final Book book = slot.getKey();
						final int amount = slot.getValue();
						return new InventoryItem(sid, books.get(book), amount);
					});
				})
				.collect(toEntityIdMap());

			this.batchInsert(this.repositories.inventoryItemRepository(), new BatchInserter<>(inventoryItems, (ps, entity) ->
			{
				ps.setLong(2, entity.shopId);
				ps.setLong(3, entity.bookId);
				ps.setInt(4, entity.amount);
			}));

			this.logger().info("+ " + shops.size() + " shops");

			return Pair.of(
				shops,
				employees.keySet().stream().map(i -> i.item).collect(toEntityIdMap())
			);
		}

		private void migratePurchases(
			final EntityIdMap<Customer> customers,
			final EntityIdMap<Employee> employees,
			final EntityIdMap<Book> books,
			final EntityIdMap<Shop> shops
		)
		{
			final Data          data           = this.readMeCorp.data();
			final AutoIncrement purchaseId     = new AutoIncrement();
			final AutoIncrement purchaseItemId = new AutoIncrement();

			data.purchases().years().forEach(year ->
			{
				this.logger().info("> purchases in " + year);

				final EntityIdMap<Purchase> purchases = data.purchases().byYear(year)
					.collect(toEntityIdMap(purchaseId));

				this.batchInsert(this.repositories.purchaseRepository(), new BatchInserter<>(purchases, (ps, entity) ->
				{
					ps.setLong(2, employees.get(entity.employee()));
					ps.setLong(3, customers.get(entity.customer()));
					ps.setTimestamp(4, new Timestamp(entity.timestamp()));
					ps.setLong(5, shops.get(entity.shop()));
				}));

				final EntityIdMap<ForeignIdItem<Item>> purchaseItems = purchases.entrySet().stream()
					.flatMap(purchaseEntry -> {
						final Purchase purchase = purchaseEntry.getKey();
						final Long     pid      = purchaseEntry.getValue();
						return purchase.items().map(item -> new ForeignIdItem<>(item, pid));
					})
					.collect(toEntityIdMap(purchaseItemId));

				this.batchInsert(this.repositories.purchaseItemRepository(), new BatchInserter<>(purchaseItems, (ps, entity) ->
				{
					ps.setLong(2, entity.foreignId);
					ps.setLong(3, books.get(entity.item.book()));
					ps.setInt(4, entity.item.amount());
				}));

				this.logger().info("+ " + purchases.size() + " purchases in " + year);

				purchaseItems.clear();
				data.purchases().clear(year);
				purchases.clear();

				System.gc();
			});
		}

		private <T extends BaseEntity> void batchInsert(
			final BaseRepository<T> repository,
			final BatchInserter<?> batchInserter
		)
		{
			repository.batchInsert(batchInserter);
		}

	}

}
