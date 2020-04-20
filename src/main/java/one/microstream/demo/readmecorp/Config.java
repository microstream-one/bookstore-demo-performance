
package one.microstream.demo.readmecorp;

import javax.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import one.microstream.demo.readmecorp.dal.DataAccess;
import one.microstream.demo.readmecorp.jpa.dal.AddressRepository;
import one.microstream.demo.readmecorp.jpa.dal.AuthorRepository;
import one.microstream.demo.readmecorp.jpa.dal.BookRepository;
import one.microstream.demo.readmecorp.jpa.dal.CityRepository;
import one.microstream.demo.readmecorp.jpa.dal.CountryRepository;
import one.microstream.demo.readmecorp.jpa.dal.CustomerRepository;
import one.microstream.demo.readmecorp.jpa.dal.EmployeeRepository;
import one.microstream.demo.readmecorp.jpa.dal.GenreRepository;
import one.microstream.demo.readmecorp.jpa.dal.InventoryItemRepository;
import one.microstream.demo.readmecorp.jpa.dal.LanguageRepository;
import one.microstream.demo.readmecorp.jpa.dal.PublisherRepository;
import one.microstream.demo.readmecorp.jpa.dal.PurchaseItemRepository;
import one.microstream.demo.readmecorp.jpa.dal.PurchaseRepository;
import one.microstream.demo.readmecorp.jpa.dal.Repositories;
import one.microstream.demo.readmecorp.jpa.dal.ShopRepository;
import one.microstream.demo.readmecorp.jpa.dal.StateRepository;


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
	public ReadMeCorp getReadMeCorp(final AppConfig appConfig)
	{
		return new ReadMeCorp(appConfig);
	}
	
	@Bean
	public DataAccess getDataAccess(
		final ReadMeCorp readMeCorp
	)
	{
		return DataAccess.New(readMeCorp);
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
