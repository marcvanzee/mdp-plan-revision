package model;

import gui.DrawTaskScheduler;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import model.mdp.State;
import model.mdp.operations.MDPGenerator;
import model.mdp.operations.MDPValueIterator;

/**
 * A TileWorldSimulation consists of a TileWorld (i.e. an MDP and an Agent) and models the evolution of this TileWorld over time.
 * 
 * @author marc.vanzee
 *
 */
public class TileWorldSimulation extends Observable 
{
	private final PopulatedMDP populatedMDP = new PopulatedMDP();
	private final MDPValueIterator valueIterator = new MDPValueIterator();
	private final MDPGenerator mdpGenerator = new MDPGenerator();
	
	private int steps = 0;
	
	boolean isRunning = false;
	
	private Timer timer;
	
	//
	// GETTERS AND SETTERS
	//
	
	public PopulatedMDP getPopulatedMDP() {
		return populatedMDP;
	}
	
	public Agent getAgent() {
		return populatedMDP.getAgent();
	}
	
	public double getValue(State s) {
		return valueIterator.getValue(populatedMDP.getStates().indexOf(s));
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void buildNewModel() 
	{
		if (isRunning)
			timer.cancel();
		
		populatedMDP.reset();
		
		// try adding an observer so that the MDP can send its changes directly to the GUI
				
		mdpGenerator.run(populatedMDP);
		
		if (Settings.ADD_AGENT)
			populatedMDP.addAgent();
		
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
		
		populatedMDP.step();
		
		notifyGUI();
	}
	
	public int getSteps() {
		return steps;
	}
	
	public void notifyGUI() {
		setChanged();
	    notifyObservers(populatedMDP.getMessageBuffer());
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
