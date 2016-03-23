package settings;

/**
 * Contains the settings for the entire project
 * 
 * @author marc.vanzee
 *
 */
public class GeneralMDPSettings 
{
	/*
	 * MDP GENERATOR SETTINGS 
	 */
	public static int NUM_STATES = 20, 				// number of states (not q-states) to generate)
			NUM_ACTIONS = 10, 				// number of distinct actions
			AVG_ACTIONS_STATE = 3, 			// average umber of actions that are executable in each state
			ACTION_VARIANCE = 3,			    // variance on numActions for number of actions per state
			MAX_SUCCESSOR_STATES = 4;			// the maximum number of successors of a q-state
	
	public static double MIN_REWARD = 0.0, 		// minimum of the reward
			MAX_REWARD = 20.0, 				// maximum of the reward. reward is random in [minReward,maxReward]
			P_DETERMINISTIC = 0.5, 			// the probability with which an action is deterministic
			P_CYCLE = 0.4;					// the probability that a state is cyclic
	
	public static boolean CYCLES_ALLOWED = true;		// whether to allow cycles in the MDP or not
	
	/*
	 * AGENT SETTINGS
	 */
	public static boolean ADD_AGENT = true;
	public static double P_DELIBERATE = 0.5;       // the probability with which the agent deliberates

	/*
	 * VISUALIZATION SETTINGS
	 */
	public static int REPAINT_DELAY = 100;					// repaint delay in ms
	public static boolean ANIMATE = true;					// whether to animate the steps
	
	/*
	 * MDP MODIFICATION SETTINGS
	 */
	public static double D_GAMMA = 0.5;
}
