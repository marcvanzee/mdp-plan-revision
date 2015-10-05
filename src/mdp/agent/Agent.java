package mdp.agent;

import java.util.LinkedList;
import java.util.Map;

import constants.SimulationConstants;
import mdp.MDP;
import mdp.Tileworld;
import mdp.algorithms.MDPValueIterator;
import mdp.algorithms.ShortestPath;
import mdp.elements.Action;
import mdp.elements.State;
import messaging.tileworld.AgentMessage;
import settings.TileworldSettings;

/**
 * An agent resides in an MDP. It has two action:
 * 1. Move: 
 *     Move to the next state according to the computed policy.
 *     This is only possible if a policy has been computed.
 *     
 * 2. Deliberate:
 *     Recompute a policy for the current MDP.
 *     
 * The agent executes the following algorithm:
 * 
 * 
 * 1. While simulation not ended:
 * 2.     If no policy exists: deliberate.
 * 3.     If next action in the policy is not executable: deliberate.
 * 4.     Deliberate with p = SimulationSettings.pDeliberate.
 * 5.     If not deliberate: execute next action on the policy.
 * 
 * @author marc.vanzee
 *
 */
public class Agent 
{
	private final MDPValueIterator valueIterator;
	private LinkedList<Action> plan;
	private final Map<State,Action> policy;
	private final MDP mdp;
	
	private State currentState = null, prevState = null;
	private double score = 0;
	private int deliberations = 0, acts = 0;
	private int actSteps = 0;
	private boolean deliberateForEvent = false;	
	private State currentTarget = null;
	
	//
	// CONSTRUCTORS
	//
	public Agent(MDP mdp) {
		this.mdp = mdp;
		
		this.valueIterator = new MDPValueIterator(mdp);
		this.plan = new LinkedList<Action>();;
		
		this.policy = valueIterator.getPolicy();
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public void setCurrentState(State newState) {
		this.currentState = newState;
	}
	
	public Action getNextAction() {
		switch (TileworldSettings.ALGORITHM) {
		case VALUE_ITERATION:
			return policy.get(currentState);
		case SHORTEST_PATH:
			return (plan == null || plan.isEmpty() ? null : plan.getFirst());
		}
		
		return null;
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public void setCurrentStateRandomly() {
		// position ourselves on the first state
		currentState = mdp.getRandomState();
	}
	
	public void removeActionFromPlan() {
		if (plan != null && plan.size() > 0)
			plan.removeFirst();
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	/**
	 * The agent runs the algorithm and returns what it has done.
	 * Choices for what has been done are:
	 * 1. contants.SimulationConstants.AGENT_CHOICE_DELIBERATE
	 * 2. contants.SimulationConstants.AGENT_CHOICE_ACT
	 * 	
	 */
	public int step() 
	{		
		int boldness = TileworldSettings.BOLDNESS,
				nrHoles = ((Tileworld) mdp).getHoles().size();
		
		/* 
		 * The agent has to deliberate if there are holes AND EITHER
		 * 1. commitment degree == actSteps, or
		 * 2. boldness == -1 and the optimal state has been reached, or
		 * 3. reaction strategy is true
		 */
		if ((nrHoles > 0) && 
			(	(actSteps == boldness) ||
				(boldness == -1 && inOptimalState()) ||
				deliberateForEvent))
		{
			deliberateForEvent = false;
			System.out.println("deliberating at step " + (deliberations+acts));
			deliberate();
			return SimulationConstants.AGENT_CHOICE_DELIBERATE;
		} else 
		{
			act();
			return SimulationConstants.AGENT_CHOICE_ACT;
		}
	}
	
	public void deliberateForEvent() {
		this.deliberateForEvent = true;
	}
	
	public boolean inOptimalState() 
	{
		State nextState = null;
		switch (TileworldSettings.ALGORITHM) {
		case VALUE_ITERATION:
			if (prevState == null) return false;
			
			// the agent is in an optimal state if it wants to return to the state it came from
			nextState = ((Tileworld) mdp).getStatePolicy().get(currentState);
			
			return (prevState == nextState);
		case SHORTEST_PATH:
			System.out.println("curstate: " + currentState);
			System.out.println("curtarget: " + currentTarget);
			System.out.println("nr holes: " + ((Tileworld) mdp).getHoles().size());
			return currentState == currentTarget;
		}
		
		return false;
	}
	
	public void reward() {
		if (currentState != null)
			score += currentState.getReward();
	}
	
	public double getScore() {
		return score;
	}
	
	public int getDeliberations() {
		return deliberations;
	}
	
	public int getActs() {
		return acts;
	}
	
	public double getValue(State s) {
		return valueIterator.getValue(s);
	}
	
	public void update() {
		valueIterator.update();
	}
	
	public void reset() {
		currentState = null;
		prevState = null;
		score = 0;
		deliberations = 0;
		acts = 0;
		actSteps = 0;
		deliberateForEvent = false;
		plan.clear();
	}
	
	public void inform(AgentMessage message, State s)
	{
		if (!TileworldSettings.USE_REACTION_STRATEGY)
			return;
		
		switch (message) 
		{
		case HOLE_DISAPPEARS: 
			// if we are using any reaction strategy and the current target disappears, the agent should deliberate
			if (currentTarget != null && s == currentTarget)
			{
				System.out.println("target disappeared!");
				deliberateForEvent = true;
			}
			break;
			
		case HOLE_APPEARS:
			ReactionStrategy strategy = TileworldSettings.REACTION_STRATEGY;
			
			if (strategy == ReactionStrategy.TARGET_DIS_OR_ANY_HOLE)
			{
				System.out.println("any hole appeared!");
				deliberateForEvent = true;
			}
			
			else if (strategy == ReactionStrategy.TARGET_DIS_OR_NEARER_HOLE)
			{
				int distanceToTarget = ShortestPath.shortestPath(currentState, currentTarget, (Tileworld) mdp),
						distanceToNewHole = ShortestPath.shortestPath(currentState, s, (Tileworld) mdp);
				
				if (distanceToNewHole < distanceToTarget)
				{
					System.out.println("nearer hole appeared!");
					deliberateForEvent = true;
				}
			}
		}
	}
	
	public LinkedList<Action> getPlan() {
		return plan;
	}
		
	/**
	 * Compute a new policy.
	 * 
	 * @param mdp
	 */
	private void deliberate() 
	{
		prevState = null;
		
		deliberations++;
		
		switch (TileworldSettings.ALGORITHM) 
		{
		case VALUE_ITERATION:
			valueIterator.run(mdp);
			break;
		case SHORTEST_PATH:
			// first find the closest hole
			Tileworld tw = (Tileworld) mdp;
			State s = ShortestPath.closestState(currentState, tw.getHoles(), tw);
			
			// then compute the plan
			this.plan = ShortestPath.computePlan(currentState, s, tw);
		}
		computeCurrentTarget();
				
		score -= TileworldSettings.PLANNING_COST;
	}
	
	/**
	 * Execute the next move in the policy.
	 */
	private void act() 
	{
		acts++;
		prevState = currentState;
	}
	
	private void computeCurrentTarget()
	{
		// TODO: we now compute the current target based on the Kinny & Georgeff utility function
		Tileworld tileworld = (Tileworld) mdp; 
		LinkedList<State> holes = (LinkedList<State>) tileworld.getHoles();
		
		this.currentTarget = (currentState == null || holes.size() == 0) ? null :
			ShortestPath.closestStateWeighted(currentState, holes, tileworld);
	}
}
