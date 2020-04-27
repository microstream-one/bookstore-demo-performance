
package one.microstream.demo.bookstore.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.bookstore.jpa.domain.LanguageEntity;


@Repository
public class LanguageRepositoryImpl extends BaseRepositoryImpl<LanguageEntity>
{
	public LanguageRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<LanguageEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getLanguageTag());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO LANGUAGE "
			+ "(ID,LANGUAGETAG) "
			+ "VALUES(?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY LANGUAGE "
			+ "(ID,LANGUAGETAG)";
	}
}
