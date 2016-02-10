package mdp.agent;

import mdp.Tileworld;
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
public abstract class Agent
{
	protected final Tileworld tileworld;
	
	protected State currentState = null, currentTarget = null;
	protected double score = 0;
	protected int deliberations = 0, acts = 0, actSteps = 0;
	protected boolean deliberateForEvent = false;	

	//
	// CONSTRUCTORS
	//
	
	public Agent(Tileworld mdp) {
		this.tileworld = mdp;
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public void setCurrentState(State newState) {
		this.currentState = newState;
		
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public void setCurrentStateRandomly() {
		// position ourselves on the first state
		currentState = tileworld.getRandomState();
	}
		
	//
	// OTHER PUBLIC METHODS
	//
	
	public void deliberateForEvent() {
		this.deliberateForEvent = true;
	}
	
	
	public void reward() {
		if (currentState != null) {
			score += currentState.getReward();
		}
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
		
	public void reset() {
		currentState = null;
		score = 0;
		deliberations = 0;
		acts = 0;
		actSteps = 0;
	}
	
	public void inform(AgentMessage message, State s)
	{
		switch (message) 
		{
		case HOLE_DISAPPEARS: 
			// if the current target disappears, any agent (except fixed) should deliberate
			if (currentTarget != null && s == currentTarget && TileworldSettings.BOLDNESS == -1)
			{
				deliberateForEvent = true;
			}
			break;
			
		case HOLE_APPEARS:
			ReactionStrategy strategy = TileworldSettings.REACTION_STRATEGY;
			
			if (strategy == ReactionStrategy.ANY_HOLE)
			{
				//System.out.println("any hole appeared!");
				deliberateForEvent = true;
			}
			
			else if (strategy == ReactionStrategy.CLOSER_HOLE)
			{
				int distanceToTarget = ShortestPath.shortestPath(currentState, currentTarget, (Tileworld) tileworld),
						distanceToNewHole = ShortestPath.shortestPath(currentState, s, (Tileworld) tileworld);
				
				if (distanceToNewHole < distanceToTarget)
				{
					//System.out.println("nearer hole appeared!");
					deliberateForEvent = true;
				}
			}
		}
	}
	
	public void copyValues(Agent ag)
	{
		// position agent at the right state
		State agState = ag.getCurrentState(),
				agTarget = ag.getCurrentTarget(),
				thisAgState = tileworld.getStateAtSameCoord(agState),
				thisAgTarget = tileworld.getStateAtSameCoord(agTarget);
		
		this.currentState = thisAgState;
		this.currentTarget = thisAgTarget;
		this.score = ag.getScore();
		this.acts = ag.getActs();
		this.actSteps = ag.getActSteps();
		this.deliberations = ag.getDeliberations();
		this.deliberateForEvent = ag.getDeliberateForEvent();
	}
	
	public State getCurrentTarget() {
		return this.currentTarget;
	}
	
	public int getActSteps() {
		return this.actSteps;
	}
	
	public boolean getDeliberateForEvent() {
		return this.deliberateForEvent;
	}
	
	public abstract MetaAction step();
	protected abstract void deliberate();
	protected abstract void act();
	public abstract  void update();
	public abstract Action getNextAction();
	public abstract void updatePlan();
}
