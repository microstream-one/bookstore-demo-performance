
package one.microstream.demo.bookstore.ui;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import javax.money.MonetaryAmount;

import org.springframework.data.domain.PageRequest;

import com.google.common.collect.Range;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.app.ActionExecutor;
import one.microstream.demo.bookstore.app.QueryAction;
import one.microstream.demo.bookstore.data.Country;
import one.microstream.demo.bookstore.data.Data;
import one.microstream.demo.bookstore.data.Shop;
import one.microstream.demo.bookstore.jpa.dal.Repositories;
import one.microstream.demo.bookstore.jpa.domain.CountryEntity;
import one.microstream.demo.bookstore.jpa.domain.ShopEntity;


class Query
{
	public static Query[] All(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query[] {
			AllCustomersPaged(data, repositories),
			BooksByPrice(data, repositories),
			BooksByTitle(data, repositories),
			RevenueOfShopInYear(data, repositories),
			BestSellerList(data, repositories),
			EmployeeOfTheYear(data, repositories),
			PurchasesOfForeigners(data, repositories)
		};
	}

	public static Query AllCustomersPaged(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query(
			"All customers paged",
			(executor, iterations) ->
			{
				final int pageSize = 100;
				IntStream.rangeClosed(0, 2).forEach(page ->
					IntStream.rangeClosed(1, iterations).forEach(iteration ->
						executor.submit(QueryAction.New(
							"All customers paged, page=" + (page + 1) + " [" + iteration + "]",
							() -> data.customers().compute(customers ->
								customers.skip(page * pageSize).limit(pageSize).collect(toList())
							),
							() -> repositories.customerRepository().findAll(PageRequest.of(page, pageSize))
						))
					)
				);
			}
		);
	}

	public static Query BooksByPrice(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query(
			"Books by price",
			(executor, iterations) ->
			{
				final int priceRange = 5;
				IntStream.rangeClosed(1, 3).forEach(priceStep ->
					IntStream.rangeClosed(1, iterations).forEach(iteration -> {
						final double minPrice = priceStep * priceRange;
						final double maxPrice = minPrice  + priceRange;
						final MonetaryAmount minPriceMoney = BookStoreDemo.money(minPrice);
						final MonetaryAmount maxPriceMoney = BookStoreDemo.money(maxPrice);
						executor.submit(QueryAction.New(
							"Books by price " + minPrice + " - " + maxPrice + " [" + iteration + "]",
							() -> data.books().compute(books ->
									books.filter(b ->
										b.retailPrice().isGreaterThanOrEqualTo(minPriceMoney) &&
										b.retailPrice().isLessThan(maxPriceMoney)
									)
									.collect(toList())
							),
							() -> repositories.bookRepository()
								.findByRetailPriceGreaterThanEqualAndRetailPriceLessThan(
									minPriceMoney, maxPriceMoney
								)
						));
					})
				);
			}
		);
	}

	public static Query BestSellerList(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query(
			"Bestseller list",
			(executor, iterations) ->
			{
				randomYears(data, 3).forEach(year ->
					randomCountries(data, repositories, 3).forEach((country, countryEntity) ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Bestseller of " + year + " in " + country.name() + " [" + iteration + "]",
								() -> data.purchases().bestSellerList(year, country),
								() -> repositories.purchaseItemRepository().bestSellerList(year, countryEntity)
							))
						)
					)
				);
			}
		);
	}

	public static Query EmployeeOfTheYear(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query(
			"Employee of the year",
			(executor, iterations) ->
			{
				randomYears(data, 3).forEach(year ->
					randomCountries(data, repositories, 3).forEach((country, countryEntity) ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Employee of " + year + " in " + country.name() + " [" + iteration + "]",
								() -> data.purchases().employeeOfTheYear(year, country),
								() -> repositories.employeeRepository().employeeOfTheYear(year, countryEntity.getId())
							))
						)
					)
				);
			}
		);
	}

	public static Query PurchasesOfForeigners(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query(
			"Purchases of foreigners",
			(executor, iterations) ->
			{
				randomYears(data, 3).forEach(year ->
					randomCountries(data, repositories, 3).forEach((country, countryEntity) ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Purchases of foreigners " + year + " in " + country.name() + " [" + iteration + "]",
								() -> data.purchases().purchasesOfForeigners(year, country),
								() -> repositories.purchaseRepository().findPurchasesOfForeigners(year, countryEntity)
							))
						)
					)
				);
			}
		);
	}

	public static Query BooksByTitle(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query(
			"Books by title",
			(executor, iterations) ->
			{
				randomCountries(data, repositories, 3).forEach((country, countryEntity) ->
					Arrays.asList("the","light","black","hero","of").forEach(pattern ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Books by title *" + pattern + "* in " + country.name() + " [" + iteration + "]",
								() -> data.books().searchByTitle(pattern).stream()
									.filter(book -> book.author().address().city().state().country() == country)
									.collect(toList()),
								() -> repositories.bookRepository()
									.findByTitleContainingIgnoreCaseAndAuthorAddressCityStateCountry(pattern, countryEntity)
							))
						)
					)
				);
			}
		);
	}

	public static Query RevenueOfShopInYear(
		final Data data,
		final Repositories repositories
	)
	{
		return new Query(
			"Revenue of shop",
			(executor, iterations) ->
			{
				final int shopCount = data.shops().shopCount();
				final int shopIndex = RANDOM.nextInt(shopCount);
				final Shop shop = data.shops().compute(shops -> shops.skip(shopIndex).findFirst().get());
				final ShopEntity shopEntity = repositories.shopRepository().findById(shopIndex + 1L).get();
				randomYears(data, 3).forEach(year ->
				{
					IntStream.rangeClosed(1, iterations).forEach(iteration ->
						executor.submit(QueryAction.New(
							"Revenue of shop " + shop.name() + " in " + year + " [" + iteration + "]",
							() -> data.purchases().revenueOfShopInYear(shop, year),
							() -> repositories.purchaseItemRepository().revenueOfShopInYear(shopEntity, year)
						))
					);
				});
			}
		);
	}


	private final static Random RANDOM = new Random();

	private static IntStream randomYears(final Data data, final int yearSpan)
	{
		final Range<Integer> years     = data.purchases().years();
		final int            minYear   = years.lowerEndpoint();
		final int            maxYear   = years.upperEndpoint();
		final int            startYear = minYear + RANDOM.nextInt(maxYear - minYear - yearSpan + 1);
		return IntStream.range(startYear, startYear + yearSpan);
	}

	private static Map<Country, CountryEntity> randomCountries(
		final Data data,
		final Repositories repositories,
		final int maxCount
	)
	{
		final List<Country> countries = data.shops().compute(shops ->
			shops.map(s -> s.address().city().state().country())
				.distinct()
				.collect(toList())
		);
		Collections.shuffle(countries);
		final Map<Country, CountryEntity> randomCountries = new HashMap<>();
		final int                         count           = Math.min(maxCount, countries.size());
		for(int i = 0; i < count; i++)
		{
			final Country       country       = countries.get(i);
			final CountryEntity countryEntity = repositories.countryRepository().findByName(country.name()).get();
			randomCountries.put(country, countryEntity);
		}
		return randomCountries;
	}


	private final String                              name;
	private final BiConsumer<ActionExecutor, Integer> actionSubmitter;

	Query(
		final String name,
		final BiConsumer<ActionExecutor, Integer> actionSubmitter
	)
	{
		super();
		this.name            = name;
		this.actionSubmitter = actionSubmitter;
	}

	public BiConsumer<ActionExecutor, Integer> actionSubmitter()
	{
		return this.actionSubmitter;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

}
