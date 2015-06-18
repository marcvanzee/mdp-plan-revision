package gui;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import messaging.ChangeMessage;

public class DrawTaskScheduler 
{
	// we create a queue with tasks and execute a task every REDRAW_RATE milliseconds
    // this is to avoid concurrent modification of our graph.
	private final LinkedList<ChangeMessage> taskList = new LinkedList<ChangeMessage>();
	private final int REDRAW_RATE = 500;
	private Timer timer;
	private final DrawPanel parent;
	
	private boolean running = false;
	
	private boolean wait = false;
	
	public DrawTaskScheduler(DrawPanel parent) {
		this.parent = parent;
	}
		
	public void add(ChangeMessage gc) 
	{
		taskList.add(gc);
		
		tryStartTimer();
	}
	
	public void setWait(boolean wait)
	{
		this.wait = wait;
	}
	
	private void tryStartTimer() 
	{
		if (!running) {
			running = true;
			timer = new Timer();
			timer.schedule(new ExecuteTask(), REDRAW_RATE, REDRAW_RATE); 
		}
	}
		      
    class ExecuteTask extends TimerTask
    {
    	public void run() 
    	{
    		if (wait) 
    			return; // wait because someone else is working on the graph
    
    		try {
	    		ChangeMessage gc = taskList.removeFirst();
	    		wait = true; // don't change the graph until the GUI has finished drawing it.
	    		parent.changeGraph(gc);
    		} catch (NoSuchElementException e) {
    			timer.cancel();
    			running = false;
    			wait = false;
    		}
         }
    }
    
}
