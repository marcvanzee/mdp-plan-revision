package mdp.agent;

import java.util.Arrays;
import java.util.LinkedList;

import constants.Printing;
import mdp.Tileworld;
import mdp.algorithms.ShortestPath;
import mdp.elements.Action;
import settings.TileworldSettings;
import simulations.MinimalTileworldSimulation;
import simulations.TileworldSimulation;

public class Angel extends Agent 
{
	TileworldSimulation simulation;
	private LinkedList<Action> plan = new LinkedList<Action>();
	private double planningDelay = 0;
	private boolean delay = true, executedAction = false;
	private MinimalTileworldSimulation minTileworldSim;
	
	//
	// CONSTRUCTORS
	//
	
	public Angel(Tileworld tw) {
		super(tw);
	}
	
	public void setSimulation(TileworldSimulation tws) 
	{
		simulation = tws;
	}
	
	public void updateSimulation()
	{
		minTileworldSim = new MinimalTileworldSimulation(simulation.getTileworld());
	}
	
	public double getPlanningDelay() {
		return this.planningDelay;
	}
	
	public boolean isDelayed() {
		return this.delay;
	}
	
	public boolean hasExecutedAction() {
		return this.executedAction;
	}
		
	@Override
	public MetaAction step() 
	{
		Printing.angel("step");
		MetaAction choice = null;
		
		if (simulation == null)
		{
			Printing.angel("Simulation null in Angel");
			return null;
		}
		
		// there are several things that prevent us from doing meta-reasoning:
		// - our planning delay doesn't allow it. in this case we simply have to wait
		// - we have no plan. in this case we have to create one if there are holes, otherwise we wait
		
		// if we have 0.5 plan delay and we have waited in the previous step, we may now do something again
		if (planningDelay == 0.5 && !delay)
		{
			delay = !delay;
			planningDelay = 0;
		}
		
		// do nothing when we are thinking
		if (planningDelay == 0.5 && delay)
		{
			Printing.angel("planning delay");
			planningDelay = 0;
			delay = !delay;
			choice = MetaAction.NOP;
		}
		else if (planningDelay >= 1)
		{
			Printing.angel("planning delay");
			planningDelay--;
			choice = MetaAction.NOP;
		}
			
		// if we have no plan, deliberate
		else if (plan.size() == 0)
		{
			// if there are no holes, don't create a new plan
			if (tileworld.getHoles().size() == 0)
			{
				Printing.angel("No holes, so I wait ");
				choice = MetaAction.NOP;
			}
			else {
				Printing.angel("No plan, so I deliberate");
				choice = MetaAction.DELIBERATE;
			}
		}
				
		// create hypotheses
		else
		{
			Printing.angel("Computing optimal action by creating hypotheses");
			choice = minTileworldSim.computeOptimalAction(simulation);	
		}
				
		switch (choice)
		{
		case ACT: 
			act();
			executedAction = true;
			Printing.angel("acted");
			break;
			
		case DELIBERATE: 
			deliberate();
			Printing.angel("deliberated, plan: " + Arrays.toString(plan.toArray()));
			executedAction = true;
			break;
			
		case NOP:
			Printing.angel("NOP");
			break;
		}
		
		return choice;
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

		planningDelay = TileworldSettings.PLANNING_TIME;
		
		
	}
	
	/**
	 * Execute the next move in the policy.
	 */
	protected void act() 
	{
		acts++;
		actSteps++;
	}
	
	public void reset() {
		plan.clear();
		planningDelay = 0;

		MinimalTileworldSimulation.planMap.clear();	
				
		super.reset();
	}
	
	public LinkedList<Action> getPlan() {
		return plan;
	}

	public Action getNextAction() {
		return (plan == null || plan.isEmpty() ? null : plan.getFirst());
	}
	
	public void removeActionFromPlan() {
		if (plan != null && plan.size() > 0)
			plan.removeFirst();
	}
	
	@Override
	public void update() {		
	}
	
	public void updatePlan() {
		removeActionFromPlan();
	}	
}
