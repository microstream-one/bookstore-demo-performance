
package one.microstream.demo.readmecorp.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.readmecorp.jpa.domain.CountryEntity;


@Repository
public class CountryRepositoryImpl extends BaseRepositoryImpl<CountryEntity>
{
	public CountryRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<CountryEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getName());
			ps.setString(3, entity.getCode());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO COUNTRY "
			+ "(ID,NAME,CODE) "
			+ "VALUES(?,?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY COUNTRY "
			+ "(ID,NAME,CODE)";
	}
}
