
package one.microstream.demo.bookstore.data;


/**
 * Customer entity which holds a customer id, name and an {@link Address}.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public interface Customer extends NamedWithAddress
{
	/**
	 * Get the customer id
	 *
	 * @return the customer id
	 */
	public int customerId();


	/**
	 * Pseudo-constructor method to create a new {@link Customer} instance with default implementation.
	 *
	 * @param customerId positive customer id
	 * @param name not empty, {@link Named.Validation#validateName(String)}
	 * @param address not <code>null</code>
	 * @return a new {@link Customer} instance
	 */
	public static Customer New(
		final int customerId,
		final String name,
		final Address address
	)
	{
		return new Default(customerId, name, address);
	}


	/**
	 * Default implementation of the {@link Customer} interface.
	 *
	 */
	public static class Default extends NamedWithAddress.Abstract implements Customer
	{
		private final int customerId;

		Default(
			final int customerId,
			final String name,
			final Address address
		)
		{
			super(name, address);
			this.customerId = customerId;
		}

		@Override
		public int customerId()
		{
			return this.customerId;
		}

	}

}
