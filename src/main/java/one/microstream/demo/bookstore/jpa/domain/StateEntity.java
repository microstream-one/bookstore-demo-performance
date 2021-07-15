
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
@Table(name = "`STATE`")
public class StateEntity extends NamedEntity
{
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COUNTRY_ID")
	private CountryEntity country;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "state")
	private List<CityEntity> cities = new ArrayList<>();
	
	public StateEntity()
	{
		super();
	}
	
	public StateEntity(
		final String name,
		final CountryEntity country
	)
	{
		super(name);
		this.country = country;
	}
	
	public CountryEntity getCountry()
	{
		return this.country;
	}
	
	public void setCountry(
		final CountryEntity country
	)
	{
		this.country = country;
	}
	
	public List<CityEntity> getCities()
	{
		return this.cities;
	}
	
	public void setCities(
		final List<CityEntity> cities
	)
	{
		this.cities = cities;
	}
	
	public CityEntity addCity(
		final CityEntity city
	)
	{
		this.getCities().add(city);
		city.setState(this);
		return city;
	}
	
	public CityEntity removeCity(
		final CityEntity city
	)
	{
		this.getCities().remove(city);
		city.setState(null);
		return city;
	}
	
}
