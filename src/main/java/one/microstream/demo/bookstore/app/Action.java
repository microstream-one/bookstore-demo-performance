package one.microstream.demo.bookstore.app;

public interface Action
{
	public String description();
	
	public Runnable logic();
}
