package one.microstream.demo.bookstore.jpa.dal;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import one.microstream.demo.bookstore.jpa.domain.AddressEntity;
import one.microstream.demo.bookstore.jpa.domain.NamedWithAddressEntity;

@NoRepositoryBean
public interface NamedWithAddressRepository<T extends NamedWithAddressEntity> extends NamedRepository<T>
{
	public Optional<T> findByAddress(AddressEntity address);
}
