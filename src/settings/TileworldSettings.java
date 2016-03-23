package settings;

import benchmarking.JSONParameters;
import mdp.agent.ReactionStrategy;
import mdp.algorithms.AlgorithmType;

public class TileworldSettings 
{
	
	/*
	 * Environment Settings
	 */
	public static int WORLD_SIZE =	 				20;
	public static int HOLE_GESTATION_TIME_MIN = 	60;
	public static int HOLE_GESTATION_TIME_MAX = 	240;
	public static int HOLE_LIFE_EXP_MIN = 			20;
	public static int HOLE_LIFE_EXP_MAX = 			25;
	public static int HOLE_SCORE_MIN = 				20;
	public static int HOLE_SCORE_MAX =				80; 
	public static int INITIAL_NR_HOLES = 			4;
	public static int WALL_SIZE_MIN =				2;
	public static int WALL_SIZE_MAX = 				4;
	public static int INITIAL_NR_WALLS =			10;
	
	/*
	 * Agent Settings
	 */
	
	// Dynamism = nr. Env steps / nr. Agent steps
	// so d=5 means the agent takes 1 step every 5 steps of the environment
	public static int DYNAMISM = 					1;
	
	// Planning time is the cost for making a plan.
	// So p=4 means the reward of the agent is decreased by 4 whenever it forms a plan
	public static double PLANNING_TIME = 			1;
	
	// Commitment degree is the number of steps the agent keeps its plan
	// When cd=-1, the agent keeps its plan until it's finished
	// For value iteration, this means the agent replans when it has reached a state where it cannot optimize further
	public static int BOLDNESS = 					-1;
	public static boolean USE_REACTION_STRATEGY = 	false;
	public static ReactionStrategy 
						REACTION_STRATEGY =			ReactionStrategy.TARGET_DISAPPEARS;
	
	public static AlgorithmType ALGORITHM = AlgorithmType.SHORTEST_PATH;
	
	public static ReactionStrategy parseReactionStrategy(String str) {
		if (str.equals("TARGET_DISAPPEARS")) return ReactionStrategy.TARGET_DISAPPEARS;
		if (str.equals("TARGET_DIS_OR_NEARER_HOLE")) return ReactionStrategy.TARGET_DIS_OR_NEARER_HOLE;
		
		return ReactionStrategy.TARGET_DIS_OR_ANY_HOLE;
	}
	
	/*
	 * Angel Settings
	 */
	
	
	public static int HYPOTHESIS_DEPTH = 6;
	
	public static int HYPOTHESIS_REPETITIONS = 5;
	
	public static boolean TEST_ENV = false;
	
	public static boolean PRINT_NOTHING = true;
	
	public static void copyValues(JSONParameters params) {
		WORLD_SIZE = params.worldSize;
		HOLE_GESTATION_TIME_MIN = params.holeGestTimeMin;
		HOLE_GESTATION_TIME_MAX = params.holeGestTimeMax;
		HOLE_LIFE_EXP_MIN = params.holeLifeExpMin;
		HOLE_LIFE_EXP_MAX = params.holeLifeExpMax;
		HOLE_SCORE_MIN = params.holeScoreMin;
		HOLE_SCORE_MAX = params.holeScoreMax;
		INITIAL_NR_HOLES = params.initNrHoles;
		INITIAL_NR_WALLS = params.initNrWalls;
		PLANNING_TIME = params.planningTime;
	}

	public static void print() {
		System.out.println("% --- TILEWORLD SETTINGS:\n"
				+ "% world size = " + TileworldSettings.WORLD_SIZE + "\n"
				+ "% holeGestationTimeMin = " + TileworldSettings.HOLE_GESTATION_TIME_MIN + "\n"
				+ "% holeGestationTimeMax = " + TileworldSettings.HOLE_GESTATION_TIME_MAX + "\n"
				+ "% holeLifetimeMin = " + TileworldSettings.HOLE_LIFE_EXP_MIN + "\n"
				+ "% holeLifetimeMax = " + TileworldSettings.HOLE_LIFE_EXP_MAX + "\n"
				+ "% holeScoreMin = " + TileworldSettings.HOLE_SCORE_MIN + "\n"
				+ "% holeScoreMax = " + TileworldSettings.HOLE_SCORE_MAX + "\n"
				+ "% initialNrHoles = " + TileworldSettings.INITIAL_NR_HOLES + "\n" 
				+ "% wallSizeMin = " + TileworldSettings.WALL_SIZE_MIN + "\n" 
				+ "% wallSizeMax = " + TileworldSettings.WALL_SIZE_MAX + "\n" 
				+ "% initialNrWalls = " + TileworldSettings.INITIAL_NR_WALLS + "\n" 
				+ "% dynamism = " + TileworldSettings.DYNAMISM+ "\n" 
				+ "% planningTime = " + TileworldSettings.PLANNING_TIME);
		
	}
}
