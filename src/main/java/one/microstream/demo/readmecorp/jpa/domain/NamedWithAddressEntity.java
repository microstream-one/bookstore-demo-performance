
package one.microstream.demo.readmecorp.jpa.domain;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;


@MappedSuperclass
public abstract class NamedWithAddressEntity extends NamedEntity
{
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "address_id")
	private AddressEntity address;
	
	protected NamedWithAddressEntity()
	{
		super();
	}
	
	protected NamedWithAddressEntity(
		final String name,
		final AddressEntity address
	)
	{
		super(name);
		this.address = address;
	}
	
	public AddressEntity getAddress()
	{
		return this.address;
	}
	
	public void setAddress(
		final AddressEntity address
	)
	{
		this.address = address;
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " [" + this.getName() + " - " + this.address + "]";
	}
	
}
