
package one.microstream.demo.readmecorp.jpa.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "COUNTRY")
public class CountryEntity extends NamedWithCodeEntity implements Serializable
{
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
	private List<StateEntity> states = new ArrayList<>();
	
	public CountryEntity()
	{
		super();
	}
	
	public CountryEntity(
		final String name,
		final String code
	)
	{
		super(name, code);
	}
	
	public List<StateEntity> getStates()
	{
		return this.states;
	}
	
	public void setStates(
		final List<StateEntity> states
	)
	{
		this.states = states;
	}
	
	public StateEntity addState(
		final StateEntity state
	)
	{
		this.getStates().add(state);
		state.setCountry(this);
		return state;
	}
	
	public StateEntity removeState(
		final StateEntity state
	)
	{
		this.getStates().remove(state);
		state.setCountry(null);
		return state;
	}
}
