package benchmarking;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import constants.MathOperations;
import gui.Main;
import mdp.agent.ReactionStrategy;
import mdp.algorithms.AlgorithmType;
import settings.BenchmarkSettings;
import settings.LearningSettings;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmarkLearningAgent {
	private TileworldSimulation simulation;

	public static void main(String args[]) {
		try {
			(new TileworldBenchmarkLearningAgent()).go();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void go() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, FileNotFoundException {
		try {
			Main.loadSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}

		benchmark();

	}

	public void benchmark() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, FileNotFoundException {

		boolean log = false, write = false;
		String AXIS = "simulation_length effectiveness";
		String TYPES = "bold reactive_nearer_hole reactive_any_hole learning";
		
		String file = "../../../matlab/metareasoning/output.txt";

		PrintWriter writer = null;
		
		if (write) {
			try {
				writer = new PrintWriter(file, "UTF-8");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		String str = "TYPES " + TYPES + "\n" 
				+ "AXIS " + AXIS + "\n"
				+ "END\n\n"
				+ "\n\n" + "% --- TILEWORLD SETTINGS:\n"
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
				+ "% planningTime = " + TileworldSettings.PLANNING_TIME + "\n"
				+ "% --- RESULTS:\n"
				+ "t,bold percentage,closer_hole percentage,any_hole percentage\n";

		System.out.println(str);
		if (writer != null) {
			writer.println(str);
		}

		
//		TileworldSettings.ALGORITHM = AlgorithmType.SHORTEST_PATH;
//		TileworldSettings.BOLDNESS = -1;
//		TileworldSettings.USE_REACTION_STRATEGY = true;
//		
//		ReactionStrategy rss[] = ReactionStrategy.values();
//		
//		for (int i=0; i<rss.length; i++){
//			TileworldSettings.REACTION_STRATEGY = rss[i];
//			benchmarkAgent(i, writer);
//		}		
		
		TileworldSettings.ALGORITHM = AlgorithmType.LEARNING;
		benchmarkAgent(3, writer);
		
		if (writer != null) {
			writer.close();
		}
	}

	public void benchmarkAgent(int id, PrintWriter writer) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		int simRep = BenchmarkSettings.REPETITIONS,
				minT = 5, maxT = 300, tStep = 5;
		
		HashMap<ReactionStrategy,Double> results = new HashMap<ReactionStrategy,Double>();
		
		for (int t = minT; t <= maxT; t+=tStep) {
			for (ReactionStrategy rs : ReactionStrategy.values()) {
				results.put(rs, 0.0);
			}
			LearningSettings.TEMP_DECREASE_STEPS = t;
			for (int simCount = 0; simCount < simRep; simCount++) {
				simulation = new TileworldSimulation();
				simulation.buildNewModel();
				simulation.startSimulation(Integer.MAX_VALUE);
								
				ReactionStrategy rs = simulation.getLearnedStrategy();
				results.put(rs, results.get(rs)+1.0);
				
				//String output = "   " + ReactionStrategy.values()[id] + ": " + effectiveness;
				//System.out.println(output);
			}
			System.out.print(t+",");
			for (ReactionStrategy rs : ReactionStrategy.values()) {
				System.out.print(MathOperations.round((results.get(rs)/simRep)*100,2) + ",");
			}
			System.out.println();
		}
	}
}
