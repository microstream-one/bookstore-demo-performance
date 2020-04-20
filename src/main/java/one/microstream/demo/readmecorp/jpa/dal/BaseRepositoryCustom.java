
package one.microstream.demo.readmecorp.jpa.dal;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;


public interface BaseRepositoryCustom<T>
{
	public void batchInsert(
		Collection<T> entities
	);
	
	public void batchInsert(
		BatchPreparedStatementSetter statementSetter
	);
	
	public static interface Dumper<E>
	{
		public int batchSize();
		
		public Object[] values(
			int index
		);
	}
	
	public <E> void dump(
		Dumper<E> dumper,
		Writer writer
	)
		throws IOException;
}
