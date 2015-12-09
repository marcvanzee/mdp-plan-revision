package benchmarking;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import gui.Main;
import gui.tileworld.TileworldGUI;
import mdp.agent.LearningAgent;
import mdp.algorithms.AlgorithmType;
import settings.BenchmarkSettings;
import settings.BenchmarkSettings.BenchmarkType;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmark 
{
	private TileworldSimulation simulation;
	
	public static void main(String args[])
	{
		try {
			(new TileworldBenchmark()).go();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{		
		try {
			Main.loadSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (TileworldSettings.ALGORITHM == AlgorithmType.LEARNING) {
			benchmarkLearning();
		} else {
			benchmarkNotLearning();
		}
	}
	
	public void benchmarkNotLearning() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {		
		boolean loga = BenchmarkSettings.LOGARITHMIC;
		
		int vMin = BenchmarkSettings.BENCHMARK_VALUE_MIN,
				vMax = BenchmarkSettings.BENCHMARK_VALUE_MAX,
				vPoints = BenchmarkSettings.BENCHMARK_POINTS,
				simLength = BenchmarkSettings.SIMULATION_LENGTH,
				simRep = BenchmarkSettings.REPETITIONS;
		BenchmarkType bmType = BenchmarkSettings.BENCHMARK_TYPE;
	
		double vStep = loga ? (Math.log10(vMax) - Math.log10(vMin)) / vPoints :
					Math.ceil(((double)vMax-(double)vMin)/(double)vPoints);
			
		System.out.println("###### benchmark settings ####");
		System.out.println("# " + bmType + " values: ["+ vMin + ", " + vMax +"], step size: " + vStep);
		System.out.println("# simulation length: " + simLength + ", repetitions per dynamism value: " + simRep);
		System.out.println("# total nr of simulations: " + (vPoints * simRep));
		System.out.println("# logarithmic scale: " + loga);
		System.out.println("##############################\n");
		
		System.out.println(bmType + "; effectiveness");
		
		int value = vMin;
		int iterations = 0;
		
		while (value <= vMax)
		{
			setBenchmarkValue(value);
			
			double totalEff = 0;
			
			//System.out.println("<benchmark>dynamism="+dynamism);
		
			for (int simCount=0; simCount < simRep; simCount++)
			{
				//System.out.println("<benchmark> starting simulation " + (simCount+1));
				simulation = new TileworldSimulation();
				simulation.buildNewModel();
				simulation.startSimulation(simLength);
				
				double score = simulation.getAgentScore(),
						maxScore = simulation.getMaxScore(),
						effectiveness = (double) score / (double) maxScore;
				
				totalEff += effectiveness;
			}
		
			totalEff /= (double) simRep;
			
			System.out.println(value + "; " + totalEff);
			
			
			
			int newValue = value;
			
			while (newValue == value) {
				iterations++;
				newValue = (int) (loga ? vMin + Math.pow(10.0, iterations * vStep) : value + vStep);
			}
			
			value = newValue;
		}
	}
	
	public void benchmarkLearning() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		int trainingLength = BenchmarkSettings.TRAINING_LENGTH,
				testLength = BenchmarkSettings.TEST_LENGTH;
		
		System.out.println("---- training ("+trainingLength+" rounds)");
		simulation = new TileworldSimulation();
		simulation.buildNewModel();
		simulation.startSimulation(trainingLength);
				
		double score = simulation.getAgentScore(),
				maxScore = simulation.getMaxScore(),
				effectiveness = (double) score / (double) maxScore;
			
		System.out.println("effectiveness for training: " + effectiveness);
		System.out.println("thetaAct: " + Arrays.toString(LearningAgent.thetaAct));
		System.out.println("thetaThink: " + Arrays.toString(LearningAgent.thetaThink));
		
		// get theta values
		double thetaAct[] = Arrays.copyOf(LearningAgent.thetaAct, LearningAgent.FEATURES);
		double thetaThink[] = Arrays.copyOf(LearningAgent.thetaThink, LearningAgent.FEATURES);
				
		System.out.println("---- testing ("+testLength+" rounds)");
		simulation = new TileworldSimulation();
		simulation.buildNewModel();
		LearningAgent.TRAINING = false;
		LearningAgent.thetaAct = Arrays.copyOf(thetaAct, LearningAgent.FEATURES);
		LearningAgent.thetaThink = Arrays.copyOf(thetaThink, LearningAgent.FEATURES);	
		//LearningAgent.thetaAct = new double[]{67.9586396409001, 0.5906703400857038, -67.9586396409001, -67.9586396409001, 33.99999999997416};
		//LearningAgent.thetaThink = new double[]{67.98464306118106, 0.564665899766446, -67.98464306118106, -67.93263520058085, 33.999999999997904};
		simulation.startSimulation(testLength);
		
		score = simulation.getAgentScore();
		maxScore = simulation.getMaxScore();
		effectiveness = (double) score / (double) maxScore;
			
		System.out.println("effectiveness for testing: " + effectiveness);
		
		System.out.println("now running the agent in the GUI...");
		
		TileworldGUI gui = new TileworldGUI();
		gui.go();
		
		gui.buildNewModel();
		gui.startSimulation();
		
	}
	
	private void setBenchmarkValue(int value)
	{
		switch (BenchmarkSettings.BENCHMARK_TYPE)
		{
		case DYNAMISM: TileworldSettings.DYNAMISM = value; break;
		case MIN_GESTATION_TIME: 
			TileworldSettings.HOLE_GESTATION_TIME_MIN = value; 
			TileworldSettings.HOLE_GESTATION_TIME_MAX = value + BenchmarkSettings.BENCHMARK_RANGE;
			break;
		case MIN_LIFETIME:
			TileworldSettings.HOLE_LIFE_EXP_MIN = value;
			TileworldSettings.HOLE_LIFE_EXP_MAX = value + BenchmarkSettings.BENCHMARK_RANGE;
			break;
		case PLANNING_TIME:
			TileworldSettings.PLANNING_TIME = value;
			break;
		case WORLD_SIZE:
			TileworldSettings.WORLD_SIZE = value;
			break;
		}
	}
}
