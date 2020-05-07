
package one.microstream.demo.bookstore.data;

/**
 * Feature type for all named entities, with {@link Comparable} capabilities.
 *
 */
public interface Named extends Comparable<Named>
{
	/**
	 * Get the name of this entity.
	 *
	 * @return the name
	 */
	public String name();

	@Override
	public default int compareTo(final Named other)
	{
		return this.name().compareTo(other.name());
	}


	/**
	 * Abstract implementation of the {@link Named} interface.
	 *
	 */
	public static abstract class Abstract implements Named
	{
		private final String name;

		Abstract(
			final String name
		)
		{
			super();
			this.name = name;
		}

		@Override
		public String name()
		{
			return this.name;
		}

		@Override
		public String toString()
		{
			return this.getClass().getSimpleName() + " [" + this.name + "]";
		}

	}

}
