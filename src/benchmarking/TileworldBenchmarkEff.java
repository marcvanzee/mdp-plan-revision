package benchmarking;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import gui.Main;
import mdp.agent.ReactionStrategy;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmarkEff 
{
	private TileworldSimulation simulation;
	public static int countDif = 0;
	final Random r = new Random();
	
	public static void main(String args[])
	{
		try {
			(new TileworldBenchmarkEff()).go();
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
	
	public int getRandEl(int arr[]) {
		return arr[r.nextInt(arr.length)];
	}
	
	private boolean constraintsViolated() {
		return (TileworldSettings.HOLE_GESTATION_TIME_MAX < TileworldSettings.HOLE_GESTATION_TIME_MIN) ||
				(TileworldSettings.HOLE_LIFE_EXP_MAX < TileworldSettings.HOLE_LIFE_EXP_MIN) ||
				(TileworldSettings.HOLE_SCORE_MAX < TileworldSettings.HOLE_SCORE_MIN) ||
				(TileworldSettings.WALL_SIZE_MAX < TileworldSettings.WALL_SIZE_MIN) ||
				(TileworldSettings.WORLD_SIZE*TileworldSettings.WORLD_SIZE <= 
								TileworldSettings.INITIAL_NR_HOLES + TileworldSettings.WALL_SIZE_MAX*TileworldSettings.INITIAL_NR_WALLS + TileworldSettings.INITIAL_NR_HOLES) ||
				(TileworldSettings.WORLD_SIZE*TileworldSettings.WORLD_SIZE <= TileworldSettings.WALL_SIZE_MAX*TileworldSettings.INITIAL_NR_WALLS*2);
	}
	
	private void generateRandomWorld() {
		int ws[] = {5,10,15,20},
		hsttime[] = {1,3,5,10,30,100,300},
		initnrholes[] = {1,2,3,5,10};

		TileworldSettings.WORLD_SIZE = getRandEl(ws);
		TileworldSettings.HOLE_GESTATION_TIME_MAX = getRandEl(hsttime);
		TileworldSettings.HOLE_GESTATION_TIME_MIN = getRandEl(hsttime);
		TileworldSettings.HOLE_LIFE_EXP_MAX = getRandEl(hsttime);
		TileworldSettings.HOLE_LIFE_EXP_MIN = getRandEl(hsttime);
		TileworldSettings.INITIAL_NR_HOLES = getRandEl(initnrholes);
		TileworldSettings.INITIAL_NR_WALLS = getRandEl(initnrholes);
		TileworldSettings.PLANNING_TIME = getRandEl(initnrholes);
	}
	
	public void benchmark() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {		
		
		TileworldSettings.BOLDNESS = -1;
		TileworldSettings.USE_REACTION_STRATEGY = true;
		TileworldSettings.REACTION_STRATEGY = ReactionStrategy.TARGET_DISAPPEARS;
		
		do {
			generateRandomWorld();
		} while (constraintsViolated());
				
		for (int i = 1000; i<80000; i+=2000) {
			double sum = 0;
			double results[] = new double[5];
			for (int j =0; j<5; j++) {
				simulation = new TileworldSimulation();
				simulation.buildNewModel();
				simulation.startSimulation(i);
					
				double score = simulation.getAgentScore(),
						maxScore = simulation.getMaxScore();
				results[j] = (double) score / (double) maxScore;
				sum += results[j];
			}
			
			double average = sum/5;
			System.out.println(i + "," + average);
		}
	}
}
