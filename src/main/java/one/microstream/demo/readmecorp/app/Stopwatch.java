package one.microstream.demo.readmecorp.app;

import java.util.function.LongSupplier;

public interface Stopwatch
{
	public long stop();
	
	public long restart();
	
	public static Stopwatch Start()
	{
		return new Default(System::currentTimeMillis);
	}
	
	public static Stopwatch StartNanotime()
	{
		return new Default(System::nanoTime);
	}
	
	public static class Default implements Stopwatch
	{
		private final LongSupplier timeSupplier;
		private long start;

		public Default(
			final LongSupplier timeSupplier
		)
		{
			super();
			this.start = (this.timeSupplier = timeSupplier).getAsLong();
		}
		
		@Override
		public long stop()
		{
			return this.timeSupplier.getAsLong() - this.start;
		}
		
		@Override
		public long restart()
		{
			final long now = this.timeSupplier.getAsLong();
			final long duration = now - this.start;
			this.start = now;
			return duration;
		}
		
	}
	
}
