
package one.microstream.demo.readmecorp.jpa.dal;

import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.rapidpm.dependencies.core.logger.HasLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;


public abstract class BaseRepositoryImpl<T> implements BaseRepositoryCustom<T>, HasLogger
{
	@FunctionalInterface
	public static interface PreparedStatementSetter<T>
	{
		public void setValues(
			final PreparedStatement ps,
			final T entity
		)
			throws SQLException;
	}
	
	private final static int               BATCH_SIZE             = 100_000;
	private final static DateTimeFormatter LOCALDATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final static DateTimeFormatter LOCALDATE_FORMATTER     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final static DateTimeFormatter LOCALTIME_FORMATTER     = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public BaseRepositoryImpl()
	{
		super();
	}
	
	protected void batchInsert(
		final Collection<T> entities,
		final PreparedStatementSetter<T> statementSetter
	)
	{
		this.jdbcTemplate.batchUpdate(this.insertSql(), new BatchPreparedStatementSetter()
		{
			final int         size     = entities.size();
			final Iterator<T> iterator = entities.iterator();
			int               pos      = -1;
			T                 current;
			
			@Override
			public void setValues(
				final PreparedStatement ps,
				final int i
			)
				throws SQLException
			{
				T elem = this.current;
				if(this.pos < i)
				{
					elem = this.current = this.iterator.next();
					this.pos++;
				}
				
				statementSetter.setValues(ps, elem);
			}
			
			@Override
			public int getBatchSize()
			{
				return this.size;
			}
		});
	}
	
	@Override
	public void batchInsert(
		final BatchPreparedStatementSetter pss
	)
	{
//		this.jdbcTemplate.batchUpdate(this.insertSql(), statementSetter);
		
		final List<String> sqls = new ArrayList<>(BATCH_SIZE);
		
		this.jdbcTemplate.execute(this.insertSql(), (PreparedStatementCallback<Object>)ps ->
		{
			try
			{
				final InterruptibleBatchPreparedStatementSetter ipss =
					(pss instanceof InterruptibleBatchPreparedStatementSetter
						? (InterruptibleBatchPreparedStatementSetter)pss : null);
				for(int i = 0, c = pss.getBatchSize(); i < c; i++)
				{
					pss.setValues(ps, i);
					
					if(ipss != null && ipss.isBatchExhausted(i))
					{
						break;
					}
					
					sqls.add(unwrap(ps).toString());
					
					if(sqls.size() == BATCH_SIZE)
					{
						this.executeAndClear(sqls);
					}
				}
				return null;
			}
			finally
			{
				if(pss instanceof ParameterDisposer)
				{
					((ParameterDisposer)pss).cleanupParameters();
				}
			}
		});
		
		if(sqls.size() > 0)
		{
			this.executeAndClear(sqls);
		}
	}
	
	private void executeAndClear(final List<String> sqls)
	{
		this.logger().info("Executing " + sqls.size() + " inserts...");
		
		final String sql = sqls.stream().collect(Collectors.joining(";\n", "", ";"));
		sqls.clear();
		this.jdbcTemplate.execute(sql);
	}
		
	private static PreparedStatement unwrap(PreparedStatement statement) throws SQLException
	{
		while(statement instanceof Wrapper)
		{
			final PreparedStatement wrapped = ((Wrapper)statement).unwrap(PreparedStatement.class);
			if(wrapped == statement)
			{
				break;
			}
			statement = wrapped;
		}
		return statement;
	}
	
	protected abstract String insertSql();
	
	@Override
	public <E> void dump(
		final Dumper<E> dumper,
		final Writer writer
	)
		throws IOException
	{
		writer.write(this.copySql());
		writer.write(" FROM stdin;\n");
		
		for(int bi = 0, bs = dumper.batchSize(); bi < bs; bi++)
		{
			final Object[] values = dumper.values(bi);
			for(int i = 0; i < values.length; i++)
			{
				writer.write(this.dump(values[i]));
				writer.write(i < values.length - 1 ? '\t' : '\n');
			}
		}
		
		writer.write("\\.\n\n");
	}
	
	protected String dump(final Object value)
	{
		if(value instanceof LocalDateTime)
		{
			return LOCALDATETIME_FORMATTER.format((LocalDateTime)value);
		}
		if(value instanceof LocalDate)
		{
			return LOCALDATE_FORMATTER.format((LocalDate)value);
		}
		if(value instanceof LocalTime)
		{
			return LOCALTIME_FORMATTER.format((LocalTime)value);
		}
		
		return value == null
			? "null"
			: value.toString();
	}
	
	protected abstract String copySql();
}
