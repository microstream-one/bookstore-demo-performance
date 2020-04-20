
package one.microstream.demo.readmecorp.jpa.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class NamedWithCodeEntity extends NamedEntity
{
	@Column(name = "CODE")
	private String code;
	
	protected NamedWithCodeEntity()
	{
		super();
	}
	
	protected NamedWithCodeEntity(
		final String name,
		final String code
	)
	{
		super(name);
		this.code = code;
	}
	
	public String getCode()
	{
		return this.code;
	}
	
	public void setCode(
		final String code
	)
	{
		this.code = code;
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " [" + this.getName() + " - " + this.code + "]";
	}
	
}
