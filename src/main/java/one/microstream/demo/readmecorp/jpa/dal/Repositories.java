
package one.microstream.demo.readmecorp.jpa.dal;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;

public interface Repositories
{
	public EntityManagerFactory entityManagerFactory();
	
	public SessionFactory sessionFactory();
	
	public AddressRepository addressRepository();
	
	public AuthorRepository authorRepository();
	
	public BookRepository bookRepository();
	
	public CityRepository cityRepository();
	
	public CountryRepository countryRepository();
	
	public CustomerRepository customerRepository();
	
	public EmployeeRepository employeeRepository();
	
	public GenreRepository genreRepository();
	
	public InventoryItemRepository inventoryItemRepository();
	
	public LanguageRepository languageRepository();
	
	public PublisherRepository publisherRepository();
	
	public PurchaseItemRepository purchaseItemRepository();
	
	public PurchaseRepository purchaseRepository();
	
	public ShopRepository shopRepository();
	
	public StateRepository stateRepository();
	
	public static Repositories New(
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
		return new Default(
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
	
	public static class Default implements Repositories
	{
		private final EntityManagerFactory    entityManagerFactory;
		private final AddressRepository       addressRepository;
		private final AuthorRepository        authorRepository;
		private final BookRepository          bookRepository;
		private final CityRepository          cityRepository;
		private final CountryRepository       countryRepository;
		private final CustomerRepository      customerRepository;
		private final EmployeeRepository      employeeRepository;
		private final GenreRepository         genreRepository;
		private final InventoryItemRepository inventoryItemRepository;
		private final LanguageRepository      languageRepository;
		private final PublisherRepository     publisherRepository;
		private final PurchaseItemRepository  purchaseItemRepository;
		private final PurchaseRepository      purchaseRepository;
		private final ShopRepository          shopRepository;
		private final StateRepository         stateRepository;
		
		Default(
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
			super();
			this.entityManagerFactory    = entityManagerFactory;
			this.addressRepository       = addressRepository;
			this.authorRepository        = authorRepository;
			this.bookRepository          = bookRepository;
			this.cityRepository          = cityRepository;
			this.countryRepository       = countryRepository;
			this.customerRepository      = customerRepository;
			this.employeeRepository      = employeeRepository;
			this.genreRepository         = genreRepository;
			this.inventoryItemRepository = inventoryItemRepository;
			this.languageRepository      = languageRepository;
			this.publisherRepository     = publisherRepository;
			this.purchaseItemRepository  = purchaseItemRepository;
			this.purchaseRepository      = purchaseRepository;
			this.shopRepository          = shopRepository;
			this.stateRepository         = stateRepository;
		}
		
		@Override
		public EntityManagerFactory entityManagerFactory()
		{
			return this.entityManagerFactory;
		}
		
		@Override
		public SessionFactory sessionFactory()
		{
			return this.entityManagerFactory.unwrap(SessionFactory.class);
		}
		
		@Override
		public AddressRepository addressRepository()
		{
			return this.addressRepository;
		}
		
		@Override
		public AuthorRepository authorRepository()
		{
			return this.authorRepository;
		}
		
		@Override
		public BookRepository bookRepository()
		{
			return this.bookRepository;
		}
		
		@Override
		public CityRepository cityRepository()
		{
			return this.cityRepository;
		}
		
		@Override
		public CountryRepository countryRepository()
		{
			return this.countryRepository;
		}
		
		@Override
		public CustomerRepository customerRepository()
		{
			return this.customerRepository;
		}
		
		@Override
		public EmployeeRepository employeeRepository()
		{
			return this.employeeRepository;
		}
		
		@Override
		public GenreRepository genreRepository()
		{
			return this.genreRepository;
		}
		
		@Override
		public InventoryItemRepository inventoryItemRepository()
		{
			return this.inventoryItemRepository;
		}
		
		@Override
		public LanguageRepository languageRepository()
		{
			return this.languageRepository;
		}
		
		@Override
		public PublisherRepository publisherRepository()
		{
			return this.publisherRepository;
		}
		
		@Override
		public PurchaseItemRepository purchaseItemRepository()
		{
			return this.purchaseItemRepository;
		}
		
		@Override
		public PurchaseRepository purchaseRepository()
		{
			return this.purchaseRepository;
		}
		
		@Override
		public ShopRepository shopRepository()
		{
			return this.shopRepository;
		}
		
		@Override
		public StateRepository stateRepository()
		{
			return this.stateRepository;
		}
		
	}
	
}
