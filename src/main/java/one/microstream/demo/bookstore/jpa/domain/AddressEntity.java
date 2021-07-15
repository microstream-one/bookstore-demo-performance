
package one.microstream.demo.bookstore.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "ADDRESS")
public class AddressEntity extends BaseEntity
{
	@Column(name = "ADDRESS")
	private String address;
	
	@Column(name = "ADDRESS2")
	private String address2;
	
	@Column(name = "ZIPCODE")
	private String zipCode;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CITY_ID")
	private CityEntity city;
	
	public AddressEntity()
	{
		super();
	}
	
	public AddressEntity(
		final String address,
		final String address2,
		final String zipCode,
		final CityEntity city
	)
	{
		super();
		this.address  = address;
		this.address2 = address2;
		this.zipCode  = zipCode;
		this.city     = city;
	}
	
	public String getAddress()
	{
		return this.address;
	}
	
	public void setAddress(
		final String noname
	)
	{
		this.address = noname;
	}
	
	public String getAddress2()
	{
		return this.address2;
	}
	
	public void setAddress2(
		final String noname
	)
	{
		this.address2 = noname;
	}
	
	public String getZipCode()
	{
		return this.zipCode;
	}
	
	public void setZipCode(
		final String noname
	)
	{
		this.zipCode = noname;
	}
	
	public CityEntity getCity()
	{
		return this.city;
	}
	
	public void setCity(
		final CityEntity city
	)
	{
		this.city = city;
	}
	
	@Override
	public String toString()
	{
		return "AddressEntity [address=" + this.address + ", address2=" + this.address2 + ", zipCode=" + this.zipCode
			+ ", city=" + this.city + "]";
	}
	
}
