
package one.microstream.demo.readmecorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer
{
	
	public static void main(
		final String[] args
	)
	{
		SpringApplication.run(Application.class, args);
	}
	
}
