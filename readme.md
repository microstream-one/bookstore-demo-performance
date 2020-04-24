
# MicroStream Performance Demo

With this demo the superior performance of MicroStream and the Java VM compared to JPA with a connected database is visualized.
It is a Spring Boot application with a Vaadin frontend. Per default Postgres is used, but you can change the application's setup, to use any other database.

## Setup

- First you need to setup a Postgres database. Then the connection settings must be adjusted in the [application.properties](src/main/resources/application.properties) file accordingly.

- Start the program via its main class: [one.microstream.demo.readmecorp.Application](src/main/java/one/microstream/demo/readmecorp/Application.java)

- At the first start a new MicroStream database is generated with random data. Depending on the setting `readmecorp.initialDataAmount` more or less data is generated.

- Then the generated data has to be migrated to the connected JPA database
  
  - If `readmecorp.jpaDataMigrationStrategy` is `batch_insert` (default), the data will be automatically migrated.
 
  - If it is set to `sql_file` the generated data will be written into a SQL file, which must then be imported into the JPA database.
  It can be found in the configured data directory `readmecorp.dataDir`, default is [userhome]/readmecorp.
  Please note, that this is by far the faster way, but the generated inserts are optimized for Postgres and probably won't work with a different database.
  
Here you can see the entity relationship diagram of the generated database:
![ERD](readme-assets/erd.png)

## Usage

After the application has started, open the client interface at: [http://localhost](http://localhost).

![Screenshot](readme-assets/screenshot.png)