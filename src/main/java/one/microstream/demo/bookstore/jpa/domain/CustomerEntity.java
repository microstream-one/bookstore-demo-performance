
package one.microstream.demo.bookstore.jpa.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "CUSTOMER")
public class CustomerEntity extends NamedWithAddressEntity
{
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
	private List<PurchaseEntity> purchases = new ArrayList<>();
	
	public CustomerEntity()
	{
		super();
	}
	
	public CustomerEntity(
		final String name,
		final AddressEntity address
	)
	{
		super(name, address);
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
		purchase.setCustomer(this);
		return purchase;
	}
	
	public PurchaseEntity removePurchase(
		final PurchaseEntity purchase
	)
	{
		this.getPurchases().remove(purchase);
		purchase.setCustomer(null);
		return purchase;
	}
	
}
