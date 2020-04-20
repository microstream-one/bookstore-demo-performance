
package one.microstream.demo.readmecorp.jpa.dal;

import java.util.List;

import one.microstream.demo.readmecorp.jpa.domain.AuthorEntity;
import one.microstream.demo.readmecorp.jpa.domain.BookEntity;
import one.microstream.demo.readmecorp.jpa.domain.CountryEntity;


public interface BookRepository extends BaseRepository<BookEntity>
{
	public List<BookEntity> findByAuthor(
		AuthorEntity author
	);
	
	public List<BookEntity> findByTitleLike(
		String title
	);
	
	public List<BookEntity> findByTitleLikeAndAuthorAddressCityStateCountry(
		String title,
		CountryEntity country
	);
	
	public List<BookEntity> findByPriceGreaterThanEqualAndPriceLessThan(double minPriceIncl, double maxPriceExcl);
}
