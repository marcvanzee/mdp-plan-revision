package settings;

public class BenchmarkSettings 
{
	public static final int SIMULATION_LENGTH = 	15000;
	public static final int REPETITIONS = 			1000;
		
	public static final boolean LOGARITHMIC = 		true;
	
	public static final 
		BenchmarkType BENCHMARK_TYPE = 				BenchmarkType.DYNAMISM;
	
	public static final int BENCHMARK_VALUE_MIN = 	1,
							BENCHMARK_VALUE_MAX = 	110,
							BENCHMARK_POINTS = 		10;
	
	public static final int BENCHMARK_RANGE = 		5; // only for hole gestation time and life time
	
	// for learning
	public static final int TRAINING_LENGTH = 1000000;
	
	public enum BenchmarkType {
		DYNAMISM, PLANNING_TIME, MIN_GESTATION_TIME, MIN_LIFETIME, WORLD_SIZE;
	}

}
