package one.microstream.demo.readmecorp.jpa.dal;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import one.microstream.demo.readmecorp.jpa.domain.NamedWithCodeEntity;

@NoRepositoryBean
public interface NamedWithCodeRepository<T extends NamedWithCodeEntity> extends NamedRepository<T>
{
	public Optional<T> findByCode(String code);
}
