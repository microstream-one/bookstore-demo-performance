
package one.microstream.demo.bookstore.jpa.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "GENRE")
public class GenreEntity extends NamedEntity implements Serializable
{
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "genre")
	private List<BookEntity> books = new ArrayList<>();
	
	public GenreEntity()
	{
		super();
	}
	
	public GenreEntity(
		final String name
	)
	{
		super(name);
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
		book.setGenre(this);
		return book;
	}
	
	public BookEntity removeBook(
		final BookEntity book
	)
	{
		this.getBooks().remove(book);
		book.setGenre(null);
		return book;
	}
	
}
