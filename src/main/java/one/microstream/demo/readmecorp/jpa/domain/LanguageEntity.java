
package one.microstream.demo.readmecorp.jpa.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "`LANGUAGE`")
public class LanguageEntity extends BaseEntity implements Serializable
{
	@Column(name = "LANGUAGETAG")
	private String languageTag;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "language")
	private List<BookEntity> books = new ArrayList<>();
	
	public LanguageEntity()
	{
		super();
	}
	
	public LanguageEntity(
		final String languageTag
	)
	{
		super();
		this.languageTag = languageTag;
	}
	
	public String getLanguageTag()
	{
		return this.languageTag;
	}
	
	public void setLanguageTag(
		final String id
	)
	{
		this.languageTag = id;
	}
	
	public Locale toLocale()
	{
		return Locale.forLanguageTag(this.languageTag);
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
		book.setLanguage(this);
		return book;
	}
	
	public BookEntity removeBook(
		final BookEntity book
	)
	{
		this.getBooks().remove(book);
		book.setLanguage(null);
		return book;
	}
}
