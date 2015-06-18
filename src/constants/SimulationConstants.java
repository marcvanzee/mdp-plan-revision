package constants;

public class SimulationConstants 
{
	// constraints on the values
	public static final int MAX_ALLOWED_STATES = 2000;
	public static final int MAX_ALLOWED_ACTIONS = 2000;
	
	public static final double MIN_ALLOWED_REWARD = -5000;
	public static final double MAX_ALLOWED_REWARD = 5000;
	
	public static final int MDP_STORE_SIZE = 50; // how many of the simulations should be stored
	
	public static final int AGENT_CHOICE_DELIBERATE = 1;
	public static final int AGENT_CHOICE_ACT = 1;
}
