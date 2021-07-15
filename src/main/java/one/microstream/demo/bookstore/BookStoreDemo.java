
package one.microstream.demo.bookstore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.format.CurrencyStyle;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.springframework.boot.SpringApplication;

import one.microstream.demo.bookstore.data.Data;
import one.microstream.demo.bookstore.data.DataMetrics;
import one.microstream.persistence.binary.jdk8.types.BinaryHandlersJDK8;
import one.microstream.persistence.types.Storer;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;


/**
 * Central singleton, which holds the {@link EmbeddedStorageManager} and the {@link Data} root object.
 * <p>
 * The demo application simulates a worldwide operating book sales company with stores in many countries.
 * <p>
 * Note: If you start the {@link Application}, which is a {@link SpringApplication},
 * it is created by the {@link ApplicationConfiguration}.
 *
 * @see #data()
 * @see #storageManager()
 * @see <a href="https://manual.docs.microstream.one/data-store/getting-started">MicroStream Reference Manual</a>
 */
public final class BookStoreDemo implements HasLogger
{
	private static BookStoreDemo instance;

	/**
	 * @return the single instance of this class
	 */
	public static BookStoreDemo getInstance()
	{
		return instance;
	}


	/**
	 * {@link CurrencyUnit} for this demo, US Dollar is used as only currency.
	 */
	private static final CurrencyUnit         CURRENCY_UNIT          = Monetary.getCurrency(Locale.US);

	/**
	 * Money format
	 */
	private final static MonetaryAmountFormat MONETARY_AMOUNT_FORMAT = MonetaryFormats.getAmountFormat(
		AmountFormatQueryBuilder.of(Locale.getDefault())
			.set(CurrencyStyle.SYMBOL)
			.build()
	);

	/**
	 * Multiplicant used to calculate retail prices, adds an 11% margin.
	 */
	private final static BigDecimal           RETAIL_MULTIPLICANT    = scale(new BigDecimal(1.11));


	private static BigDecimal scale(final BigDecimal number)
	{
		return number.setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * @return the {@link CurrencyUnit} for this demo, US Dollar is used as only currency.
	 */
	public static CurrencyUnit currencyUnit()
	{
		return CURRENCY_UNIT;
	}

	/**
	 * @return the {@link MonetaryAmountFormat} for this demo
	 */
	public static MonetaryAmountFormat monetaryAmountFormat()
	{
		return MONETARY_AMOUNT_FORMAT;
	}

	/**
	 * Converts a double into a {@link MonetaryAmount}
	 * @param number the number to convert
	 * @return the converted {@link MonetaryAmount}
	 */
	public static MonetaryAmount money(final double number)
	{
		return money(new BigDecimal(number));
	}

	/**
	 * Converts a {@link BigDecimal} into a {@link MonetaryAmount}
	 * @param number the number to convert
	 * @return the converted {@link MonetaryAmount}
	 */
	public static MonetaryAmount money(final BigDecimal number)
	{
		return RoundedMoney.of(scale(number), currencyUnit());
	}

	/**
	 * Calculates the retail price based on a purchase price by adding a margin.
	 * @param purchasePrice the purchase price
	 * @return the calculated retail price
	 * @see #RETAIL_MULTIPLICANT
	 */
	public static MonetaryAmount retailPrice(
		final MonetaryAmount purchasePrice
	)
	{
		return money(RETAIL_MULTIPLICANT.multiply(new BigDecimal(purchasePrice.getNumber().doubleValue())));
	}


	private final DemoConfiguration demoConfiguration;
	private EmbeddedStorageManager  storageManager;

	public BookStoreDemo(final DemoConfiguration demoConfiguration)
	{
		super();

		this.demoConfiguration = demoConfiguration;
	}

	@PostConstruct
	void postConstruct()
	{
		BookStoreDemo.instance = this;
	}

	public DemoConfiguration getDemoConfiguration()
	{
		return this.demoConfiguration;
	}

	public EmbeddedStorageManager storageManager()
	{
		if(this.storageManager == null)
		{
			this.logger().info("Initializing MicroStream StorageManager");

			final EmbeddedStorageFoundation<?> foundation = EmbeddedStorageConfiguration.Builder()
				.setStorageDirectory(Paths.get(this.demoConfiguration.dataDir(), "microstream").toString())
				.setChannelCount(Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1))
				.createEmbeddedStorageFoundation()
			;

			foundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
			this.storageManager = foundation.createEmbeddedStorageManager().start();

			if(this.storageManager.root() == null)
			{
				this.logger().info("No data found, initializing random data");

				final Data.Default data = Data.New();
				this.storageManager.setRoot(data);
				this.storageManager.storeRoot();
				final DataMetrics metrics = data.populate(
					this.demoConfiguration.initialDataAmount(),
					this.storageManager
				);

				this.logger().info("Random data generated: " + metrics.toString());
			}
		}

		return this.storageManager;
	}

	public Storer createStorer()
	{
		return this.storageManager().createStorer();
	}

	public Data data()
	{
		return (Data)this.storageManager().root();
	}

	public void shutdown()
	{
		if(this.storageManager != null)
		{
			this.storageManager.shutdown();
			this.storageManager = null;
		}
	}

}
