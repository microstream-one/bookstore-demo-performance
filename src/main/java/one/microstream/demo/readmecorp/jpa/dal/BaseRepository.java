
package one.microstream.demo.readmecorp.jpa.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import one.microstream.demo.readmecorp.jpa.domain.BaseEntity;


@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends CrudRepository<T, Long>, BaseRepositoryCustom<T>
{
	public Page<T> findAll(Pageable pageable);
}
