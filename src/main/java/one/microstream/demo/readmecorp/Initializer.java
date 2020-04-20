
package one.microstream.demo.readmecorp;

import javax.annotation.PostConstruct;

import org.rapidpm.dependencies.core.logger.HasLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.microstream.demo.readmecorp.jpa.DataMigrator;
import one.microstream.demo.readmecorp.jpa.dal.Repositories;


@Component
public class Initializer implements HasLogger
{
	private final ReadMeCorp   readMeCorp;
	private final Repositories repositories;
	
	@Autowired
	public Initializer(
		final ReadMeCorp readMeCorp,
		final Repositories repositories
	)
	{
		super();
		this.readMeCorp   = readMeCorp;
		this.repositories = repositories;
	}
	
	@PostConstruct
	public void init()
	{
		this.readMeCorp.data();
		
		if(this.repositories.bookRepository().count() == 0)
		{
			this.logger().info("JPA database is empty, migrating data from MicroStream");
			
			DataMigrator.FromMicroStream(
				this.readMeCorp,
				this.repositories)
			.migrate();
			
			this.logger().info("Migration finished");
		}
	}
}
