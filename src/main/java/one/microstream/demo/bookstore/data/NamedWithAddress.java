
package one.microstream.demo.bookstore.data;

/**
 * Feature type for all named entities with an {@link Address}.
 *
 */
public interface NamedWithAddress extends Named
{
	/**
	 * Get the address of this entity.
	 *
	 * @return the address
	 */
	public Address address();


	/**
	 * Abstract implementation of the {@link NamedWithAddress} interface.
	 *
	 */
	public static class Abstract extends Named.Abstract implements NamedWithAddress
	{
		private final Address address;

		Abstract(
			final String name,
			final Address address
		)
		{
			super(name);
			this.address = address;
		}

		@Override
		public Address address()
		{
			return this.address;
		}

		@Override
		public String toString()
		{
			return this.getClass().getSimpleName() + " [" + this.name() + " - " + this.address + "]";
		}

	}

}
