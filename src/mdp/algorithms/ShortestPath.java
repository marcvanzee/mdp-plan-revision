package mdp.algorithms;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mdp.Tileworld;
import mdp.elements.Action;
import mdp.elements.State;
import settings.TileworldSettings;

public class ShortestPath 
{
	/**
	 * Computes the closest state in targetStates from s taking into account obstacles in tileworld
	 * @param s
	 * @param targetStates
	 * @param tileworld
	 * @return
	 */
	public static State closestState(State s, List<State> targetStates, Tileworld tileworld)
	{
		int minDist = Integer.MAX_VALUE;
		State ret = null;
		
		for (State s2 : targetStates)
		{
			int dist = shortestPath(s,s2, tileworld);
			if (dist < minDist) {
				minDist = dist;
				ret = s2;
			}
		}
		
		return ret;
	}
	
	public static State closestStateWeighted(State s, List<State> targetStates, Tileworld tileworld)
	{
		double maxScore = Double.MIN_VALUE;
		State ret = null;
		
		for (State s2 : targetStates)
		{
			double score = (double) s2.getReward() / (double) shortestPath(s,s2, tileworld);
			
			if (score > maxScore) {
				maxScore = score;
				ret = s2;
			}
		}
		
		return ret;
	}
	
	public static State closestStateWeighted2(State s, List<State> targetStates, Tileworld tileworld)
	{
		double maxScore = Double.MIN_VALUE;
		State ret = null;
		
		for (State s2 : targetStates)
		{
			int dist = shortestPath(s,s2, tileworld);
			if (s2.getLifetime() - (dist+TileworldSettings.PLANNING_TIME)*TileworldSettings.DYNAMISM < 0) {
				continue;
			}
			double score = (double) s2.getReward() / (double) dist;
			
			if (score > maxScore) {
				maxScore = score;
				ret = s2;
			}
		}
		
		return ret;
	}
	
	/**
	 * Computes the shortest path from s to s2 taking into account obstacles in tileworld
	 * @param s
	 * @param targetStates
	 * @param tileworld
	 * @return
	 */
	public static int shortestPath(State s1, State s2, Tileworld tileworld)
	{
		// implemented using depth first search
		// iterative using a queue
		LinkedList<State> queue = new LinkedList<State>();
		
		HashMap<State,Integer> valueMap = new HashMap<State,Integer>();
		
		valueMap.put(s1, 0);
		queue.addFirst(s1);
		
		while (!queue.isEmpty())
		{
			State s = queue.removeFirst();
			int value = valueMap.get(s);
			
			if (s == s2) {
				// found the goal state!
				// the first one that finds it should have the shortest path 
				// so we can simply return the current value
				return value;
			}

			int nextValue = value + 1;
			
			for (State n : tileworld.getNeighbors(s))
			{
				// if we haven't visited the node, or if our current way is shortest than
				// the previous one we should (re)visit the neighbor
				if (!valueMap.containsKey(n) || valueMap.get(n) > nextValue)
				{
					valueMap.put(n, nextValue);
					queue.addLast(n);
				}
			}
		}
		
		return -1; // not found		
	}
	
	public static LinkedList<Action> computePlan(State s1, State s2, Tileworld tw)
	{
		if (s1 == null || s2 == null || tw == null) {
			System.err.println("cannot compute plan for empty states/tileworld! " + s1 + ", " + s2 + ", " + tw);
			return null;
		}
		
		// implemented using depth first search
		// iterative using a queue
		LinkedList<State> queue = new LinkedList<State>();
		
		HashMap<State,Integer> valueMap = new HashMap<State,Integer>();
		HashMap<State,LinkedList<Action>> planMap = new HashMap<State,LinkedList<Action>>();
		
		planMap.put(s1, new LinkedList<Action>());
		
		valueMap.put(s1, 0);
		queue.addFirst(s1);
		
		while (!queue.isEmpty())
		{
			State s = queue.removeFirst();
			int value = valueMap.get(s);
			LinkedList<Action> curPlan = planMap.get(s);
			
			if (s == s2) {
				// found the goal state!
				// the first one that finds it should have the shortest path 
				// so we can simply return the current value
				return curPlan;
			}

			int nextValue = value + 1;
			
			for (State n : tw.getNeighbors(s))
			{
				// if we haven't visited the node, or if our current way is shortest than
				// the previous one we should (re)visit the neighbor
				if (!valueMap.containsKey(n) || valueMap.get(n) > nextValue)
				{
					valueMap.put(n, nextValue);
					queue.addLast(n);
					LinkedList<Action> newPlan = new LinkedList<Action>(curPlan);
					newPlan.add(getAction(s,n, tw));
					planMap.put(n, newPlan);
				}
			}
		}
		
		System.err.println("no plan found from " + s1 + " to " + s2);
		return null; // not found		
	}
	
	public static Action getAction(State s1, State s2, Tileworld tw) {
		if (s1.getX() < s2.getX()) return tw.getAction("right");
		if (s1.getX() > s2.getX()) return tw.getAction("left");
		if (s1.getY() < s2.getY()) return tw.getAction("down");
		return tw.getAction("up");
	}
}
