package mdp.agent;

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
	private boolean delay = false;
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
		if (planningDelay > 0)
		{
			Printing.angel("planning delay");
			planningDelay--;			
			choice = MetaAction.NOP;
		}
		
		// wait if there are no holes
		else if (tileworld.getHoles().size() == 0)  
		{
			choice = MetaAction.NOP;
		}
		
		else if (plan.size() == 0)
		{
			choice = MetaAction.DELIBERATE;
		}
				
		// create hypotheses
		else
		{
			Printing.angel("Computing optimal action by creating hypotheses");
			choice = minTileworldSim.computeOptimalAction(simulation);
			Printing.angel("decisions: " + choice);
		}
				
		switch (choice)
		{			
		case DELIBERATE: 
			deliberate();
			if (planningDelay > 0.0 || plan.size() == 0 || delay) {
				if (planningDelay > 0) planningDelay--;
				if (delay) delay = false;
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
