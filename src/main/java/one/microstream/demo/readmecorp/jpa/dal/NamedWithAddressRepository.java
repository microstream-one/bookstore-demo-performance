package one.microstream.demo.readmecorp.jpa.dal;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import one.microstream.demo.readmecorp.jpa.domain.AddressEntity;
import one.microstream.demo.readmecorp.jpa.domain.NamedWithAddressEntity;

@NoRepositoryBean
public interface NamedWithAddressRepository<T extends NamedWithAddressEntity> extends NamedRepository<T>
{
	public Optional<T> findByAddress(AddressEntity address);
}
