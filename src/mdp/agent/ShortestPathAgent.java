package mdp.agent;

import java.util.LinkedList;

import constants.Printing;
import mdp.Tileworld;
import mdp.algorithms.ShortestPath;
import mdp.elements.Action;
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
public class ShortestPathAgent extends Agent
{
	private LinkedList<Action> plan = new LinkedList<Action>();
	private double planningDelay = 0;
	private boolean delay = false;
	
	//
	// CONSTRUCTORS
	//
	public ShortestPathAgent(Tileworld mdp) {
		super(mdp);
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public Action getNextAction() {
		return (plan == null || plan.isEmpty() ? null : plan.getFirst());
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
	public MetaAction step() 
	{		
		int boldness = TileworldSettings.BOLDNESS,
				nrHoles = tileworld.getHoles().size();
		
		MetaAction choice = null;
				
		// do nothing when we are thinking
		if (planningDelay > 0)
		{
			planningDelay--;
			
			choice = MetaAction.NOP;
			Printing.spa("planning delay");
		}
		
		// wait if there are no holes
		if (nrHoles == 0 && plan.isEmpty())  
		{
			choice = MetaAction.NOP;
			Printing.spa("no holes");
		}
		
		// if we did not decide yet
		if (choice == null)
		{
			// if there are holes and we have no plan, deliberate
			// note that if boldness=-1, this will automatically mean that planning
			// only occurs when the plan is finished (or possibly through reaction strategies)
			if (plan.size() == 0)
			{
				Printing.spa("deliberating because plan is empty");
				choice = MetaAction.DELIBERATE;
			}
			
			// if there are holes and we have a plan, deliberate if our boldness tells us to
			else if (actSteps == boldness)
			{
				Printing.spa("deliberating because of boldness");
				choice = MetaAction.DELIBERATE;
			}
			
			// if there are holes and we have a plan and our boldness doesn't tell us to deliberate,
			// deliberate for reaction strategies
			else if (deliberateForEvent)
			{
				Printing.spa("deliberating for event");
				choice = MetaAction.DELIBERATE;
				deliberateForEvent = false;
				
			}
		
			// if there are holes, we have a plan, our boldness doesn't tell us to deliberate and
			// there are no reaction strategies that apply, act
			else
			{
				choice = MetaAction.ACT;
			}
		}
		
		switch (choice)
		{			
		case DELIBERATE:
			deliberate(); 
			deliberateForEvent = false;
			if (planningDelay > 0 || plan.size() == 0 || delay) {
				delay = false;
				break;
			}
			choice = MetaAction.ACT;
		case ACT:
			act();
			break;
			
		case NOP:
			break;
		}
		
		return choice;
	}

	public void reset() {
		plan.clear();
		
		super.reset();
	}
	
	public LinkedList<Action> getPlan() {
		return plan;
	}
		
	/**
	 * Compute a new policy.
	 * 
	 * @param tileworld
	 */
	protected void deliberate() 
	{
		deliberations++;
		actSteps = 0;
		
		// first find the closest hole
		this.currentTarget = ShortestPath.closestStateWeighted(currentState, tileworld.getHoles(), tileworld);
		
		// if there is no hole, do nothing
		if (currentTarget == null)
			return;
		
		// then compute the plan
		this.plan = ShortestPath.computePlan(currentState, this.currentTarget, tileworld);

		if (TileworldSettings.PLANNING_TIME == 0.5)
		{
			delay = true;
		}
		else 
		{
			planningDelay = TileworldSettings.PLANNING_TIME;
		}
	}
	
	/**
	 * Execute the next move in the policy.
	 */
	protected void act() 
	{
		acts++;
		actSteps++;
	}
	
	@Override
	public void update() {		
	}
	
	public void updatePlan() {
		removeActionFromPlan();
	}
}
