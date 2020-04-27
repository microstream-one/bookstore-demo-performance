
package one.microstream.demo.bookstore.jpa.dal;

import one.microstream.demo.bookstore.jpa.domain.BookEntity;


public class BookSales implements Comparable<BookSales>
{
	private final BookEntity book;
	private final long  amount;
	
	public BookSales(
		final BookEntity book,
		final long amount
	)
	{
		super();
		this.book   = book;
		this.amount = amount;
	}
	
	public BookEntity book()
	{
		return this.book;
	}
	
	public long amount()
	{
		return this.amount;
	}
	
	@Override
	public int compareTo(
		final BookSales other
	)
	{
		return Long.compare(other.amount(), this.amount);
	}
	
	@Override
	public String toString()
	{
		return "Default [book=" + this.book + ", amount=" + this.amount + "]";
	}
	
}
