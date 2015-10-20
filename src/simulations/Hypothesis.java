package simulations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import constants.MathOperations;
import constants.Printing;
import mdp.Tileworld;
import mdp.agent.Agent;
import mdp.agent.Angel;
import mdp.agent.MetaAction;
import mdp.elements.Action;
import mdp.elements.State;
import settings.TileworldSettings;

// a hypothesis contains all the values that change during a simulation
public class Hypothesis 
{
	int nextHole, agPos, score, steps, depth;
	Set<Integer> holes = new HashSet<Integer>();
	HashMap<Integer, Integer> holeLife = new HashMap<Integer,Integer>();
	HashMap<Integer, Integer> holeScore = new HashMap<Integer,Integer>();
	double planningDelay;
	boolean delay; // for planningdelay = 0.5
	LinkedList<Integer> plan; // a list of integers, representing positions in tw[]
	MetaAction metaAction;
	
	public Hypothesis() {}
	
	public Hypothesis(Hypothesis h, MetaAction ma)
	{
		nextHole = h.nextHole;
		agPos = h.agPos;
		score = h.score;
		steps = h.steps;
		depth = h.depth-1;
		holes = new HashSet<Integer>(h.holes);
		holeLife = new HashMap<Integer,Integer>(h.holeLife);
		holeScore = new HashMap<Integer,Integer>(h.holeScore);
		planningDelay = h.planningDelay;
		delay = h.delay;
		plan = new LinkedList<Integer>(h.plan);
		metaAction = ma;
	}
	
	public Hypothesis(TileworldSimulation tws)
	{
		Tileworld tileworld = tws.getTileworld();
		Agent ag = tws.getAgent();
		
		// add holes and their lifetime
		for (State s : tileworld.getHoles()) 
		{
			int pos = getPos(s.getX(),s.getY());
			holes.add(pos);
			holeLife.put(pos, s.getLifetime());
			holeScore.put(pos, (int) s.getReward());
		}
		
		// add agent position
		State agState = ag.getCurrentState();
		agPos = getPos(agState.getX(), agState.getY());
		
		nextHole = tws.getNextHole();
		
		// current step
		steps = tws.getSteps();
		
		// TODO: unsafe cast!
		planningDelay = ((Angel) ag).getPlanningDelay();
		delay = ((Angel) ag).isDelayed();
		
		// copy the plan
		plan = new LinkedList<Integer>();
		planToIntArray(((Angel) ag).getPlan());
		
		depth = TileworldSettings.HYPOTHESIS_DEPTH;
		
		MinimalTileworldSimulation.printWithHyp(this);
	}
	
	public void planToIntArray(List<Action> agPlan)
	{
		plan.clear();
		
		int x = getX(agPos), y = getY(agPos);
		
		for (Action a : agPlan)
		{
			switch (a.getActionType())
			{
			case UP: y--; break;
			case DOWN: y++; break;
			case LEFT: x--; break;
			case RIGHT: x++; break;
			}
			
			plan.add(getPos(x,y));
		}
	}
	
	// run the hypothesis until the agent has performed the meta action
	public void step()
	{
		if (metaAction == null)
		{
			Printing.hyp("no meta action to perform in hypothesis!");
			return;
		}
		
		while (metaAction != null)
		{
			Printing.hyp("step");
			
			if (nextHole <= 0) 
			{
				Printing.hyp("adding hole");
				addHole();
				removeHoleIfVisited();
				setNextHole();
				Printing.hyp("next hole: " + nextHole);
			}
			
			if (steps % TileworldSettings.DYNAMISM == 0) 
			{
				agentStep();
				metaAction = null;
			}
			
			steps++;
			nextHole--;
			
			decreaseLifetimeHoles();
			removeHoleIfVisited();
		}
	}
	
	private void decreaseLifetimeHoles() 
	{
		final List<Integer> toRemove = new LinkedList<Integer>();
		
		for (int hole : holes) {
			int life = holeLife.get(hole);
			
			if (life <= 1)
				toRemove.add(hole);
			else
				holeLife.put(hole, life-1);
		}
		
		for (int hole : toRemove) { 
			holes.remove(hole);
			holeLife.remove(hole);
			holeScore.remove(hole);
		}
	}
	
