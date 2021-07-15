
package one.microstream.demo.bookstore.jpa.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "EMPLOYEE")
public class EmployeeEntity extends NamedWithAddressEntity
{
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
	private List<PurchaseEntity> purchases = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SHOP_ID")
	private ShopEntity shop;
	
	public EmployeeEntity()
	{
		super();
	}
	
	public EmployeeEntity(
		final String name,
		final AddressEntity address,
		final ShopEntity shop
	)
	{
		super(name, address);
		this.shop = shop;
	}
	
	public List<PurchaseEntity> getPurchases()
	{
		return this.purchases;
	}
	
	public void setPurchases(
		final List<PurchaseEntity> purchases
	)
	{
		this.purchases = purchases;
	}
	
	public PurchaseEntity addPurchase(
		final PurchaseEntity purchase
	)
	{
		this.getPurchases().add(purchase);
		purchase.setEmployee(this);
		return purchase;
	}
	
	public PurchaseEntity removePurchase(
		final PurchaseEntity purchase
	)
	{
		this.getPurchases().remove(purchase);
		purchase.setEmployee(null);
		return purchase;
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
