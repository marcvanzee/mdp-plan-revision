package benchmarking;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import constants.MathOperations;
import mdp.agent.ReactionStrategy;
import mdp.algorithms.AlgorithmType;
import settings.LearningSettings;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmarkJSON {
	
	private TileworldSimulation simulation;
	
	final int simrep = 5, simlength = 15000;
	
	//static final String GET_URL = "http://www.marcvanzee.nl/mdp-json/getParameters.php",
	//		PUT_URL = "http://www.marcvanzee.nl/mdp-json/addResult.php";
	static final String GET_URL = "http://www.marcvanzee.nl/mdp-json/getParameters.php",
					PUT_URL = "http://www.marcvanzee.nl/mdp-json/addResult.php";
	
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	static int ID;
	
	private static void parseResponse(HttpResponse response) throws IOException {
		JSONParameters params = response.parseAs(JSONParameters.class);
		
		System.out.println(params);
		
		ID = params.id;
		TileworldSettings.copyValues(params);
	}
	
	private static void printResponse(HttpResponse hr) throws IOException {
		java.util.Scanner s = new java.util.Scanner(hr.getContent()).useDelimiter("\\A");
		System.out.println(s.hasNext() ? s.next() : "");
	}
	
	public static void main(String args[]) {
		try {
			(new TileworldBenchmarkJSON()).go();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public void go() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		HttpRequestFactory requestFactory =
					HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
						@Override
						public void initialize(HttpRequest request) throws IOException {
							request.setParser(new JsonObjectParser(JSON_FACTORY));
						}
					});
		
		GenericUrl url = new GenericUrl(new java.net.URL(GET_URL));
		
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		System.out.println("Tileworld JSON Benchmarker. Using handle: " + GET_URL + "\n\n");
//				+ "% RESULTS: worldSize,holeGestTimeMin,holeGestTimeMax,holeLifetimeMin,holeLifetimeMax,holeScoreMin,holeScoreMax,"
//					+ "wallSizeMin,wallSizeMax,initialNrHoles,initialNrWalls,planningTime,effBold,effAny,effCloser,winner(0=bold,1=any_hole,2=closer_hole,-1=no winner)\n");
//		
		int count = 0;
		while (true) {
			try {
				HttpResponse ht = request.execute();
				parseResponse(ht);
				
				HashMap<String,Object> results = singleBenchmark();
				
				GenericUrl url2 = new GenericUrl(new java.net.URL(PUT_URL));
				url2.put("id", ID);
				url2.putAll(results);
			
				requestFactory.buildGetRequest(url2).execute();
			//printResponse(hr2);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("***** Detected socket read error. Possibly we're overloading the server. Sleeping for 2min...");
				try {
					Thread.sleep(120000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("done! Retrying...");
				continue;
			}
			
			
			System.out.print(".");
			count++;
			if (count % 100 == 0) { System.out.println(count); };
		}
	}

	
	private HashMap<String,Object> singleBenchmark() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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
		
		HashMap<String,Object> ret = new HashMap<String,Object>();
		ret.put("effBold", MathOperations.round(eff0, 4));
		ret.put("effAny", MathOperations.round(eff1, 4));
		ret.put("effCloser", MathOperations.round(eff2, 4));
		ret.put("winner", winner);
		
		return ret;
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
}
