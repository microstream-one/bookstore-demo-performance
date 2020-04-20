
package one.microstream.demo.readmecorp;

import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.rapidpm.dependencies.core.logger.HasLogger;

import one.microstream.demo.readmecorp.data.Data;
import one.microstream.demo.readmecorp.data.DataMetrics;
import one.microstream.jdk8.java.util.BinaryHandlersJDK8;
import one.microstream.persistence.types.Storer;
import one.microstream.storage.configuration.Configuration;
import one.microstream.storage.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.EmbeddedStorageManager;


public final class ReadMeCorp implements HasLogger
{	
	private static ReadMeCorp instance;
	
	public static ReadMeCorp getInstance()
	{
		return instance;
	}

	
	private final AppConfig        appConfig;
	private EmbeddedStorageManager storageManager;
	
	public ReadMeCorp(final AppConfig appConfig)
	{
		super();
		
		this.appConfig = appConfig;
	}
	
	@PostConstruct
	void postConstruct()
	{
		ReadMeCorp.instance = this;
	}
	
	public AppConfig getAppConfig()
	{
		return this.appConfig;
	}
	
	public EmbeddedStorageManager storageManager()
	{
		if(this.storageManager == null)
		{
			this.logger().info("Initializing MicroStream StorageManager");
			
			final Configuration configuration = Configuration.Default();
			configuration.setBaseDirectory(Paths.get(this.appConfig.dataDir(), "microstream").toString());
			configuration.setChannelCount(Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1));
			
			final EmbeddedStorageFoundation<?> foundation = configuration.createEmbeddedStorageFoundation();
			foundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
			this.storageManager = foundation.createEmbeddedStorageManager().start();
			
			if(this.storageManager.root() == null)
			{
				this.logger().info("No data found, initializing random data");
				
				final Data data = Data.New();
				this.storageManager.setRoot(data);
				this.storageManager.storeRoot();
				final DataMetrics metrics = data.populate(
					this.appConfig.initialDataAmount(),
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
