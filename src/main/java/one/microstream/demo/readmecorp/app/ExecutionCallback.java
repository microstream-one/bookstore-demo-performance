
package one.microstream.demo.readmecorp.app;

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
