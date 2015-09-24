package model;

import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import gui.DrawTaskScheduler;
import messaging.edges.AddStateEdgesMessage;
import messaging.states.AddStatesMessage;
import model.mdp.ActionEdge;
import model.mdp.State;
import model.mdp.StateEdge;
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
		TileWorld tw = this.tileworld;
		
		if (isRunning)
			timer.cancel();
		
		tw.reset();
		
		// try adding an observer so that the MDP can send its changes directly to the GUI
				
		this.tileWorldGenerator.run(tw);
		
		if (Settings.ADD_AGENT)
			tw.addAgent();
		
		steps = 0;
		
		// clear the buffer
		tw.clearMessageBuffer();
		
		List<State> states = tw.getStates();
		
		// now only add states and transltions, leave out qstates because the domain is deterministic
		tw.addMessage(new AddStatesMessage(states));
		
		for (State s : states) {
			for (ActionEdge ae : s.getEdges()) {
				if (ae.getToVertex().getEdges().size() != 1) continue;
				
				StateEdge se = new StateEdge(s, ae.getToVertex().getEdges().get(0).getToVertex(), ae.getAction());
				
				tw.addMessage(new AddStateEdgesMessage(se));
			}
		}
		
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
