
package one.microstream.demo.bookstore.jpa.dal;

import java.util.List;

import javax.money.MonetaryAmount;

import one.microstream.demo.bookstore.jpa.domain.AuthorEntity;
import one.microstream.demo.bookstore.jpa.domain.BookEntity;
import one.microstream.demo.bookstore.jpa.domain.CountryEntity;


public interface BookRepository extends BaseRepository<BookEntity>
{
	public List<BookEntity> findByAuthor(
		AuthorEntity author
	);

	public List<BookEntity> findByTitleLike(
		String title
	);

	public List<BookEntity> findByTitleContainingIgnoreCaseAndAuthorAddressCityStateCountry(
		String title,
		CountryEntity country
	);

	public List<BookEntity> findByRetailPriceGreaterThanEqualAndRetailPriceLessThan(
		MonetaryAmount minRetailPriceIncl,
		MonetaryAmount maxReatailPriceExcl
	);
}
