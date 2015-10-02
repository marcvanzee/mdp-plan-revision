package mdps.elements;

import java.util.Map;

import constants.SimulationConstants;
import mdps.MDP;
import mdps.Tileworld;
import mdps.algorithms.MDPValueIterator;
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
	private final Map<State,Action> policy;
	private final MDP mdp;
	
	private State currentState = null, prevState = null;
	private double score = 0;
	private int deliberations = 0, acts = 0;
	private int actSteps = 0;
	private boolean deliberateForEvent = false;	
	
	//
	// CONSTRUCTORS
	//
	public Agent(MDP mdp) {
		this.mdp = mdp;
		
		this.valueIterator = new MDPValueIterator(mdp);
		
		this.policy = valueIterator.getPolicy();
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public void setCurrentState(State newState) {
		this.currentState = newState;
	}
	
	public Action getNextAction() {
		return policy.get(currentState);
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public void setCurrentStateRandomly() {
		// position ourselves on the first state
		currentState = mdp.getRandomState();
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
				deliberateForEvent	))
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
		if (prevState == null) return false;
		
		// the agent is in an optimal state if it wants to return to the state it came from
		State nextState = ((Tileworld) mdp).getStatePolicy().get(currentState);
		boolean ret = (prevState == nextState);
		
		if (ret) System.out.println("in optimal state: " + nextState + " and " + prevState);
		
		return ret;
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
	}
		
	/**
	 * Compute a new policy through value iteration.
	 * 
	 * @param mdp
	 */
	private void deliberate() 
	{
		prevState = null;
		
		deliberations++;
		valueIterator.run(mdp);
		
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
}
