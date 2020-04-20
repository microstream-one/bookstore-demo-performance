package one.microstream.demo.readmecorp.app;

public interface Action
{
	public String description();
	
	public Runnable logic();
}