	public void agentStep()
	{
		Printing.hyp("agent step");
		
		if (planningDelay == 0.5 && !delay)
		{
			delay = !delay;
			planningDelay = 0;
		}
		
		// do nothing when we are thinking
		if (planningDelay == 0.5 && delay)
		{
			planningDelay = 0;
			delay = !delay;
		}
		else if (planningDelay >= 1)
		{
			planningDelay--;
		}
		// otherwise execute the meta-action
		// this should never occur
		else if (plan.size() == 0 && metaAction != MetaAction.DELIBERATE)
		{
			System.out.println("I am instructed to act but I have to think because"
					+ "there is no plan! " + metaAction);
		}
		
		// otherwise carry out the meta-action
		else if (metaAction == MetaAction.ACT && plan.size() > 0)
		{
			act();
			Printing.hyp("agent acted");
		}
		else if (metaAction == MetaAction.DELIBERATE)
		{
			if (holes.size() == 0)
			{
				// wait
				Printing.hyp("no holes");
			}
			else
			{
				deliberate();
				Printing.hyp("agent deliberated");
			}
		}
	}
	
	public void act()
	{
		Printing.hyp("acting. plan: " + Arrays.toString(plan.toArray()));
		agPos = plan.removeFirst();
		
		score += holes.contains(agPos) ? holeScore.get(agPos) : 0;
	}
	
	public void deliberate()
	{
		// find best hole
		double max = Integer.MIN_VALUE;
		int maxPos = -1;
		
		for (int hole : holes)
		{
			int score = 0, distance = 0;
			
			try {
				score = holeScore.get(hole);
				distance = MinimalTileworldSimulation.dist[agPos][hole];
			} 
			catch (ArrayIndexOutOfBoundsException e)
			{
				MinimalTileworldSimulation.printWithHyp(this);
				System.out.println("agPos: " + agPos);
				System.out.println("hole: " + hole);
				System.out.println("holes: " + holes);
				System.exit(-1);
			}
			
			// first see whether the hole is reachable
			if (distance > TileworldSettings.WORLD_SIZE * TileworldSettings.WORLD_SIZE)
			{
				Printing.hyp(hole + "  is unreachable");
				continue;
			}
			
			
			double wScore = score / distance;
			
			if (wScore > max)
			{
				max = wScore;
				maxPos = hole;
			}
		}
		
		if (maxPos == -1) {
			//System.out.println("no hole is reachable");
			return;
		}
		
		plan = MinimalTileworldSimulation.path(agPos, maxPos);
		
		planningDelay = TileworldSettings.PLANNING_TIME;
	}
	
	public void addHole()
	{
		int p = getRandomEmptyState();
		int lifetime = MathOperations.getRandomInt(
				TileworldSettings.HOLE_LIFE_EXP_MIN, TileworldSettings.HOLE_LIFE_EXP_MAX);
	
		int score = MathOperations.getRandomInt(
				TileworldSettings.HOLE_SCORE_MIN, TileworldSettings.HOLE_SCORE_MAX);
		
		holes.add(p);
		holeLife.put(p, lifetime);
		holeScore.put(p, score);
		
	}
	
	public void removeHoleIfVisited()
	{
		if (holes.contains(agPos))
		{
			holes.remove(agPos);
			holeLife.remove(agPos);
			holeScore.remove(agPos);
		}
	}
	
	public void setNextHole()
	{
		nextHole = MathOperations.getRandomInt(
				TileworldSettings.HOLE_GESTATION_TIME_MIN, TileworldSettings.HOLE_GESTATION_TIME_MAX);
	}
	
	public int getX(int pos)
	{
		return pos % TileworldSettings.WORLD_SIZE;
	}
	
	public int getY(int pos)
	{
		return pos / TileworldSettings.WORLD_SIZE;
	}
	
	public int getPos(int x, int y)
	{
		return y*TileworldSettings.WORLD_SIZE + x;
	}
	
	public int getRandomEmptyState() 
	{
		// exclude obstacles, holes, and the agent location
		
		int twsize = TileworldSettings.WORLD_SIZE;
		
		ArrayList<Integer> exclude = new ArrayList<Integer>(holes);
				
		for (int i : MinimalTileworldSimulation.obstacles)
			exclude.add(i);	
		
		exclude.add(agPos);
		
		Collections.sort(exclude);
		
		int start = 0;
		
		while (exclude.contains(start)) start++;
		
		if (start >= twsize*twsize) {
			System.err.println("No free empty state left!");
			System.err.println("exclude:" + exclude);
			MinimalTileworldSimulation.printWithHyp(this);
			return -1;
		}			
			
		int random = MathOperations.getRandomInt(start, twsize*twsize-1 - exclude.size());
		    
	    for (int ex : exclude) {
	        if (random < ex) {
	            break;
	        }
	        random++;
	    }
	    
	    return random;
	}
	

}
