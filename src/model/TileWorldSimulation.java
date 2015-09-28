package model;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import factories.MDPType;
import gui.DrawTaskScheduler;
import messaging.edges.AddStateEdgesMessage;
import messaging.states.AddStatesMessage;
import model.mdp.ActionEdge;
import model.mdp.QEdge;
import model.mdp.State;
import model.mdp.StateEdge;
import model.mdp.operations.MDPValueIterator;

/**
 * A TileWorldSimulation consists of a TileWorld (i.e. an MDP and an Agent) and models the evolution of this TileWorld over time.
 * 
 * @author marc.vanzee
 *
 */
public class TileWorldSimulation extends BasicSimulation
{
	public TileWorldSimulation() {
		super(MDPType.TILEWORLD);
	}

	private final MDPValueIterator valueIterator = new MDPValueIterator();
	
	private int nextHole;
		
	//
	// GETTERS AND SETTERS
	//
	
	public Agent getAgent() {
		return this.mdp.getAgent();
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
		
		// generate a tileworld	
		this.tileWorldGenerator.run(tw);
		
		if (Settings.ADD_AGENT)
			tw.addAgent();
		
		steps = 0;
		
		// clear the buffer
		tw.clearMessageBuffer();
		
		List<State> states = tw.getStates();
		
		// now add states and transltions for GUI, leave out qstates because the domain is deterministic
		tw.addMessage(new AddStatesMessage(states));
		
		for (State s : states) {
			for (ActionEdge ae : s.getEdges()) {
				if (ae.getToVertex().getEdges().size() != 1) continue;
				
				StateEdge se = new StateEdge(s, ae.getToVertex().getEdges().get(0).getToVertex(), ae.getAction());
				
				tw.addMessage(new AddStateEdgesMessage(se));
				
				tw.addStateEdge(se);
			}
		}
		
		notifyGUI();
		
		nextHole = 0;
	}
	
	public void startSimulation(DrawTaskScheduler scheduler)
	{
		if (isRunning) {
			timer.cancel();
		}
				
		//setNextHole();
		nextHole = 0;
		
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
		nextHole--;
		
		if (nextHole <= 0) {
			// generate a hole
			State hole = tileworld.getRandomEmptyState();
			hole.setHole(true);
			
			tileworld.addHole(hole);
			
			Random r = new Random();
			
			int low = Settings.SCORE - Settings.SCORE_SD;
			int high = Settings.SCORE + Settings.SCORE_SD;
			
			int score = r.nextInt(high-low) + low;
			
			for (QEdge qe : tileworld.getQEdges()) {
				if (qe.getToVertex() == hole) {
					qe.setReward(score);
				}
			}
			
			System.out.println("added hole at " + hole.toString() + ", lifetime: " + hole.getLifetime() + ", score: " + score);
			
			setNextHole();
			
			tileworld.getAgent().clearPolicy();
		}
		
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
	
	private void setNextHole()
	{
		Random r = new Random();
		
		int low = Settings.GESTATION_PERIOD - Settings.GESTATION_PERIOD_SD;
		int high = Settings.GESTATION_PERIOD + Settings.GESTATION_PERIOD_SD;
		
		nextHole = r.nextInt(high-low) + low;
		
		System.out.println("adding a hole in " + nextHole);
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
