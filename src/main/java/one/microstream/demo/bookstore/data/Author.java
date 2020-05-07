
package one.microstream.demo.bookstore.data;

/**
 * Author entity which holds a name and an {@link Address}.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public interface Author extends NamedWithAddress
{
	/**
	 * Pseudo-constructor method to create a new {@link Author} instance with default implementation.
	 *
	 * @param name not empty, {@link Named.Validation#validateName(String)}
	 * @param address not <code>null</code>, {@link NamedWithAddress.Validation#validateAddress(Address)}
	 * @return a new {@link Author} instance
	 */
	public static Author New(
		final String name,
		final Address address
	)
	{
		return new Default(name, address);
	}


	/**
	 * Default implementation of the {@link Author} interface.
	 *
	 */
	public static class Default extends NamedWithAddress.Abstract implements Author
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
