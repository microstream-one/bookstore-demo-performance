
package one.microstream.demo.bookstore.jpa.dal;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.bookstore.jpa.domain.PurchaseEntity;


@Repository
public class PurchaseRepositoryImpl extends BaseRepositoryImpl<PurchaseEntity>
{
	public PurchaseRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<PurchaseEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setLong(2, entity.getEmployee().getId());
			ps.setLong(3, entity.getCustomer().getId());
			ps.setTimestamp(4, Timestamp.from(entity.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
			ps.setLong(5, entity.getShop().getId());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO PURCHASE "
			+ "(ID,EMPLOYEE_ID,CUSTOMER_ID,TIMESTAMP,SHOP_ID) "
			+ "VALUES(?,?,?,?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY PURCHASE "
			+ "(ID,EMPLOYEE_ID,CUSTOMER_ID,TIMESTAMP,SHOP_ID)";
	}
}
