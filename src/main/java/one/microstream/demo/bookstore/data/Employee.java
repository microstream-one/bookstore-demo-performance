
package one.microstream.demo.bookstore.data;

/**
 * Employee entity which holds a name and an {@link Address}.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public interface Employee extends NamedWithAddress
{
	/**
	 * Pseudo-constructor method to create a new {@link Employee} instance with default implementation.
	 *
	 * @param name not empty, {@link Named.Validation#validateName(String)}
	 * @param address not <code>null</code>
	 * @return a new {@link Employee} instance
	 */
	public static Employee New(
		final String name,
		final Address address
	)
	{
		return new Default(name, address);
	}


	/**
	 * Default implementation of the {@link Employee} interface.
	 *
	 */
	public static class Default extends NamedWithAddress.Abstract implements Employee
	{
		Default(
			final String name,
			final Address address
		)
		{
			super(name, address);
		}

	}

}
