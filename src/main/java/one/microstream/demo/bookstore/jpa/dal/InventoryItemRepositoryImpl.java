
package one.microstream.demo.bookstore.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.bookstore.jpa.domain.InventoryItemEntity;


@Repository
public class InventoryItemRepositoryImpl extends BaseRepositoryImpl<InventoryItemEntity>
{
	public InventoryItemRepositoryImpl()
	{
		super();
	}
	
	@Transactional
	@Override
	public void batchInsert(
		final Collection<InventoryItemEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setLong(2, entity.getShop().getId());
			ps.setLong(3, entity.getBook().getId());
			ps.setInt(4, entity.getAmount());
		});
	}
	
	@Override
	protected String insertSql()
	{
		return "INSERT INTO INVENTORYITEM "
			+ "(ID,SHOP_ID,BOOK_ID,AMOUNT) "
			+ "VALUES(?,?,?,?)";
	}
	
	@Override
	protected String copySql()
	{
		return "COPY INVENTORYITEM "
			+ "(ID,SHOP_ID,BOOK_ID,AMOUNT)";
	}
}
