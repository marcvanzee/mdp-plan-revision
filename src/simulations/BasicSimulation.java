package simulations;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import gui.generalMDP.DrawTaskScheduler;
import mdps.MDP;
import mdps.factories.MDPFactory;
import mdps.factories.MDPType;
import mdps.generators.MDPGenerator;
import settings.SimulationSettings;

public abstract class BasicSimulation extends Observable implements Simulation
{

	Timer timer;
	boolean isRunning = false;
	int steps = 0;
	final MDP mdp;
	final MDPGenerator mdpGenerator;
	
	/*
	 * Constructor
	 */
	public BasicSimulation(MDPType mdpType) {
		MDPFactory mdpFactory = new MDPFactory(mdpType);
		
		this.mdp = mdpFactory.buildMDP();
		this.mdpGenerator = mdpFactory.buildMDPGenerator();
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
		
		timer = new Timer(true);
		
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
	class StepTask extends TimerTask
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
