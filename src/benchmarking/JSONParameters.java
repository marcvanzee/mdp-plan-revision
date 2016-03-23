package benchmarking;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class JSONParameters extends GenericJson {
	public static final String DONE = "done";
	public static final String WORLDSIZE = "worldSize";
	public static final String HOLEGESTTIMEMIN = "holeGestTimeMin";
	public static final String HOLEGESTTIMEMAX = "holeGestTimeMax";
	public static final String HOLELIFEXPMIN = "holeLifeExpMin";
	public static final String HOLELIFEXPMAX = "holeLifeExpMax";
	public static final String HOLESCOREMIN = "holeScoreMin";
	public static final String HOLESCOREMAX = "holeScoreMax";
	public static final String WALLSIZEMIN = "wallSizeMin";
	public static final String WALLSIZEMAX = "wallSizeMax";
	public static final String INITNRHOLES = "initNrHoles";
	public static final String INITNRWALLS = "initNrWalls";
	public static final String PLANNINGTIME = "planningTime";
	public static final String ID = "id";
	
		@Key(DONE)
		public boolean done;
		
		@Key(WORLDSIZE)
		public int worldSize;
		
		@Key(HOLEGESTTIMEMIN)
		public  int holeGestTimeMin;
		
		@Key(HOLEGESTTIMEMAX)
		public  int holeGestTimeMax;
		
		@Key(HOLELIFEXPMIN)
		public  int holeLifeExpMin;
		
		@Key(HOLELIFEXPMAX)
		public  int holeLifeExpMax;
		
		@Key(HOLESCOREMIN)
		public  int holeScoreMin;
		
		@Key(HOLESCOREMAX)
		public  int holeScoreMax;
		
		@Key(WALLSIZEMIN)
		public  int wallSizeMin;
		
		@Key(WALLSIZEMAX)
		public  int wallSizeMax;
		
		@Key(INITNRHOLES)
		public  int initNrHoles;
		
		@Key(INITNRWALLS)
		public  int initNrWalls;
		
		@Key(PLANNINGTIME)
		public  int planningTime;
		
		@Key(ID)
		public int id;
	}