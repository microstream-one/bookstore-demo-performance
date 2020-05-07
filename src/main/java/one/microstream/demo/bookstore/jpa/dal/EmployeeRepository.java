
package one.microstream.demo.bookstore.jpa.dal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import one.microstream.demo.bookstore.jpa.domain.EmployeeEntity;


public interface EmployeeRepository extends NamedWithAddressRepository<EmployeeEntity>
{
	@Query(nativeQuery = true, value =
		"select * from employee e where e.id=(" +
		"  select p.employee_id " +
		"  from purchaseitem i " +
		"  cross join purchase p " +
		"  where i.purchase_id=p.id " +
		"    and extract(YEAR FROM p.\"timestamp\")=:year " +
		"  group by p.employee_id " +
		"  order by SUM(i.amount*i.price) DESC" +
		"  limit 1" +
		")"
	)
	public EmployeeEntity employeeOfTheYear(
		@Param("year") int year
	);


	@Query(nativeQuery = true, value =
		"select * from employee e where e.id=(" +
		"  select p.employee_id " +
		"  from purchaseitem i " +
		"  cross join purchase p " +
		"  left outer join shop s on p.shop_id=s.id " +
		"  left outer join address a on s.address_id=a.id " +
		"  left outer join city c on a.city_id=c.id " +
		"  left outer join \"state\" st on c.state_id=st.id " +
		"  where st.country_id=:countryId " +
		"    and i.purchase_id=p.id " +
		"    and extract(YEAR FROM p.\"timestamp\")=:year " +
		"  group by p.employee_id " +
		"  order by SUM(i.amount*i.price) DESC" +
		"  limit 1" +
		")"
	)
	public EmployeeEntity employeeOfTheYear(
		@Param("year") int yearl,
		@Param("countryId") long countryId
	);
}
