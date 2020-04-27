
package one.microstream.demo.bookstore.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.bookstore.jpa.domain.CustomerEntity;


@Repository
public class CustomerRepositoryImpl extends BaseRepositoryImpl<CustomerEntity>
{
	public CustomerRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<CustomerEntity> entities
	)
	{
		this.batchInsert(entities,  (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getName());
			ps.setLong(3, entity.getAddress().getId());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO CUSTOMER "
			+ "(ID,NAME,ADDRESS_ID) "
			+ "VALUES(?,?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY CUSTOMER "
			+ "(ID,NAME,ADDRESS_ID)";
	}
}
