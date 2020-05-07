
package one.microstream.demo.bookstore.data;

/**
 * Genre entity which holds a name.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public interface Genre extends Named
{
	/**
	 * Pseudo-constructor method to create a new {@link Genre} instance with default implementation.
	 *
	 * @param name not empty, {@link Named.Validation#validateName(String)}
	 * @return a new {@link Genre} instance
	 */
	public static Genre New(
		final String name
	)
	{
		return new Default(name);
	}


	/**
	 * Default implementation of the {@link Genre} interface.
	 *
	 */
	public static class Default extends Named.Abstract implements Genre
	{
		Default(
			final String name
		)
		{
			super(name);
		}

	}

}
