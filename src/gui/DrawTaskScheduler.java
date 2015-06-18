package gui;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import messaging.ChangeMessage;
import messaging.ChangeMessageBuffer;

public class DrawTaskScheduler 
{
	// we create a queue with tasks and execute a task every REDRAW_RATE milliseconds
    // this is to avoid concurrent modification of our graph.
	private final LinkedList<ChangeMessage> taskList = new LinkedList<ChangeMessage>();
	private final int REDRAW_RATE = 100;
	private final Timer timer = new Timer();
	private final DrawPanel parent;
	
	private boolean running = false;
	
	public DrawTaskScheduler(DrawPanel parent) {
		this.parent = parent;
	}
		
	public void add(ChangeMessage gc) 
	{
		if (gc instanceof ChangeMessageBuffer) {
			taskList.addAll(((ChangeMessageBuffer) gc).getChanges());
		} else {
			taskList.add(gc);
		}
		
		tryStartTimer();
	}
	
	private void tryStartTimer() 
	{
		if (!running) {
			running = true;
			timer.schedule(new ExecuteTask(), REDRAW_RATE, REDRAW_RATE); 
		}
	}
		      
    class ExecuteTask extends TimerTask
    {
    	public void run() 
    	{
    		try {
	    		ChangeMessage gc = taskList.removeFirst();
	    		parent.changeGraph(gc);
    		} catch (NoSuchElementException e) {
    			timer.cancel();
    			running = false;
    		}
         }
    }
    
}
