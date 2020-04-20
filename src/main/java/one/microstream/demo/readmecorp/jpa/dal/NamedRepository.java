package one.microstream.demo.readmecorp.jpa.dal;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import one.microstream.demo.readmecorp.jpa.domain.NamedEntity;

@NoRepositoryBean
public interface NamedRepository<T extends NamedEntity> extends BaseRepository<T>
{
	public Optional<T> findByName(String name);
}
