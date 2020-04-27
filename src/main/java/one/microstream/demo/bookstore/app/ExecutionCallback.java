
package one.microstream.demo.bookstore.app;

public interface ExecutionCallback
{
	public void beforeExecution(
		Action action
	);
	
	public void afterExecution(
		Action action
	);
	
	public void queueUpdated();
}
