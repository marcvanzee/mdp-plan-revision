package simulations;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import gui.generalMDP.DrawTaskScheduler;
import mdp.MDP;
import mdp.operations.MDPOperation;
import settings.SimulationSettings;

/**
 * A Simulations consists of a MDPGenerator, an MDP, an MDPModifier and several public methods to control the simulation
 * 
 * @author marc.vanzee
 *
 */
public abstract class Simulation<MDPTYPE extends MDP, GENERATOR extends MDPOperation<? extends MDP>, 
						MODIFIER extends MDPOperation<? extends MDP>> extends Observable
{
	protected final Timer timer = new Timer(true);
	protected final MDPTYPE mdp;
	protected final GENERATOR mdpGenerator;
	protected final MODIFIER mdpModifier;	
	
	protected boolean isRunning = false;
	protected int steps = 0;
	
	/*
	 * Constructor
	 */
	public Simulation(Class<MDPTYPE> mdpType, Class<GENERATOR> mdpGenerator, Class<MODIFIER> mdpModifier) throws InstantiationException, IllegalAccessException
	{
		this.mdp = mdpType.newInstance();
		this.mdpGenerator = mdpGenerator.newInstance();
		this.mdpModifier = mdpModifier.newInstance();
	}
	
	/*
	 * Abstract methods
	 */
	public abstract void buildNewModel();

	/*
	 * Implemented methods
	 */
	public void startSimulation(DrawTaskScheduler scheduler) {
		if (isRunning) {
			timer.cancel();
		}
		
		// try to step every 100 ms, this will only work when the GUI has finished drawing
		timer.schedule(new StepTask(scheduler), 
				SimulationSettings.REPAINT_DELAY, SimulationSettings.REPAINT_DELAY); 
	}

	public void stopSimulation() {
		timer.cancel();		
	}

	public void step() 
	{
		steps++;		
		notifyGUI();
	}
	
	public void notifyGUI() {
		setChanged();
	    notifyObservers(mdp.getMessageBuffer());
	}
	
	/*
	 * Implemented getters and setters
	 */
	public int getSteps() {
		return steps;
	}

	public MDP getMDP() {
		return mdp;
	}
	
	/*
	 * Private class that is used by the timer
	 */
	private class StepTask extends TimerTask
	{
		DrawTaskScheduler scheduler;
		
		public StepTask(DrawTaskScheduler s) {
			this.scheduler = s;
		}
		
		public void run() 
		{
			if (scheduler == null || scheduler.hasFinished())
				step();
		}
			// wait until the GUI has finished drawing
	}
}
