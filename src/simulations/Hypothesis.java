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
	
	LinkedList<Hypothesis> history = new LinkedList<Hypothesis>();
	
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
		history = new LinkedList<Hypothesis>(h.history);
	}
	
	public void save()
	{
		Hypothesis copy = new Hypothesis(this, metaAction);
		copy.depth++;
		history.add(copy);
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
		
		depth = TileworldSettings.HYPOTHESIS_DEPTH+1;
		
		Printing.hyp(MinimalTileworldSimulation.toStringWithHyp(this));
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
			
			if (TileworldSettings.TEST_ENV)
			{
				addTestHoles();
				removeHoleIfVisited();
			}
			else if (nextHole <= 0) 
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
				depth--;
			}
			
			steps++;
			nextHole--;
			
			decreaseLifetimeHoles();
			removeHoleIfVisited();
		}
	}
	
	private void addTestHoles()
	{
		if (steps == 0)
		{
			addHole(getPos(0, 4), 10, 10);
		}
		
		else if (steps == 4)
		{
			addHole(getPos(2, 0), 4, 40);
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
		
		if (planningDelay > 0)
		{
			planningDelay--;
		}
		
		// wait if there are no holes
		else if (holes.size() == 0)  
		{
			// do nothing
			Printing.hyp("no holes");
		}
						
		// otherwise carry out the meta-action
		else if (metaAction == MetaAction.ACT)
		{
			// if we have to act but we have no plan, stop
			if (plan.size() == 0) {
				System.err.println("PROBLEMEMMM");
				metaAction = null;
			}
			else {
				act();
				Printing.hyp("agent acted");
				metaAction = null;
			}
		}
		else if (metaAction == MetaAction.DELIBERATE)
		{
			deliberate();
			if (planningDelay > 0)
				planningDelay--;
			if (delay)
				delay = false;
			Printing.hyp("agent deliberated");
			metaAction = null;
		}
		
		else {
			Printing.hyp("agent did nothing :(");
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
				Printing.hyp(MinimalTileworldSimulation.toStringWithHyp(this));
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
		
		if (TileworldSettings.PLANNING_TIME == 0.5)
		{
			delay = true;
		}
		else 
		{
			planningDelay = TileworldSettings.PLANNING_TIME;
		}
	}
	
	public void addHole()
	{
		int p = getRandomEmptyState();
		
		if (p == -1) // no free space
			return;
		int lifetime = MathOperations.getRandomInt(
				TileworldSettings.HOLE_LIFE_EXP_MIN, TileworldSettings.HOLE_LIFE_EXP_MAX);
	
		int score = MathOperations.getRandomInt(
				TileworldSettings.HOLE_SCORE_MIN, TileworldSettings.HOLE_SCORE_MAX);
		
		addHole(p, lifetime, score);
	}
	
	public void addHole(int location, int lifetime, int score)
	{
		holes.add(location);
		holeLife.put(location, lifetime);
		holeScore.put(location, score);
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
		
		int twsize = TileworldSettings.WORLD_SIZE,
				end = twsize * twsize;
				
		
		ArrayList<Integer> exclude = new ArrayList<Integer>(holes);
				
		for (int i : MinimalTileworldSimulation.obstacles)
			exclude.add(i);	
		
		exclude.add(agPos);
		
		Collections.sort(exclude);
		
		int start = 0;
		
		if (exclude.size() == end)
			return -1;
		
		int random = 0;
		
		try {
			random = start + MathOperations.getRandomInt(0, end-start-1 - exclude.size());
		} catch (IllegalArgumentException e)
		{
			return -1;
		}
		    
	    for (int ex : exclude) {
	        if (random < ex) {
	            break;
	        }
	        random++;
	    }
	    
	    return random;
	}
	

}
