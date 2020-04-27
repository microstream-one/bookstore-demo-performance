
package one.microstream.demo.bookstore.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.bookstore.jpa.domain.EmployeeEntity;


@Repository
public class EmployeeRepositoryImpl extends BaseRepositoryImpl<EmployeeEntity>
{
	public EmployeeRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<EmployeeEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getName());
			ps.setLong(3, entity.getAddress().getId());
			ps.setLong(4, entity.getShop().getId());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO EMPLOYEE "
			+ "(ID,NAME,ADDRESS_ID,SHOP_ID) "
			+ "VALUES(?,?,?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY EMPLOYEE "
			+ "(ID,NAME,ADDRESS_ID,SHOP_ID)";
	}
}
