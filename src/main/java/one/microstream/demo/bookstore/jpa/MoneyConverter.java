package one.microstream.demo.bookstore.jpa;

import javax.money.MonetaryAmount;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import one.microstream.demo.bookstore.BookStoreDemo;

@Converter
public class MoneyConverter implements AttributeConverter<MonetaryAmount, Double>
{
	public MoneyConverter()
	{
		super();
	}

	@Override
	public Double convertToDatabaseColumn(
		final MonetaryAmount money
	)
	{
		return money == null
			? null
			: money.getNumber().doubleValue()
		;
	}

	@Override
	public MonetaryAmount convertToEntityAttribute(
		final Double amount
	)
	{
		return amount == null
			? null
			: BookStoreDemo.money(amount)
		;
	}

}
