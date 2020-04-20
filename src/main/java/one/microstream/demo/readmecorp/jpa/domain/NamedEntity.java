
package one.microstream.demo.readmecorp.jpa.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class NamedEntity extends BaseEntity
{
	@Column(name = "`NAME`")
	private String name;
	
	protected NamedEntity()
	{
		super();
	}
	
	protected NamedEntity(
		final String name
	)
	{
		super();
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(
		final String name
	)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " [" + this.name + "]";
	}
	
}
