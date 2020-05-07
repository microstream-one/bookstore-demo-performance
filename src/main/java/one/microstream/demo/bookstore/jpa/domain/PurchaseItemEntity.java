
package one.microstream.demo.bookstore.jpa.domain;

import java.io.Serializable;

import javax.money.MonetaryAmount;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import one.microstream.demo.bookstore.jpa.MoneyConverter;


@Entity
@Table(name = "PURCHASEITEM", indexes = @Index(columnList = "PURCHASE_ID"))
public class PurchaseItemEntity extends BaseEntity implements Serializable
{
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BOOK_ID")
	private BookEntity book;

	@Column(name = "AMOUNT")
	private int amount;

	@Column(name = "PRICE")
	@Convert(converter = MoneyConverter.class)
	private MonetaryAmount price;

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
		final MonetaryAmount price,
		final PurchaseEntity purchase
	)
	{
		super();
		this.book     = book;
		this.amount   = amount;
		this.price    = price;
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

	public MonetaryAmount getPrice()
	{
		return this.price;
	}

	public void setPrice(
		final MonetaryAmount price
	)
	{
		this.price = price;
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
