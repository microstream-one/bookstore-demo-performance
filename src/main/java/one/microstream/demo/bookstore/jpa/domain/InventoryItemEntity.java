
package one.microstream.demo.bookstore.jpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "INVENTORYITEM")
public class InventoryItemEntity extends BaseEntity implements Serializable
{
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BOOK_ID")
	private BookEntity book;
	
	@Column(name = "AMOUNT")
	private int amount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SHOP_ID")
	private ShopEntity shop;
	
	public InventoryItemEntity()
	{
		super();
	}
	
	public InventoryItemEntity(
		final BookEntity book,
		final int amount,
		final ShopEntity shop
	)
	{
		super();
		this.book   = book;
		this.amount = amount;
		this.shop   = shop;
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
	
	public ShopEntity getShop()
	{
		return this.shop;
	}
	
	public void setShop(
		final ShopEntity shop
	)
	{
		this.shop = shop;
	}
	
}
