
package one.microstream.demo.bookstore.jpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "PURCHASEITEM", indexes = @Index(columnList = "PURCHASE_ID"))
public class PurchaseItemEntity extends BaseEntity implements Serializable
{
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BOOK_ID")
	private BookEntity book;
	
	@Column(name = "AMOUNT")
	private int amount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PURCHASE_ID")
	private PurchaseEntity purchase;
	
	public PurchaseItemEntity()
	{
		super();
	}
	
	public PurchaseItemEntity(
		final BookEntity book,
		final int amount,
		final PurchaseEntity purchase
	)
	{
		super();
		this.book     = book;
		this.amount   = amount;
		this.purchase = purchase;
	}
	
	public BookEntity getBook()
	{
		return this.book;
	}
	
	public void setBook(
		final BookEntity book
	)
	{
		this.book = book;
	}
	
	public int getAmount()
	{
		return this.amount;
	}
	
	public void setAmount(
		final int noname
	)
	{
		this.amount = noname;
	}
	
	public PurchaseEntity getPurchase()
	{
		return this.purchase;
	}
	
	public void setPurchase(
		final PurchaseEntity purchase
	)
	{
		this.purchase = purchase;
	}
	
}
