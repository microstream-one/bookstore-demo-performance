
package one.microstream.demo.bookstore;

import javax.annotation.PostConstruct;

import org.rapidpm.dependencies.core.logger.HasLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.microstream.demo.bookstore.jpa.DataMigrator;
import one.microstream.demo.bookstore.jpa.dal.Repositories;


@Component
public class Initializer implements HasLogger
{
	private final BookStoreDemo bookStoreDemo;
	private final Repositories  repositories;

	@Autowired
	public Initializer(
		final BookStoreDemo bookStoreDemo,
		final Repositories  repositories
	)
	{
		super();
		this.bookStoreDemo   = bookStoreDemo;
		this.repositories = repositories;
	}

	@PostConstruct
	public void init()
	{
		this.bookStoreDemo.data();

		if(this.repositories.bookRepository().count() == 0)
		{
			this.migrateData();
		}
	}

	private void migrateData()
	{
		DataMigrator dataMigrator             = null;
		final String jpaDataMigrationStrategy = this.bookStoreDemo.getDemoConfiguration().jpaDataMigrationStrategy();
		switch(jpaDataMigrationStrategy)
		{
			case "batch_insert":

				dataMigrator = DataMigrator.BatchInsert(
					this.bookStoreDemo,
					this.repositories
				);

				break;

			case "sql_file":

				dataMigrator = DataMigrator.SqlFile(
					this.bookStoreDemo,
					this.repositories
				);

				break;
		}

		if(dataMigrator == null)
		{
			throw new IllegalArgumentException(
				"Invalid data migration strategy: " + jpaDataMigrationStrategy
				+ ", valid options are batch_insert or sql_file"
			);
		}

		this.logger().info(
			"JPA database is empty, migrating data from MicroStream, strategy = "
			+ jpaDataMigrationStrategy
		);

		dataMigrator.migrate();

		this.logger().info("Migration finished");
	}

}
