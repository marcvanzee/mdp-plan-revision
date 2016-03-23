package simulations;

import java.util.HashMap;
import java.util.LinkedList;

import constants.Printing;
import mdp.Tileworld;
import mdp.agent.MetaAction;
import mdp.elements.State;
import settings.TileworldSettings;

/**
 *  This is a tileworld simulation that is concise and efficient, it's used to create many hypothetical simulations
 * @author marc.vanzee
 *
 *
 *
 * IDEA:
 * - compute distance and shortest path from every node to every other node and store in both representations (optimized and normal)
 * 
 * https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
 * 
 * 
 * - create optimized TW using int[][] where
 * 		 -1 = obstacle
 * 		 >0 = hole where number represents reward 
 * - planning is then simply a lookup
 */

public class MinimalTileworldSimulation 
{	
	// ===== variables that remain constant during simulations
	static int[] tw, obstacles;
	static int[][] dist, next;
	
	// store all plans in a map so we don't have to recompute them every time
	public static HashMap<Integer,LinkedList<Integer>> planMap = new HashMap<Integer,LinkedList<Integer>>();
	
	// create minimal tileworld from complex tileworld
	// only add obstacles (no holes and agents)
	public MinimalTileworldSimulation(Tileworld tileworld)
	{
		Printing.minsim("new tileworld");
		tileworldToIntArray(tileworld);
		
		floydWarshallWithPathReconstruction();
	}
	
	public void tileworldToIntArray(Tileworld tileworld)
	{		
		int ws = TileworldSettings.WORLD_SIZE;
		tw = new int[ws * ws];
		obstacles = new int[tileworld.countObstacles()];
		
		State [][] stateArr = tileworld.getStateArray();
		
		int obsIndex = 0;
		
		for (int i=0; i<ws; i++)
		{
			for (int j=0; j<ws; j++)
			{
				int index = getPos(i,j);
				
				if (stateArr[i][j].isObstacle())
				{
					tw[index] = -1;
					obstacles[obsIndex] = index;
					obsIndex++;
				}
				else
				{
					tw[index] = 0;
				}
			}
		}
	}
		
