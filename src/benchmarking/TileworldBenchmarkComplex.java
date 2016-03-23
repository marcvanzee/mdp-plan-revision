package benchmarking;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;

import constants.MathOperations;
import gui.Main;
import mdp.agent.ReactionStrategy;
import mdp.algorithms.AlgorithmType;
import settings.LearningSettings;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmarkComplex {
	
	private TileworldSimulation simulation;

	int[] worldSize = {5,10,15,20},
				holeGestTimeMin = {1,5,10,30,100},
				holeGestTimeMax = {1,5,10,30,100},
				holeLifeExpMin = {1,5,10,30,100},
				holeLifeExpMax = {1,5,10,30,100},		
				holeScoreMin = {20}, holeScoreMax = {80},
				wallSizeMin = {2}, wallSizeMax = {4},
				
				initNrHoles = {0,1,3,10,20},
				initNrWalls = {0},
				planningTime = {1};
	
	int simrep = 20, simlength = 80000;
	
	PrintWriter writer = null;
	String file = "worldID.txt";
	
	public static void main(String args[]) {
		try {
			(new TileworldBenchmarkComplex()).go();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | FileNotFoundException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void go() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, FileNotFoundException, SQLException, ClassNotFoundException {
		try {
			Main.loadSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}

		benchmark();

	}

	public void benchmark() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, FileNotFoundException, SQLException, ClassNotFoundException {
		
		try {
			writer = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		int totalSims = 	worldSize.length * 		holeGestTimeMin.length *	holeGestTimeMax.length * 
							holeLifeExpMin.length * holeLifeExpMax.length * 	holeScoreMin.length * 
							holeScoreMax.length *	initNrHoles.length * 		wallSizeMin.length * 		
							wallSizeMax.length * 	initNrWalls.length * 		planningTime.length;
		
		output("% Benchmarking parameters:\n"
				+ "% worldSize: " + arr(worldSize) + "\n"
				+ "% holeGestTimeMin: " + arr(holeGestTimeMin) + "\n"
				+ "% holeGestTImeMax: " + arr(holeGestTimeMax) + "\n"
				+ "% holeLifeExpMin: " + arr(holeLifeExpMin) + "\n"
				+ "% holeLifeExpMax: " + arr(holeLifeExpMax) + "\n"
				+ "% holeScoreMin: " + arr(holeScoreMin) + "\n"
				+ "% holeScoreMax: " + arr(holeScoreMax) + "\n"
				+ "% wallSizeMin: " + arr(wallSizeMin) + "\n"
				+ "% wallSizeMax:" + arr(wallSizeMax) + "\n"
				+ "% initialNrHoles: " + arr(initNrHoles) + "\n"
				+ "% initalNrWalls: " + arr(initNrWalls) + "\n" 
				+ "% planningTime: " + arr(planningTime) + "\n"
				+ "% ---------------------------------------------\n"
				+ "% total benchmarks: " + totalSims + "\n"
				+ "% simulation repetitions per benchmarks: " + simrep + "\n"
				+ "% simulation length: " + simlength + "\n"
				+ "% total number of simulation steps: " + ((long) totalSims * (long) simrep * (long)simlength) + "\n\n"
				+ "% RESULTS: worldSize,holeGestTimeMin,holeGestTimeMax,holeLifetimeMin,holeLifetimeMax,holeScoreMin,holeScoreMax,"
					+ "wallSizeMin,wallSizeMax,initialNrHoles,initialNrWalls,planningTime,effBold,effAny,effCloser,winner(0=bold,1=any_hole,2=closer_hole),winner(-1,0,1,2 (-1=no winner))\n");
				
		Class.forName("com.mysql.jdbc.Driver") ;
		Connection conn = DriverManager.getConnection("jdbc:mysql://95.170.70.167:3306/marcvanzee_nl_bijles", "marcv_nl_bijles", "westsidE1") ;
		Statement stmt = conn.createStatement() ;
		String query = "SELECT * FROM mdp_param_space WHERE free=true AND resultID=null ORDER BY worldSize ASC LIMIT 1;";
		ResultSet rs = stmt.executeQuery(query) ;
		
		/*
		
		for (int ws : worldSize) {
			TileworldSettings.WORLD_SIZE = ws;
			for (int hgtmin : holeGestTimeMin) {
				TileworldSettings.HOLE_GESTATION_TIME_MIN = hgtmin;
				for (int hgtmax : holeGestTimeMax) {
					TileworldSettings.HOLE_GESTATION_TIME_MAX = hgtmax;
					for (int lifemin : holeLifeExpMin) {
						TileworldSettings.HOLE_LIFE_EXP_MIN = lifemin;
						for (int lifemax : holeLifeExpMax) {
							TileworldSettings.HOLE_LIFE_EXP_MAX = lifemax;
							for (int hsmin : holeScoreMin) {
								TileworldSettings.HOLE_SCORE_MIN = hsmin;
								for (int hsmax : holeScoreMax) {
									TileworldSettings.HOLE_SCORE_MAX = hsmax;
									for (int inith : initNrHoles) {
										TileworldSettings.INITIAL_NR_HOLES = inith;
										for (int wsmin : wallSizeMin) {
											TileworldSettings.WALL_SIZE_MIN = wsmin;
											for (int wsmax : wallSizeMax) {
												TileworldSettings.WALL_SIZE_MAX = wsmax;
												for (int initw : initNrWalls) {
													TileworldSettings.INITIAL_NR_WALLS = initw;
													for (int pt : planningTime) {
														TileworldSettings.PLANNING_TIME = pt;
														
														if (!constraintsViolated()) { singleBenchmark(); }
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (writer != null) {
			writer.close();
		}*/
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
	
	private void singleBenchmark() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// run for three reactive agents and record effectiveness
		TileworldSettings.ALGORITHM = AlgorithmType.SHORTEST_PATH;
		TileworldSettings.BOLDNESS = -1;
		TileworldSettings.USE_REACTION_STRATEGY = true;
		
		double eff0, eff1, eff2;
		
		// bold
		TileworldSettings.REACTION_STRATEGY = ReactionStrategy.TARGET_DISAPPEARS; 
		eff0 = benchmarkReactive();
		// any hole
		TileworldSettings.REACTION_STRATEGY = ReactionStrategy.TARGET_DIS_OR_ANY_HOLE;
		eff1 = benchmarkReactive();
		
		// closer hole
		TileworldSettings.REACTION_STRATEGY = ReactionStrategy.TARGET_DIS_OR_NEARER_HOLE;
		eff2 = benchmarkReactive();
		int winner = (eff0 == eff1 && eff1 == eff2) ? -1 : (eff0 > eff1 ? (eff2 > eff0 ? 2 : 0) : (eff2 > eff1 ? 2 : 1));
		
		// run learning agent		
		//TileworldSettings.ALGORITHM = AlgorithmType.LEARNING;
		//benchmarkLearner();
		
		output(parametersToString() +","+eff0+","+eff1+","+eff2+","+winner);
	}
	
	private double benchmarkReactive() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchMethodException, SecurityException {
		double totalEff = 0;
	
		for (int simCount = 0; simCount < simrep; simCount++) {
			simulation = new TileworldSimulation();
			simulation.buildNewModel();
			simulation.startSimulation(simlength);
	
			double score = simulation.getAgentScore(), maxScore = simulation.getMaxScore(),
					effectiveness = (double) score / (double) maxScore;
			totalEff += effectiveness;
		}
	
		totalEff /= (double) simrep;
		
		return totalEff;
	}

	public void benchmarkLearner() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		int minT = 5, maxT = 300, tStep = 5;
		int rep = 1000;
		
		HashMap<ReactionStrategy,Double> results = new HashMap<ReactionStrategy,Double>();
		
		for (int t = minT; t <= maxT; t+=tStep) {
			for (ReactionStrategy rs : ReactionStrategy.values()) {
				results.put(rs, 0.0);
			}
			LearningSettings.TEMP_DECREASE_STEPS = t;
			for (int simCount = 0; simCount < rep; simCount++) {
				simulation = new TileworldSimulation();
				simulation.buildNewModel();
				simulation.startSimulation(Integer.MAX_VALUE);
				
				ReactionStrategy rs = simulation.getLearnedStrategy();
				results.put(rs, results.get(rs)+1.0);
			}
			for (ReactionStrategy rs : ReactionStrategy.values()) {
				System.out.print(MathOperations.round((results.get(rs)/simrep)*100,2) + ",");
			}
		}
	}
	
	private void output(String str) { output1(str+"\n"); }
	
	private void output1(String str) {
		System.out.print(str);
		if (writer != null) {
			writer.print(str);
		}
	}
	
	private String arr(int []arr) {
		return Arrays.toString(arr);
	}
	
	private String parametersToString() {
		return TileworldSettings.WORLD_SIZE + "," 
				+ TileworldSettings.HOLE_GESTATION_TIME_MIN + "," + TileworldSettings.HOLE_GESTATION_TIME_MAX + ","
				+ TileworldSettings.HOLE_LIFE_EXP_MIN + "," + TileworldSettings.HOLE_LIFE_EXP_MAX + ","
				+ TileworldSettings.HOLE_SCORE_MIN + "," + TileworldSettings.HOLE_SCORE_MAX + ","
				+ TileworldSettings.WALL_SIZE_MIN + "," + TileworldSettings.WALL_SIZE_MAX + ","
				+ TileworldSettings.INITIAL_NR_HOLES + "," + TileworldSettings.INITIAL_NR_WALLS + ","
				+ TileworldSettings.PLANNING_TIME;
	}
}
