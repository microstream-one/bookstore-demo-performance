
package one.microstream.demo.bookstore.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "CITY")
public class CityEntity extends NamedEntity
{
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "STATE_ID")
	private StateEntity state;
	
	public CityEntity()
	{
		super();
	}
	
	public CityEntity(
		final String name,
		final StateEntity state
	)
	{
		super(name);
		this.state = state;
	}
	
	public StateEntity getState()
	{
		return this.state;
	}
	
	public void setState(
		final StateEntity state
	)
	{
		this.state = state;
	}
	
}
