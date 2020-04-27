
package one.microstream.demo.bookstore.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public interface ActionExecutor
{
	public void start();
	
	public void shutdown();
	
	public void submit(
		QueryAction action
	);
	
	public void remove(
		QueryAction action
	);
	
	public void submit(
		ClearAction action
	);
	
	public List<QueryAction> submittedQueries();
	
	public void clearQueue();
	
	public void schedule(
		ClearAction action,
		int queryInterval
	);
	
	public void unschedule(
		ClearAction action
	);
	
	public static ActionExecutor New(
		final ExecutionCallback callback
	)
	{
		return new Default(callback);
	}
	
	public static class Default implements ActionExecutor, Runnable
	{
		static final class Entry
		{
			final Action action;
			
			Entry(
				final Action action
			)
			{
				super();
				this.action = action;
			}
		}
		
		private final ExecutionCallback             callback;
		private final LinkedList<Entry>             queue;
		private final HashMap<ClearAction, Integer> clearSchedules;
		private final AtomicBoolean                 started            = new AtomicBoolean(false);
		private final AtomicBoolean                 shutdown           = new AtomicBoolean(false);
		private int                                 executedQueryCount = 0;
		
		Default(
			final ExecutionCallback callback
		)
		{
			super();
			this.callback       = callback;
			this.queue          = new LinkedList<>();
			this.clearSchedules = new HashMap<>();
		}
		
		@Override
		public void submit(
			final QueryAction action
		)
		{
			if(!this.shutdown.get())
			{
				synchronized(this.queue)
				{
					this.queue.addLast(new Entry(action));
				}
				
				this.callback.queueUpdated();
				
				synchronized(this)
				{
					this.notify();
				}
			}
		}
		
		@Override
		public void remove(
			final QueryAction action
		)
		{
			if(!this.shutdown.get())
			{
				synchronized(this.queue)
				{
					this.queue.removeIf(e -> e.action == action);
				}
				
				this.callback.queueUpdated();
			}
		}
		
		@Override
		public void submit(
			final ClearAction action
		)
		{
			if(!this.shutdown.get())
			{
				synchronized(this.queue)
				{
					this.queue.addFirst(new Entry(action));
				}
				
				this.callback.queueUpdated();
				
				synchronized(this)
				{
					this.notify();
				}
			}
		}
		
		@Override
		public List<QueryAction> submittedQueries()
		{
			final List<QueryAction> list = new ArrayList<>();
			
			synchronized(this.queue)
			{
				this.queue.forEach(entry ->
				{
					if(entry.action instanceof QueryAction)
					{
						list.add((QueryAction)entry.action);
					}
				});
			}

			return list;
		}
		
		@Override
		public void clearQueue()
		{
			synchronized(this.queue)
			{
				this.queue.clear();
			}
			
			this.callback.queueUpdated();
			
			synchronized(this)
			{
				this.notify();
			}
		}
		
		@Override
		public void schedule(
			final ClearAction action,
			final int queryInterval
		)
		{
			synchronized(this.clearSchedules)
			{
				this.clearSchedules.put(action, queryInterval);
			}
		}
		
		@Override
		public void unschedule(
			final ClearAction action
		)
		{
			synchronized(this.clearSchedules)
			{
				this.clearSchedules.remove(action);
			}
		}
		
		@Override
		public synchronized void start()
		{
			if(!this.started.get())
			{
				this.started.set(true);
				new Thread(this).start();
			}
		}
		
		@Override
		public synchronized void shutdown()
		{
			this.shutdown.set(true);
				
			synchronized(this)
			{
				this.notify();
			}
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				final Action action;
				synchronized(this.queue)
				{
					action = this.queue.isEmpty()
						? null
						: this.queue.removeFirst().action;
				}
				
				if(action != null)
				{
					this.callback.queueUpdated();
					this.execute(action);
				}
				else if(this.shutdown.get())
				{
					return;
				}
				else
				{
					try
					{
						synchronized(this)
						{
							this.wait();
						}
					}
					catch(final InterruptedException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		private void execute(final Action action)
		{
			this.executeAndNotify(action);
			
			if(action instanceof QueryAction)
			{
				this.executedQueryCount++;
				
				List<ClearAction> intervalClearActions;
				synchronized(this.clearSchedules)
				{
					intervalClearActions = this.clearSchedules.entrySet().stream()
						.filter(kv -> this.executedQueryCount % kv.getValue() == 0)
						.map(kv -> kv.getKey())
						.collect(Collectors.toList());
				}
				intervalClearActions.forEach(this::executeAndNotify);
			}
		}
		
		private void executeAndNotify(
			final Action action
		)
		{
			this.callback.beforeExecution(action);
			action.logic().run();
			this.callback.afterExecution(action);
		}
		
	}
	
}
