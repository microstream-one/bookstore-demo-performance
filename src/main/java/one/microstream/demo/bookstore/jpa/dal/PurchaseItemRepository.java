
package one.microstream.demo.bookstore.jpa.dal;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import one.microstream.demo.bookstore.jpa.domain.CountryEntity;
import one.microstream.demo.bookstore.jpa.domain.PurchaseItemEntity;
import one.microstream.demo.bookstore.jpa.domain.ShopEntity;


public interface PurchaseItemRepository extends BaseRepository<PurchaseItemEntity>
{
	@Query(
		"SELECT new one.microstream.demo.bookstore.jpa.dal.BookSales(" +
		"  p.book, " +
		"  COUNT(p.amount) as bookAmount" +
		") " +
        "FROM  PurchaseItemEntity p " +
        "GROUP BY p.book " +
        "ORDER BY bookAmount DESC"
    )
	public List<BookSales> bestSellerList();


	@Query(
		"SELECT new one.microstream.demo.bookstore.jpa.dal.BookSales(" +
		"  p.book, " +
		"  COUNT(p.amount) as bookAmount" +
		") " +
        "FROM  PurchaseItemEntity p " +
		"WHERE EXTRACT(YEAR FROM p.purchase.timestamp) = :year " +
        "GROUP BY p.book " +
        "ORDER BY bookAmount DESC"
    )
	public List<BookSales> bestSellerList(
		@Param("year") int year
	);


	@Query(
		"SELECT new one.microstream.demo.bookstore.jpa.dal.BookSales(" +
		"  p.book, " +
		"  COUNT(p.amount) as bookAmount" +
		") " +
        "FROM  PurchaseItemEntity p " +
		"WHERE EXTRACT(YEAR FROM p.purchase.timestamp) = :year " +
        "  AND p.purchase.shop.address.city.state.country = :country " +
        "GROUP BY p.book " +
        "ORDER BY bookAmount DESC"
    )
	public List<BookSales> bestSellerList(
		@Param("year") int year,
		@Param("country") CountryEntity country
	);


	@Query(
		"SELECT SUM(p.amount * p.price) " +
		"FROM PurchaseItemEntity p " +
		"WHERE p.purchase.shop = :shop " +
		"  AND EXTRACT(YEAR FROM p.purchase.timestamp) = :year"
	)
	public Double revenueOfShopInYear(
		@Param("shop") ShopEntity shop,
		@Param("year") int year
	);

}
