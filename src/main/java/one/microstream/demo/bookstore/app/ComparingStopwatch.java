
package one.microstream.demo.bookstore.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;
import org.rapidpm.dependencies.core.logger.HasLogger;


public interface ComparingStopwatch
{
	public ComparingStopwatch restart();
	
	public ComparingStopwatch stop();
	
	public ComparingStopwatch logResult();
	
	
	public static ComparingStopwatch Start(
		final String first,
		final String second
	)
	{
		return new Default(first, second);
	}
	
	public static class Default implements ComparingStopwatch, HasLogger
	{
		static final long   nanosPerSecond = 1_000_000_000L;
		static final long   nanosPerMilli  =     1_000_000L;
		
		private final String    first;
		private final String    second;
		private final Stopwatch stopwatch;
		private long            firstDuration;
		private long            secondDuration;
		
		Default(
			final String first,
			final String second
		)
		{
			this.first     = first;
			this.second    = second;
			this.stopwatch = Stopwatch.StartNanotime();
		}
		
		@Override
		public ComparingStopwatch restart()
		{
			this.firstDuration = this.stopwatch.restart();
			return this;
		}
		
		@Override
		public ComparingStopwatch stop()
		{
			this.secondDuration = this.stopwatch.stop();
			return this;
		}
		
		@Override
		public ComparingStopwatch logResult()
		{
			final String firstSeconds  = String.format("%1$12s",this.decimalFormat(4).format(
				this.divide(this.firstDuration, nanosPerSecond)));
			final String secondSeconds = String.format("%1$12s",this.decimalFormat(4).format(
				this.divide(this.secondDuration, nanosPerSecond)));
			
			final String firstMillis  = String.format("%1$10s",this.decimalFormat(2).format(
				this.divide(this.firstDuration, nanosPerMilli))) + "  ";
			final String secondMillis = String.format("%1$10s",this.decimalFormat(2).format(
				this.divide(this.secondDuration, nanosPerMilli)));

			final String factor      = this.decimalFormat(2).format(this.divide(this.secondDuration, this.firstDuration));
			
			this.logger().info(StringUtils.repeat('-', 35 + this.first.length() + this.second.length()));
			this.logger().info("        |" + StringUtils.repeat(' ', 13 - this.first.length()) +
				this.first   + " | " + StringUtils.repeat(' ', 12 - this.second.length()) + this.second +
				" | Factor");
			this.logger().info(StringUtils.repeat('-', 35 + this.first.length() + this.second.length()));
			this.logger().info("Seconds | " + firstSeconds + " | " + secondSeconds +   " | " + factor);
			this.logger().info("Millis  | " + firstMillis  + " | " + secondMillis  + "   | " + factor);
			this.logger().info("");
			
			return this;
		}
		
		private double divide(final long dividend, final long divisor)
		{
			return new BigDecimal(dividend)
				.divide(new BigDecimal(divisor), 10, RoundingMode.HALF_UP)
				.doubleValue();
		}

		private NumberFormat decimalFormat(final int fractionDigits)
		{
			final NumberFormat format = NumberFormat.getNumberInstance();
			format.setGroupingUsed(false);
			format.setMinimumFractionDigits(fractionDigits);
			format.setMaximumFractionDigits(fractionDigits);
			return format;
		}
		
	}
	
}
