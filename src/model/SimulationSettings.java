package model;

/**
 * Contains the settings for the simulations, some of them are changable in the GUI
 * 
 * @author marc.vanzee
 *
 */
public class SimulationSettings 
{
	/*
	 * MDP SETTINGS
	 */
	private int numStates = 5, 				// number of states (not q-states) to generate)
			numActions = 10, 				// number of distinct actions
			avgActionsState = 3, 			// average umber of actions that are executable in each state
			maxSuccessorStates = 4;			// the maximum number of successors of a q-state
	
	private double minReward = 0.0, 		// minimum of the reward
			maxReward = 20.0, 				// maximum of the reward. reward is random in [minReward,maxReward]
			pDeterministic = 0.5, 			// the probability with which an action is deterministic
			pCycle = 0.4;					// the probability that a state is cyclic
	
	private boolean allowCycles = true;		// whether to allow cycles in the MDP or not
	
	/*
	 * AGENT SETTINGS
	 */
	private double pDeliberate = 0.5;       // the probability with which the agent deliberates
	
	/*
	 * Enforce the singleton property. We do not want to have two setting files 
	 * floating around. We enforce this by declaring a final field INSTANCE that 
	 * contains our instantiated class, and declaring a private constructor. The
	 * instantiated class can be reached via SimulationSettings.getInstance().
	 */
	private static final SimulationSettings INSTANCE = new SimulationSettings();
	
	private SimulationSettings() { }
	
	public static SimulationSettings getInstance() {
		return INSTANCE;
	}
		
	/*
	 * GETTERS AND SETTERS
	 */
	public int getNumStates() { return numStates; }
	public int getNumActions() { return numActions; }
	public int getAvgActionsState() { return avgActionsState; }
	public int getmaxSuccessorStates() { return maxSuccessorStates; }
	public double getMinReward() { return minReward; }
	public double getMaxReward() { return maxReward; }
	public double getPDeterministic() { return pDeterministic; }
	public double getPCyclic() { return pCycle; }
	public double getPDeliberate() { return pDeliberate; }
	public boolean allowCycles() { return allowCycles; }
	
	public void setNumStates(int newNumstates) { this.numStates = newNumstates; }
	public void setNumActions(int newNumActions) { this.numActions = newNumActions; }
	public void setAvgActionsState(int newAvgActionsState) { this.avgActionsState = newAvgActionsState; }
	public void setmaxSuccessorStates(int newMaxSuccessorStates) { this.maxSuccessorStates = newMaxSuccessorStates; }
	public void setMinReward(double newMinReward) { this.minReward = newMinReward; }
	public void setMaxReward(double newMaxReward) { this.maxReward = newMaxReward; }
	public void setPDeterministic(double newPDeterministic) { this.pDeterministic = newPDeterministic; }
	public void setPCyclic(double newPCyclic) { this.pCycle = newPCyclic; }
	public void setPDeliberate(double newPDeliberate) { this.pDeliberate = newPDeliberate; }
	
	public void setCyclic(boolean cyclic) { this.allowCycles = cyclic; }
	
	
	
}
