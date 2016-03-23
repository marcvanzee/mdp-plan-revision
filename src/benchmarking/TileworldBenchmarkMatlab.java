package benchmarking;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.ws.Holder;

import gui.Main;
import gui.tileworld.TileworldGUI;
import mdp.agent.ReactionStrategy;
import settings.BenchmarkSettings;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmarkMatlab {
	private TileworldSimulation simulation;

	public static void main(String args[]) {
		try {
			(new TileworldBenchmarkMatlab()).go();
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

		boolean log = false;
		String AXIS = "planning_time hole_lifetime effectiveness";
		String TYPES = "bold reactive_nearer_hole";
		
		int xMin =5, xMax = 40; double xStep = 8;
		int yMin = 10, yMax = 20, yStep = 2;
		
		if (log) {
			xStep = (Math.log10(xMax) - Math.log10(xMin)) / ((xMax-xMin)/xStep);
		}
		
		String file = "../../../matlab/metareasoning/output.txt";

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String str = "TYPES " + TYPES + "\n" 
				+ "AXIS " + AXIS + "\n"
				+ (log ? "LOG\n" : "") 
				+ "END\n\n"
				+ "\n\n" + "% --- TILEWORLD SETTINGS:\n"
//				+ "% world size = " + TileworldSettings.WORLD_SIZE + "\n"
				+ "% holeGestationTimeMin = " + TileworldSettings.HOLE_GESTATION_TIME_MIN + "\n"
				+ "% holeGestationTimeMax = " + TileworldSettings.HOLE_GESTATION_TIME_MAX + "\n"
//				+ "% holeLifetimeMin = " + TileworldSettings.HOLE_LIFE_EXP_MIN + "\n"
//				+ "% holeLifetimeMax = " + TileworldSettings.HOLE_LIFE_EXP_MAX + "\n"
				+ "% holeScoreMin = " + TileworldSettings.HOLE_SCORE_MIN + "\n"
				+ "% holeScoreMax = " + TileworldSettings.HOLE_SCORE_MAX + "\n"
				+ "% initialNrHoles = " + TileworldSettings.INITIAL_NR_HOLES + "\n" 
				+ "% wallSizeMin = " + TileworldSettings.WALL_SIZE_MIN + "\n" 
				+ "% wallSizeMax = " + TileworldSettings.WALL_SIZE_MAX + "\n" 
				+ "% initialNrWalls = 8% of total surface\n" 
				+ "% dynamism = " + TileworldSettings.DYNAMISM+ "\n" 
				+ "% planningTime = " + TileworldSettings.PLANNING_TIME + "\n"
				+ "% --- RESULTS:\n"
				+ "% planning time, hole lifetime [std=2], type, effectiveness\n";

		System.out.println(str);
		writer.println(str);

		// HashMap<Integer,HashMap<Integer,Integer>> bestStrategy =
		// parseResultsFrom("data2.txt");
		int iterations = 0;
		int x = xMin;
		
		

		while (x <= xMax) {
			for (int y = yMin; y <= yMax; y += yStep) {
				TileworldSettings.WORLD_SIZE = x;
				TileworldSettings.HOLE_LIFE_EXP_MIN = y-1;
				TileworldSettings.HOLE_LIFE_EXP_MAX = y+1;
								
				TileworldSettings.USE_REACTION_STRATEGY = false;
				TileworldSettings.BOLDNESS = -1;
				
				//TileworldSettings.INITIAL_NR_WALLS = 
				
				benchmarkAgent(x, y, 0, writer);
				
				TileworldSettings.USE_REACTION_STRATEGY = true;
				TileworldSettings.REACTION_STRATEGY = ReactionStrategy.TARGET_DIS_OR_NEARER_HOLE;
				benchmarkAgent(x, y, 1, writer);
		
				// optimized reactive
				// TileworldSettings.REACTION_STRATEGY =
				// (bestStrategy.get(d).get(p) == 0 ?
				// ReactionStrategy.TARGET_DISAPPEARS :
				// bestStrategy.get(d).get(p) == 1 ?
				// ReactionStrategy.TARGET_DIS_OR_NEARER_HOLE :
				// ReactionStrategy.TARGET_DIS_OR_ANY_HOLE);

			}
			int newX = x;
			while (newX == x) {
				iterations++;
				newX = (int) (log ? xMin + Math.pow(10.0, iterations * xStep) : x + xStep);
			}
			x = newX;
		}

		writer.close();
	}

	public HashMap<Integer, HashMap<Integer, Integer>> parseResultsFrom(String file) {
		HashMap<Integer, HashMap<Integer, Integer>> ret = new HashMap<Integer, HashMap<Integer, Integer>>();

		/// Input file which needs to be parsed
		String fileToParse = "data2.txt";
		BufferedReader fileReader = null;

		// Delimiter used in CSV file
		final String DELIMITER = ",";
		try {
			String line = "";
			// Create the file reader
			fileReader = new BufferedReader(new FileReader(fileToParse));

			// Read the file line by line
			while ((line = fileReader.readLine()) != null) {
				// Get all tokens available in line
				String[] tokens = line.split(DELIMITER);
				if (tokens.length != 3 || (tokens.length > 0 && tokens[0].startsWith("%"))) {
					continue;
				}

				int dyn = Integer.parseInt(tokens[0]), pCost = Integer.parseInt(tokens[1]),
						best = Integer.parseInt(tokens[2]);

				if (!ret.containsKey(dyn)) {
					ret.put(dyn, new HashMap<Integer, Integer>());
				}
				ret.get(dyn).put(pCost, best);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public void benchmarkAgent(int x, int y, int id, PrintWriter writer) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		double totalEff = 0;
		int simLength = BenchmarkSettings.SIMULATION_LENGTH, simRep = BenchmarkSettings.REPETITIONS;

		for (int simCount = 0; simCount < simRep; simCount++) {
			simulation = new TileworldSimulation();
			simulation.buildNewModel();
			simulation.startSimulation(simLength);

			double score = simulation.getAgentScore(), maxScore = simulation.getMaxScore(),
					effectiveness = (double) score / (double) maxScore;
			totalEff += effectiveness;
		}

		totalEff /= (double) simRep;
		
		String output = x + "," + y + "," + id + "," + totalEff;
		System.out.println(output);
		writer.println(output);
	}
}
