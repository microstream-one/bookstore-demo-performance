
package one.microstream.demo.bookstore.jpa.dal;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import one.microstream.demo.bookstore.jpa.domain.BookEntity;


@Repository
public class BookRepositoryImpl extends BaseRepositoryImpl<BookEntity>
{
	public BookRepositoryImpl()
	{
		super();
	}

	@Transactional
	@Override
	public void batchInsert(
		final Collection<BookEntity> entities
	)
	{
		this.batchInsert(entities, (ps, entity) -> {
			ps.setLong(1, entity.getId());
			ps.setString(2, entity.getIsbn13());
			ps.setDouble(3, entity.getPurchasePrice().getNumber().doubleValue());
			ps.setDouble(4, entity.getRetailPrice().getNumber().doubleValue());
			ps.setString(5, entity.getTitle());
			ps.setLong(6, entity.getAuthor().getId());
			ps.setLong(7, entity.getGenre().getId());
			ps.setLong(8, entity.getLanguage().getId());
			ps.setLong(9, entity.getPublisher().getId());
		});
	}

	@Override
	protected String insertSql()
	{
		return "INSERT INTO BOOK "
			+ "(ID,ISBN13,PURCHASE_PRICE,RETAIL_PRICE,TITLE,AUTHOR_ID,GENRE_ID,LANGUAGE_ID,PUBLISHER_ID) "
			+ "VALUES(?,?,?,?,?,?,?,?,?)";
	}

	@Override
	protected String copySql()
	{
		return "COPY BOOK "
			+ "(ID,ISBN13,PURCHASE_PRICE,RETAIL_PRICE,TITLE,AUTHOR_ID,GENRE_ID,LANGUAGE_ID,PUBLISHER_ID)";
	}
}
