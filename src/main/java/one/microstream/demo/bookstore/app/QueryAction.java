
package one.microstream.demo.bookstore.app;

public interface QueryAction extends Action
{
	public QueryStats queryStats();
	
	public static QueryAction New(
		final String description,
		final Runnable msQuery,
		final Runnable jpaQuery
	)
	{
		return new Default(description, msQuery, jpaQuery);
	}
	
	public static class Default implements QueryAction, Runnable
	{
		private final String   description;
		private final Runnable msQuery;
		private final Runnable jpaQuery;
		private QueryStats     queryStats;
		
		Default(
			final String description,
			final Runnable msQuery,
			final Runnable jpaQuery
		)
		{
			super();
			this.description = description;
			this.msQuery     = msQuery;
			this.jpaQuery    = jpaQuery;
		}
		
		@Override
		public String description()
		{
			return this.description;
		}
		
		@Override
		public Runnable logic()
		{
			return this;
		}
		
		@Override
		public void run()
		{
			final Stopwatch stopwatch = Stopwatch.StartNanotime();
			
			this.msQuery.run();
			
			final long msNanos = stopwatch.restart();
			
			this.jpaQuery.run();
			
			final long jpaNanos = stopwatch.stop();
			
			this.queryStats = QueryStats.New(this.description, msNanos, jpaNanos);
		}
		
		@Override
		public QueryStats queryStats()
		{
			return this.queryStats;
		}
		
		@Override
		public String toString()
		{
			return this.description;
		}
		
	}
	
}
