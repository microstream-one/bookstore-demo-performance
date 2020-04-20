
package one.microstream.demo.readmecorp.jpa.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "PURCHASE", indexes = {
	@Index(columnList = "EMPLOYEE_ID"),
	@Index(columnList = "CUSTOMER_ID"),
	@Index(columnList = "SHOP_ID"),
	@Index(columnList = "`TIMESTAMP`")
})
public class PurchaseEntity extends BaseEntity implements Serializable
{
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMPLOYEE_ID")
	private EmployeeEntity employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID")
	private CustomerEntity customer;
	
	@Column(name = "`TIMESTAMP`")
	private LocalDateTime timestamp;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase")
	private List<PurchaseItemEntity> items = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SHOP_ID")
	private ShopEntity shop;
	
	public PurchaseEntity()
	{
		super();
	}
	
	public PurchaseEntity(
		final EmployeeEntity employee,
		final CustomerEntity customer,
		final LocalDateTime timestamp,
		final ShopEntity shop
	)
	{
		super();
		this.employee  = employee;
		this.customer  = customer;
		this.timestamp = timestamp;
		this.shop      = shop;
	}
	
	public EmployeeEntity getEmployee()
	{
		return this.employee;
	}
	
	public void setEmployee(
		final EmployeeEntity employee
	)
	{
		this.employee = employee;
	}
	
	public CustomerEntity getCustomer()
	{
		return this.customer;
	}
	
	public void setCustomer(
		final CustomerEntity customer
	)
	{
		this.customer = customer;
	}
	
	public LocalDateTime getTimestamp()
	{
		return this.timestamp;
	}
	
	public void setTimestamp(
		final LocalDateTime noname
	)
	{
		this.timestamp = noname;
	}
	
	public List<PurchaseItemEntity> getItems()
	{
		return this.items;
	}
	
	public void setItems(
		final List<PurchaseItemEntity> items
	)
	{
		this.items = items;
	}
	
	public PurchaseItemEntity addItem(
		final PurchaseItemEntity item
	)
	{
		this.getItems().add(item);
		item.setPurchase(this);
		return item;
	}
	
	public PurchaseItemEntity removeItem(
		final PurchaseItemEntity item
	)
	{
		this.getItems().remove(item);
		item.setPurchase(null);
		return item;
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
