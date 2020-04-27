
package one.microstream.demo.bookstore;

import javax.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import one.microstream.demo.bookstore.dal.DataAccess;
import one.microstream.demo.bookstore.jpa.dal.AddressRepository;
import one.microstream.demo.bookstore.jpa.dal.AuthorRepository;
import one.microstream.demo.bookstore.jpa.dal.BookRepository;
import one.microstream.demo.bookstore.jpa.dal.CityRepository;
import one.microstream.demo.bookstore.jpa.dal.CountryRepository;
import one.microstream.demo.bookstore.jpa.dal.CustomerRepository;
import one.microstream.demo.bookstore.jpa.dal.EmployeeRepository;
import one.microstream.demo.bookstore.jpa.dal.GenreRepository;
import one.microstream.demo.bookstore.jpa.dal.InventoryItemRepository;
import one.microstream.demo.bookstore.jpa.dal.LanguageRepository;
import one.microstream.demo.bookstore.jpa.dal.PublisherRepository;
import one.microstream.demo.bookstore.jpa.dal.PurchaseItemRepository;
import one.microstream.demo.bookstore.jpa.dal.PurchaseRepository;
import one.microstream.demo.bookstore.jpa.dal.Repositories;
import one.microstream.demo.bookstore.jpa.dal.ShopRepository;
import one.microstream.demo.bookstore.jpa.dal.StateRepository;


@Configuration
@ComponentScan(basePackageClasses = Repositories.class)
public class Config
{
	@Bean
	public AppConfig getAppConfig()
	{
		return new AppConfig();
	}

	@Bean(destroyMethod = "shutdown")
	public BookStoreDemo getBookStoreDemo(final AppConfig appConfig)
	{
		return new BookStoreDemo(appConfig);
	}

	@Bean
	public DataAccess getDataAccess(
		final BookStoreDemo bookStoreDemo
	)
	{
		return DataAccess.New(bookStoreDemo);
	}

	@Bean
	public Repositories getRepositories(
		final EntityManagerFactory entityManagerFactory,
		final AddressRepository addressRepository,
		final AuthorRepository authorRepository,
		final BookRepository bookRepository,
		final CityRepository cityRepository,
		final CountryRepository countryRepository,
		final CustomerRepository customerRepository,
		final EmployeeRepository employeeRepository,
		final GenreRepository genreRepository,
		final InventoryItemRepository inventoryItemRepository,
		final LanguageRepository languageRepository,
		final PublisherRepository publisherRepository,
		final PurchaseItemRepository purchaseItemRepository,
		final PurchaseRepository purchaseRepository,
		final ShopRepository shopRepository,
		final StateRepository stateRepository
	)
	{
		return Repositories.New(
			entityManagerFactory,
			addressRepository,
			authorRepository,
			bookRepository,
			cityRepository,
			countryRepository,
			customerRepository,
			employeeRepository,
			genreRepository,
			inventoryItemRepository,
			languageRepository,
			publisherRepository,
			purchaseItemRepository,
			purchaseRepository,
			shopRepository,
			stateRepository
		);
	}

}
