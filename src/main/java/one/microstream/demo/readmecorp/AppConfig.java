package one.microstream.demo.readmecorp;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;

import one.microstream.demo.readmecorp.data.RandomDataAmount;

public class AppConfig
{
	@Value("${readmecorp.dataDir}")
	private String dataDir;

	@Value("${readmecorp.initialDataAmount:medium}")
	private String initialDataAmountConfiguration;
	
	public AppConfig()
	{
		super();
	}
	
	public String dataDir()
	{
		final String dir = this.dataDir;
		return dir.startsWith("~/") || dir.startsWith("~\\")
			? Paths.get(System.getProperty("user.home"), dir.substring(2)).toString()
			: dir;
	}
	
	public RandomDataAmount initialDataAmount()
	{
		return RandomDataAmount.valueOf(this.initialDataAmountConfiguration);
	}
}
