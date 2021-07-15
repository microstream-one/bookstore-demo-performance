
package one.microstream.demo.bookstore.jpa.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "PUBLISHER")
public class PublisherEntity extends NamedWithAddressEntity
{
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "publisher")
	private List<BookEntity> books = new ArrayList<>();
	
	public PublisherEntity()
	{
		super();
	}
	
	public PublisherEntity(
		final String name,
		final AddressEntity address
	)
	{
		super(name, address);
	}
	
	public List<BookEntity> getBooks()
	{
		return this.books;
	}
	
	public void setBooks(
		final List<BookEntity> books
	)
	{
		this.books = books;
	}
	
	public BookEntity addBook(
		final BookEntity book
	)
	{
		this.getBooks().add(book);
		book.setPublisher(this);
		return book;
	}
	
	public BookEntity removeBook(
		final BookEntity book
	)
	{
		this.getBooks().remove(book);
		book.setPublisher(null);
		return book;
	}
	
}
