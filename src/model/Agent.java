package model;

import model.mdp.Action;
import model.mdp.MDP;
import model.mdp.State;
import model.mdp.operations.MDPValueIterator;
import constants.MathOperations;
import constants.SimulationConstants;

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
	private State currentState = null;
	private Action[] policy = null;
	private Action nextAction = null;
	private MDPValueIterator valueIterator = new MDPValueIterator();
	private MDP mdp = null;
	private double reward = 0;
	private int deliberations = 0, acts = 0;
	
	//
	// CONSTRUCTORS
	//
	public Agent(MDP mdp) {
		setMDP(mdp);
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public void setMDP(MDP newMDP) {
		this.mdp = newMDP;

		// position ourselves on the first state
		currentState = mdp.getRandomState();
	}
	
	public void setCurrentState(State newState) {
		this.currentState = newState;
	}
	
	public Action getNextAction() {
		return nextAction;
	}
	
	public State getCurrentState() {
		return currentState;
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
		int index = mdp.getStateIndex(currentState);
		State currentState = mdp.getState(index);
		
		if 
			// if the current state is null or there is no policy we have to deliberate
			( (currentState == null) || (policy == null) ||
			
			// if the action that should be executed is not possible according to the MDP, deliberate as well
			(index >= mdp.countStates()) || 
				(mdp.getActionEdge(currentState, policy[index]) == null) ||
						
			// deliberate with probability pDeliberate
			MathOperations.throw_dice(Settings.P_DELIBERATE))
		{
			System.out.println("<Agent> I will deliberate");
			deliberate();
			return SimulationConstants.AGENT_CHOICE_DELIBERATE;
		}
		else {
			System.out.println("<Agent> I will act");
			act();
			return SimulationConstants.AGENT_CHOICE_ACT;
		}
	}
	
	public void reward(double r) {
		reward += r;
	}
	
	public double getReward() {
		return reward;
	}
	
	public int getDeliberations() {
		return deliberations;
	}
	
	public int getActs() {
		return acts;
	}
	
	public void reset() 
	{
		currentState = null;
		policy = null;
		nextAction = null;
		mdp = null;
		reward = 0;
		deliberations = 0;
		acts = 0;
	}
	
	/**
	 * Compute a new policy through value iteration.
	 * 
	 * @param mdp
	 */
	private void deliberate() 
	{
		deliberations++;
		valueIterator.run(mdp);
		policy = valueIterator.getPolicy();
		nextAction = null; // don't move
	}
	
	/**
	 * Execute the next move in the policy.
	 */
	private void act() 
	{
		acts++;
		nextAction = policy[mdp.getStateIndex(currentState)];
	}
}
