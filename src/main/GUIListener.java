package main;

public interface GUIListener extends TaskListener 
{
	public void threadEvent(Runnable runner, int event);
}
