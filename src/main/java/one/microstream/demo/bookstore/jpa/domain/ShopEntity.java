
package one.microstream.demo.bookstore.jpa.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "SHOP")
public class ShopEntity extends NamedWithAddressEntity implements Serializable
{
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "shop")
	private List<EmployeeEntity> employees = new ArrayList<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "shop")
	private List<PurchaseEntity> purchases = new ArrayList<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "shop")
	private List<InventoryItemEntity> inventoryItems = new ArrayList<>();
	
	public ShopEntity()
	{
		super();
	}
	
	public ShopEntity(
		final String name,
		final AddressEntity address
	)
	{
		super(name, address);
	}
	
	public List<EmployeeEntity> getEmployees()
	{
		return this.employees;
	}
	
	public void setEmployees(
		final List<EmployeeEntity> employees
	)
	{
		this.employees = employees;
	}
	
	public EmployeeEntity addEmployee(
		final EmployeeEntity employee
	)
	{
		this.getEmployees().add(employee);
		employee.setShop(this);
		return employee;
	}
	
	public EmployeeEntity removeEmployee(
		final EmployeeEntity employee
	)
	{
		this.getEmployees().remove(employee);
		employee.setShop(null);
		return employee;
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
		purchase.setShop(this);
		return purchase;
	}
	
	public PurchaseEntity removePurchase(
		final PurchaseEntity purchase
	)
	{
		this.getPurchases().remove(purchase);
		purchase.setShop(null);
		return purchase;
	}
	
	public List<InventoryItemEntity> getInventoryItems()
	{
		return this.inventoryItems;
	}
	
	public void setInventoryItems(
		final List<InventoryItemEntity> inventoryItems
	)
	{
		this.inventoryItems = inventoryItems;
	}
	
	public InventoryItemEntity addInventoryItem(
		final InventoryItemEntity inventoryItem
	)
	{
		this.getInventoryItems().add(inventoryItem);
		inventoryItem.setShop(this);
		return inventoryItem;
	}
	
	public InventoryItemEntity removeInventoryItem(
		final InventoryItemEntity inventoryItem
	)
	{
		this.getInventoryItems().remove(inventoryItem);
		inventoryItem.setShop(null);
		return inventoryItem;
	}
	
}
