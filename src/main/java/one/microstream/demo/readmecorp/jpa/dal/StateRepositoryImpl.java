
package one.microstream.demo.readmecorp.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.readmecorp.jpa.domain.StateEntity;


@Repository
public class StateRepositoryImpl extends BaseRepositoryImpl<StateEntity>
{
	public StateRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<StateEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getName());
			ps.setLong(3, entity.getCountry().getId());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO STATE "
			+ "(ID,NAME,COUNTRY_ID) "
			+ "VALUES(?,?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY STATE "
			+ "(ID,NAME,COUNTRY_ID)";
	}
}
