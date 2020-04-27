package one.microstream.demo.bookstore.jpa.dal;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import one.microstream.demo.bookstore.jpa.domain.NamedWithCodeEntity;

@NoRepositoryBean
public interface NamedWithCodeRepository<T extends NamedWithCodeEntity> extends NamedRepository<T>
{
	public Optional<T> findByCode(String code);
}
