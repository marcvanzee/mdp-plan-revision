package model;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import messaging.ChangeMessageBuffer;
import model.mdp.State;
import model.mdp.operations.MDPGenerator;
import model.mdp.operations.MDPValueIterator;

/**
 * A Model consists of a PopulatedMDP (i.e. an MDP and an Agent) and models the evolution of this MDP over time.
 * 
 * @author marc.vanzee
 *
 */
public class Model extends Observable 
{
	private final PopulatedMDP populatedMDP = new PopulatedMDP();
	private final MDPValueIterator valueIterator = new MDPValueIterator();
	private final MDPGenerator mdpGenerator = new MDPGenerator();
	
	private int steps = 0;
	
	boolean isRunning = false;
	
	final Timer timer = new Timer(true);
	
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
		
		// try adding an observer so that the MDP can send it's changes directly to the GUI
				
		mdpGenerator.run(populatedMDP);
		
		populatedMDP.addAgent();
		
		steps = 0;
		
		notifyGUI();
	}
	
	public void startSimulation()
	{
		if (isRunning) {
			timer.cancel();
		}
		
		timer.schedule(new StepTask(), 500, 500);
	}
	
	public void stopSimulation()
	{
		timer.cancel();
	}
	
	public void computeOptimalPolicy()
	{
		valueIterator.run(populatedMDP);
		
		// we do not have to send any changes to the observer,
		// because we only change the value of the vertices, we do not add or remove any
	}
	
	public void step() 
	{
		steps++;
		
		populatedMDP.step();
	}
	
	public int getSteps() {
		return steps;
	}
	
	public void notifyGUI() {
		System.out.println("notifying gui");
		setChanged();
	    notifyObservers(populatedMDP.getMessageBuffer());
	}
		
	class StepTask extends TimerTask
	{
		public void run() 
		{
			step();
		}
	}
}
