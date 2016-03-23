package gui.generalMDP;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import messaging.jung.ChangeMessage;
import settings.GeneralMDPSettings;

public class DrawTaskScheduler 
{
	// we create a queue with tasks and execute a task every REDRAW_RATE milliseconds
    // this is to avoid concurrent modification of our graph.
	private final LinkedList<ChangeMessage> taskList = new LinkedList<ChangeMessage>();
	private Timer timer;
	private final MDPDrawer parent;
	
	private boolean running = false;
	
	private boolean wait = false;
	
	public DrawTaskScheduler(MDPDrawer parent) {
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
			timer.schedule(new ExecuteTask(), GeneralMDPSettings.REPAINT_DELAY, 
					GeneralMDPSettings.REPAINT_DELAY); 
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
    
    public boolean hasFinished() {
    	return (taskList.size() == 0 && !wait); 
    }
    
}
