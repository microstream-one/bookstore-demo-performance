
package one.microstream.demo.bookstore.jpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "BOOK")
public class BookEntity extends BaseEntity implements Serializable
{
	@Column(name = "ISBN13")
	private String isbn13;
	
	@Column(name = "TITLE")
	private String title;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AUTHOR_ID")
	private AuthorEntity author;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GENRE_ID")
	private GenreEntity genre;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PUBLISHER_ID")
	private PublisherEntity publisher;
	
	@Column(name = "PRICE")
	private double price;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LANGUAGE_ID")
	private LanguageEntity language;
	
	public BookEntity()
	{
		super();
	}
	
	public BookEntity(
		final String isbn13,
		final String title,
		final AuthorEntity author,
		final GenreEntity genre,
		final PublisherEntity publisher,
		final double price,
		final LanguageEntity language
	)
	{
		super();
		this.isbn13    = isbn13;
		this.title     = title;
		this.author    = author;
		this.genre     = genre;
		this.publisher = publisher;
		this.price     = price;
		this.language  = language;
	}
	
	public String getIsbn13()
	{
		return this.isbn13;
	}
	
	public void setIsbn13(
		final String isbn13
	)
	{
		this.isbn13 = isbn13;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public void setTitle(
		final String noname
	)
	{
		this.title = noname;
	}
	
	public AuthorEntity getAuthor()
	{
		return this.author;
	}
	
	public void setAuthor(
		final AuthorEntity author
	)
	{
		this.author = author;
	}
	
	public GenreEntity getGenre()
	{
		return this.genre;
	}
	
	public void setGenre(
		final GenreEntity genre
	)
	{
		this.genre = genre;
	}
	
	public PublisherEntity getPublisher()
	{
		return this.publisher;
	}
	
	public void setPublisher(
		final PublisherEntity publisher
	)
	{
		this.publisher = publisher;
	}
	
	public double getPrice()
	{
		return this.price;
	}
	
	public void setPrice(
		final double noname
	)
	{
		this.price = noname;
	}
	
	public LanguageEntity getLanguage()
	{
		return this.language;
	}
	
	public void setLanguage(
		final LanguageEntity language
	)
	{
		this.language = language;
	}
	
	@Override
	public String toString()
	{
		return "BookEntity [title=" + this.title + ", author=" + this.author + ", genre=" + this.genre + ", publisher="
			+ this.publisher + ", language=" + this.language + ", price=" + this.price + "]";
	}
	
}
