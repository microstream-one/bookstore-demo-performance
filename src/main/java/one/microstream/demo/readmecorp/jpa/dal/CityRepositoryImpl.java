
package one.microstream.demo.readmecorp.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.readmecorp.jpa.domain.CityEntity;


@Repository
public class CityRepositoryImpl extends BaseRepositoryImpl<CityEntity>
{
	public CityRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<CityEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getName());
			ps.setLong(3, entity.getState().getId());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO CITY "
			+ "(ID,NAME,STATE_ID) "
			+ "VALUES(?,?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY CITY "
			+ "(ID,NAME,STATE_ID)";
	}
}
