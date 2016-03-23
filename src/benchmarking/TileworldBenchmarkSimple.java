package benchmarking;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import gui.Main;
import mdp.agent.ReactionStrategy;
import settings.BenchmarkSettings;
import settings.BenchmarkSettings.BenchmarkType;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmarkSimple 
{
	private TileworldSimulation simulation;
	public static int countDif = 0;
	
	public static void main(String args[])
	{
		try {
			(new TileworldBenchmarkSimple()).go();
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
			
		benchmark();
	}
	
	public void benchmark() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {		
		boolean loga = BenchmarkSettings.LOGARITHMIC;
		
		int vMin = BenchmarkSettings.BENCHMARK_VALUE_MIN,
				vMax = BenchmarkSettings.BENCHMARK_VALUE_MAX,
				vPoints = BenchmarkSettings.BENCHMARK_POINTS,
				simLength = BenchmarkSettings.SIMULATION_LENGTH,
				simRep = BenchmarkSettings.REPETITIONS;
		BenchmarkType bmType = BenchmarkSettings.BENCHMARK_TYPE;
	
		double vStep = loga ? (Math.log10(vMax) - Math.log10(vMin)) / vPoints :
					Math.ceil(((double)vMax-(double)vMin)/(double)vPoints);
		
		TileworldSettings.BOLDNESS = -1;
		TileworldSettings.USE_REACTION_STRATEGY = true;
		TileworldSettings.REACTION_STRATEGY = ReactionStrategy.TARGET_DIS_OR_ANY_HOLE;
		TileworldSettings.PLANNING_TIME = 1;
			
		System.out.println("---- benchmark settings:");
		System.out.println("    algorithm type:" + TileworldSettings.ALGORITHM);
		System.out.println("    " + bmType + " values: ["+ vMin + ", " + vMax +"], step size: " + vStep);
		System.out.println("    simulation length: " + simLength + ", repetitions per dynamism value: " + simRep);
		System.out.println("    total nr of simulations: " + (vPoints * simRep));
		System.out.println("    logarithmic scale: " + loga);
		System.out.println("---- benchmarking....\n");
		
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
