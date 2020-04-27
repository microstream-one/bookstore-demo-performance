
package one.microstream.demo.bookstore.jpa.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "AUTHOR")
public class AuthorEntity extends NamedWithAddressEntity implements Serializable
{
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
	private List<BookEntity> books = new ArrayList<>();
	
	public AuthorEntity()
	{
		super();
	}
	
	public AuthorEntity(
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
		book.setAuthor(this);
		return book;
	}
	
	public BookEntity removeBook(
		final BookEntity book
	)
	{
		this.getBooks().remove(book);
		book.setAuthor(null);
		return book;
	}
	
}