	public MetaAction computeOptimalAction(TileworldSimulation tw)
	{
		Printing.minsim("computing optimal action.");
		Hypothesis h = new Hypothesis(tw);
		
		int actScore=0, delScore=0;
		
		int hCount = TileworldSettings.HYPOTHESIS_REPETITIONS;
		
		Printing.minsim("creating " + hCount*2 + " simulations");
		for (int i=0; i<hCount; i++)
		{
			// create two new hypothesis for acting and deliberating
			// and add their scores
		
			Printing.minsim("creating think/act simulations " + (i+1));
			Hypothesis hypAct = new Hypothesis(h, MetaAction.ACT),
					hypThink = new Hypothesis(h, MetaAction.DELIBERATE);
			
			SimThread actThread = new SimThread(hypAct),
					delThread = new SimThread(hypThink);
			
			Thread t1 = new Thread(actThread),
					t2 = new Thread(delThread);
			
			try {
				t1.start();
				t1.join();
				
				t2.start();
								
				t2.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			actScore += actThread.getMaxScore();
			delScore += delThread.getMaxScore();
		}
				
		Printing.angel("finished hypothesis. max actScore="+actScore+", max thinkScore="+delScore);
		
		// prefer acting
		return (actScore >= delScore ? MetaAction.ACT : MetaAction.DELIBERATE);
	}
		
	public void floydWarshallWithPathReconstruction()
	{
		int ws = TileworldSettings.WORLD_SIZE;
		dist = new int[ws * ws][ws * ws];
		next = new int[ws * ws][ws * ws];
		
		for (int i=0; i<dist.length; i++) 
		{
			dist[i] = new int[ws * ws];
			next[i] = new int[ws * ws];
		}
		
		// initialize distances for neighbors and self-loops
		for (int i=0; i<tw.length; i++) 
		{
			for (int j=0; j<tw.length; j++)
			{
				if (i == j) 
					dist[i][j] = 0;
				
				else if (isNeighbor(i,j) && tw[i] != -1 && tw[j] != -1) {
					dist[i][j] = 1;
					next[i][j] = j;
				}					
				
				else {
					dist[i][j] = Integer.MAX_VALUE;
					next[i][j] = -1;
				}
			}
		}
		
		// run the algorithm
		for (int k=0; k<tw.length; k++) 
		{
			for (int i=0; i<tw.length; i++)
			{
				for (int j=0; j<tw.length; j++)
				{
					int sum = canAdd(dist[i][k], dist[k][j]) ? dist[i][k] + dist[k][j] : Integer.MAX_VALUE;
						
					if (dist[i][j] > sum) {
						dist[i][j] = sum;
						next[i][j] = next[i][k];
					}					
				}
			}
		}
		
		// add all plans
		for (int i=0; i<tw.length; i++)
		{
			for (int j=0; j<tw.length; j++)
			{
				if (tw[i] == -1 || tw[j] == -1 || i == j)
					continue;
				path(i, j);
			}
		}
	}
	
	public static LinkedList<Integer> path(int p1, int p2)
	{
		int paired = pair(p1, p2);
		
		if (planMap.containsKey(paired))
			return planMap.get(paired);
				
		LinkedList<Integer> path = new LinkedList<Integer>();
		
		if (p1 < 0 || p2 < 0 || p1 >= next.length || p2 >= next[p1].length)
		{
			return path;
		}
		
		if (next[p1][p2] == 0)
			return path;
		
		//path.add(p1);
		
		while (p1 != p2)
		{
			if (p1 < 0 || p2 < 0 || p1 >= next.length || p2 >= next[p1].length)
			{
				path.clear();
				return path;
			}
			p1 = next[p1][p2];
			path.add(p1);
		}
		
		planMap.put(paired, path);
		
		return path;
			
	}
	
	public static int pair(int x, int y)
	{
		return (int) (0.5 * (x+y) * (x+y+1) + y);
	}
	
	public boolean canAdd(int... values) {
	    long longSum = 0;
	    int intSum = 0;
	    for (final int value: values) {
	        intSum += value;
	        longSum += value;
	    }
	    return intSum == longSum;
	}
	
	// compute all neighbors of p and see whether q is one of them
	public boolean isNeighbor(int p, int q)
	{ 
		int pX = getX(p), pY = getY(p), qX = getX(q), qY = getY(q);
		
		int startPosX = (pX - 1 < 0) ? pX : pX-1;
		int startPosY = (pY - 1 < 0) ? pY : pY-1;
		int endPosX =   (pX + 1 > TileworldSettings.WORLD_SIZE) ? pX : pX+1;
		int endPosY =   (pY + 1 > TileworldSettings.WORLD_SIZE) ? pY : pY+1;

		// See how many are alive
		for (int rowNum=startPosX; rowNum<=endPosX; rowNum++) {
		    for (int colNum=startPosY; colNum<=endPosY; colNum++) {
		    	if (qX == rowNum && qY == colNum && (qX == pX || qY == pY))
		    		return true;
		    }
		}
		
		return false;		
	}
	
	public int getX(int pos)
	{
		return pos/TileworldSettings.WORLD_SIZE;
	}
	
	public int getY(int pos)
	{
		return pos % TileworldSettings.WORLD_SIZE;
	}
	
	public int getPos(int x, int y)
	{
		return y*TileworldSettings.WORLD_SIZE + x;
	}
		
	public int getScore()
	{
		return 0;
	}
	
	public String toString()
	{
		String ret = "[";
		
		for (int i=0; i<tw.length; i++)
		{
			if (i%TileworldSettings.WORLD_SIZE == 0 && i>0) ret += "]\n[";
			ret += (tw[i]==0?" ":"")+tw[i]+", ";
		}
		ret += "]";
		
		return ret;
	}
	
	public static String toStringWithHyp(Hypothesis h)
	{
		String ret = "\n[";
		
		for (int i=0; i<tw.length; i++)
		{
			if (i%TileworldSettings.WORLD_SIZE == 0 && i>0) 
				ret += "]\n[";
			
			if (i == h.agPos)
				ret += " A, ";
			
			else if (h.holes.contains(i)) {
				int score = h.holeScore.get(i);
				ret += (score > 9 ? score : " "+score) + ", ";
			}
			
			else 
				ret += (tw[i]==0?" ":"")+tw[i]+", ";
		}
		ret += "]";
		
		return ret;
	}

}
