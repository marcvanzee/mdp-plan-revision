package solver;

public class Parameters {
	// DEFAULT VALUES
	static double pState = 1.0, pDeterministic = 0.5, pExecutable = 0.5, minReward = 0.0, maxReward = 20.0;
	static int maxX = 5, maxY = 5, numActions = 3;
	
	public static void setValues(double pState, double pDeterministic, double pExecutable, 
			double minReward, double maxReward, int maxX, int maxY, int numActions) {
		Parameters.pState = pState;
		Parameters.pDeterministic = pDeterministic;
		Parameters.pExecutable = pExecutable;
		Parameters.minReward = minReward;
		Parameters.maxReward = maxReward;
		Parameters.maxX = maxX;
		Parameters.maxY = maxY;
		Parameters.numActions = numActions;
	}
	
	public static String valuesToString() {
		return "pState = " + Parameters.pState + ", pDeterministic = " + Parameters.pDeterministic +
				", pExecutable = " + Parameters.pExecutable + ", minReward = " + Parameters.minReward +
				", maxReward = " + Parameters.maxReward + ", maxX = " + Parameters.maxX +
				", maxY = " + Parameters.maxY + ", numActions = " + Parameters.numActions;
	}
}
