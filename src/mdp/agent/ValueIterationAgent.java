package mdp.agent;

import java.util.LinkedList;
import java.util.Map;

import mdp.Tileworld;
import mdp.algorithms.MDPValueIterator;
import mdp.algorithms.ShortestPath;
import mdp.elements.Action;
import mdp.elements.State;
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
public class ValueIterationAgent extends Agent
{
	private final MDPValueIterator valueIterator;
	private final Map<State,Action> policy;
	
	private State prevState = null;
	
	//
	// CONSTRUCTORS
	//
	public ValueIterationAgent(Tileworld mdp) 
	{
		super(mdp);
		this.valueIterator = new MDPValueIterator(mdp);
		this.policy = valueIterator.getPolicy();
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public Action getNextAction() {
		return policy.get(currentState);
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
	public MetaAction step() 
	{		
		int boldness = TileworldSettings.BOLDNESS,
				nrHoles = ((Tileworld) tileworld).getHoles().size();
		
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
			return MetaAction.DELIBERATE;
		} else 
		{
			act();
			return MetaAction.ACT;
		}
	}
	
	public void deliberateForEvent() {
		this.deliberateForEvent = true;
	}
	
	public boolean inOptimalState() 
	{
		State nextState = null;
		
		if (prevState == null) return false;
		
		// the agent is in an optimal state if it wants to return to the state it came from
		nextState = tileworld.getStatePolicy().get(currentState);
		
		return (prevState == nextState);
		
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
		prevState = null;		
	}
			
	/**
	 * Compute a new policy.
	 * 
	 * @param tileworld
	 */
	protected void deliberate() 
	{
		prevState = null;
		
		deliberations++;
		
		valueIterator.run(tileworld);
			
		computeCurrentTarget();
				
		score -= TileworldSettings.PLANNING_TIME;
	}
	
	/**
	 * Execute the next move in the policy.
	 */
	protected void act() 
	{
		acts++;
		prevState = currentState;
	}
	
	private void computeCurrentTarget()
	{
		// TODO: we now compute the current target based on the Kinny & Georgeff utility function
		LinkedList<State> holes = (LinkedList<State>) tileworld.getHoles();
		
		this.currentTarget = (currentState == null || holes.size() == 0) ? null :
			ShortestPath.closestStateWeighted(currentState, holes, tileworld);
	}

	@Override
	public void updatePlan() {
		// TODO Auto-generated method stub
		
	}
}
