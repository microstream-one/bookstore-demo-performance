
package one.microstream.demo.bookstore.data;

import javax.money.MonetaryAmount;

/**
 * Book entity which holds an ISBN-13, title, {@link Author}, {@link Genre}, {@link Publisher},
 * {@link Language} and the purchase and retail price.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public interface Book extends Named
{
	/**
	 * Gets the title of this book. This is a synonym method to get compatibility to the {@link Named} interface.
	 */
	@Override
	default String name()
	{
		return this.title();
	}

	/**
	 * Get the ISBN-13.
	 *
	 * @return the ISBN-13
	 */
	public String isbn13();

	/**
	 * Get the title.
	 *
	 * @return the title
	 */
	public String title();

	/**
	 * Get the author.
	 *
	 * @return the author
	 */
	public Author author();

	/**
	 * Get the genre.
	 *
	 * @return the genre
	 */
	public Genre genre();

	/**
	 * Get the publisher.
	 *
	 * @return the publisher
	 */
	public Publisher publisher();

	/**
	 * Get the language.
	 *
	 * @return the language
	 */
	public Language language();

	/**
	 * Get the purchase price.
	 *
	 * @return the purchase price
	 */
	public MonetaryAmount purchasePrice();

	/**
	 * Get the retail price.
	 *
	 * @return the retail price
	 */
	public MonetaryAmount retailPrice();


	/**
	 * Pseudo-constructor method to create a new {@link Book} instance with default implementation.
	 *
	 * @param isbn13 a valid ISBN-13, see {@link Validation#validateIsbn13(String)}
	 * @param title not empty, see {@link Validation#validateTitle(String)}
	 * @param author not <code>null</code>, see {@link Validation#validateAuthor(Author)}
	 * @param genre not <code>null</code>, see {@link Validation#validateGenre(Genre)}
	 * @param publisher not <code>null</code>, see {@link Validation#validatePublisher(Publisher)}
	 * @param language not <code>null</code>, see {@link Validation#validateLanguage(Language)}
	 * @param purchasePrice valid price, see {@link Validation#validatePrice(MonetaryAmount)}
	 * @param retailPrice valid price, see {@link Validation#validatePrice(MonetaryAmount)}
	 * @return
	 */
	public static Book New(
		final String isbn13,
		final String title,
		final Author author,
		final Genre genre,
		final Publisher publisher,
		final Language language,
		final MonetaryAmount purchasePrice,
		final MonetaryAmount retailPrice
	)
	{
		return new Default(isbn13, title, author, genre, publisher, language, purchasePrice, retailPrice);
	}


	/**
	 * Default implementation of the {@link Book} interface.
	 *
	 */
	public static class Default implements Book
	{
		private final String         isbn13;
		private final String         title;
		private final Author         author;
		private final Genre          genre;
		private final Publisher      publisher;
		private final Language       language;
		private final MonetaryAmount purchasePrice;
		private final MonetaryAmount retailPrice;

		Default(
			final String isbn13,
			final String title,
			final Author author,
			final Genre genre,
			final Publisher publisher,
			final Language language,
			final MonetaryAmount purchasePrice,
			final MonetaryAmount retailPrice
		)
		{
			super();
			this.isbn13        = isbn13;
			this.title         = title;
			this.author        = author;
			this.genre         = genre;
			this.publisher     = publisher;
			this.language      = language;
			this.purchasePrice = purchasePrice;
			this.retailPrice   = retailPrice;
		}

		@Override
		public String isbn13()
		{
			return this.isbn13;
		}

		@Override
		public String title()
		{
			return this.title;
		}

		@Override
		public Author author()
		{
			return this.author;
		}

		@Override
		public Genre genre()
		{
			return this.genre;
		}

		@Override
		public Publisher publisher()
		{
			return this.publisher;
		}

		@Override
		public Language language()
		{
			return this.language;
		}

		@Override
		public MonetaryAmount purchasePrice()
		{
			return this.purchasePrice;
		}

		@Override
		public MonetaryAmount retailPrice()
		{
			return this.retailPrice;
		}

		@Override
		public String toString()
		{
			return "Book [isbn13="
				+ this.isbn13
				+ ", title="
				+ this.title
				+ ", author="
				+ this.author
				+ ", genre="
				+ this.genre
				+ ", publisher="
				+ this.publisher
				+ ", language="
				+ this.language
				+ ", purchasePrice="
				+ this.purchasePrice
				+ ", retailPrice="
				+ this.retailPrice
				+ "]";
		}

	}

}
