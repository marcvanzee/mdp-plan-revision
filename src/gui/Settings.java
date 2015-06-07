package gui;

public class Settings {
	// DEFAULT VALUES
	int numStates = 5, numActions = 10, avgActionsState = 3;
	double minReward = 0.0, maxReward = 20.0;
	boolean allowCycles = true;
	
	// constraints on the values
	public static final int MAX_ALLOWED_STATES = 2000;
	public static final int MAX_ALLOWED_ACTIONS = 2000;
	
	public static final double MIN_ALLOWED_REWARD = -5000;
	public static final double MAX_ALLOWED_REWARD = 5000;
	
	// some other values that we cannot set in the GUI but that we do use to tweak the simulation
	public static final int MAX_SUCCESSOR_STATES = 4;
	public static final double P_DETERMINISTIC = 0.5;
	public static final double P_CYCLE = 0.8;
	
	
	
	public Settings() { }
	
	public Settings(int numStates, int numActions, int avgActionsState,
			double minReward, double maxReward, boolean allowCycles) 
	{
		this.numStates = numStates;
		this.numActions = numActions;
		this.avgActionsState = avgActionsState;
		this.minReward = minReward;
		this.maxReward = maxReward;
		this.allowCycles = allowCycles;
	}
	
	public String valuesToString() {
		return "numstates = " + numStates + ", numActions = " + numActions +
				"avgActionsState = " + avgActionsState + ",  minReward = " + minReward + 
				", maxReward = " + maxReward + ", cycles = " + allowCycles;
	}
	
	public int getNumStates() { return numStates; }
	public int getNumActions() { return numActions; }
	public int getAvgActionsState() { return avgActionsState; }
	public double getMinReward() { return minReward; }
	public double getMaxReward() { return maxReward; }
	public boolean allowCycles() { return allowCycles; }
}
