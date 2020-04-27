
package one.microstream.demo.bookstore.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.bookstore.jpa.domain.AddressEntity;


@Repository
public class AddressRepositoryImpl extends BaseRepositoryImpl<AddressEntity>
{
	public AddressRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<AddressEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getAddress());
			ps.setString(3, entity.getAddress2());
			ps.setString(4, entity.getZipCode());
			ps.setLong(5, entity.getCity().getId());
		});
	}

	@Override
	protected String insertSql()
	{
		return "INSERT INTO ADDRESS "
			+ "(ID,ADDRESS,ADDRESS2,ZIPCODE,CITY_ID) "
			+ "VALUES(?,?,?,?,?)";
	}

	@Override
	protected String copySql()
	{
		return "COPY ADDRESS "
			+ "(ID,ADDRESS,ADDRESS2,ZIPCODE,CITY_ID)";
	}
}
