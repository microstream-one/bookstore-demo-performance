
package one.microstream.demo.bookstore.data;

/**
 * View of a book's sale numbers, with {@link Comparable} capabilities.
 * <p>
 * This type is immutable and therefor inherently thread safe.
 *
 */
public class BookSales implements Comparable<BookSales>
{
	private final Book book  ;
	private final int  amount;

	/**
	 * Constructor to create a new {@link BookSales} instance.
	 *
	 * @param book not <code>null</code>
	 * @param amount zero or positive
	 */
	public BookSales(
		final Book book  ,
		final int  amount
	)
	{
		super();
		
		this.book   = book;
		this.amount = amount;
	}
	
	/**
	 * Get the book
	 *
	 * @return the book
	 */
	public Book book()
	{
		return this.book;
	}

	/**
	 * Get the amount
	 *
	 * @return the amount
	 */
	public int amount()
	{
		return this.amount;
	}

	@Override
	public int compareTo(final BookSales other)
	{
		return Integer.compare(other.amount(), this.amount());
	}

	@Override
	public String toString()
	{
		return "BookSales"
			+ " [book="   + this.book
			+ ", amount=" + this.amount
			+ "]";
	}

}
