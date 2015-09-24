package model;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import gui.DrawTaskScheduler;
import model.mdp.State;
import model.mdp.operations.MDPGenerator;
import model.mdp.operations.MDPValueIterator;
import model.mdp.operations.TileWorldGenerator;

/**
 * A TileWorldSimulation consists of a TileWorld (i.e. an MDP and an Agent) and models the evolution of this TileWorld over time.
 * 
 * @author marc.vanzee
 *
 */
public class TileWorldSimulation extends Observable 
{
	private final TileWorld tileworld = new TileWorld();
	private final MDPValueIterator valueIterator = new MDPValueIterator();
	private final TileWorldGenerator tileWorldGenerator = new TileWorldGenerator();
	
	private int steps = 0;
	
	boolean isRunning = false;
	
	private Timer timer;
	
	//
	// GETTERS AND SETTERS
	//
	
	public TileWorld getTileWorld() {
		return this.tileworld;
	}
	
	public Agent getAgent() {
		return this.tileworld.getAgent();
	}
	
	public double getValue(State s) {
		return valueIterator.getValue(this.tileworld.getStates().indexOf(s));
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void buildNewModel() 
	{
		if (isRunning)
			timer.cancel();
		
		this.tileworld.reset();
		
		// try adding an observer so that the MDP can send its changes directly to the GUI
				
		this.tileWorldGenerator.run(this.tileworld);
		
		if (Settings.ADD_AGENT)
			this.tileworld.addAgent();
		
		steps = 0;
		
		notifyGUI();
	}
	
	public void startSimulation(DrawTaskScheduler scheduler)
	{
		if (isRunning) {
			timer.cancel();
		}
		
		timer = new Timer(true);
		
		// try to step every 100 ms, this will only work when the GUI has finished drawing
		timer.schedule(new StepTask(scheduler), 100, 100); 
	}
	
	public void stopSimulation()
	{
		timer.cancel();
	}
		
	public void step() 
	{
		steps++;
		
		this.tileworld.step();
		
		notifyGUI();
	}
	
	public int getSteps() {
		return steps;
	}
	
	public void notifyGUI() {
		setChanged();
	    notifyObservers(this.tileworld.getMessageBuffer());
	}
		
	class StepTask extends TimerTask
	{
		DrawTaskScheduler scheduler;
		
		public StepTask(DrawTaskScheduler s) {
			this.scheduler = s;
		}
		
		public void run() 
		{
			if (scheduler.hasFinished())
				step();
		}
			// wait until the GUI has finished drawing
	}
}
