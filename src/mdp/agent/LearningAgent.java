package mdp.agent;

import java.util.LinkedList;

import constants.MathOperations;
import constants.Printing;
import mdp.Tileworld;
import mdp.algorithms.ShortestPath;
import mdp.elements.Action;
import mdp.elements.State;
import settings.TileworldSettings;

public class LearningAgent extends Agent
{
	public static final int FEATURES = 5;
	private double ALPHA = 0.1, EPSILON = 0.1;
	public static boolean TRAINING = true;
	
	public static double thetaAct[] = new double[]{67, 0.6, -68, -68, 34},
				thetaThink[] = new double[]{68, 0.56, -68, -68, 34},
				features[] = new double[FEATURES];
	
	
	private LinkedList<Action> plan = new LinkedList<Action>();
	private double planningDelay = 0;
	private boolean delay = false;
	private boolean justDeliberated = false;
	
	//
	// CONSTRUCTORS
	//
	public LearningAgent(Tileworld mdp) {
		super(mdp);
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public Action getNextAction() {
		return plan == null || plan.isEmpty() ? null : plan.getFirst();
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
		int nrHoles = tileworld.getHoles().size();
		
		MetaAction choice = null;
				
		// do nothing when we are thinking
		if (planningDelay > 0)
		{
			planningDelay--;
			
			choice = MetaAction.NOP;
			Printing.spa("planning delay");
		}
		
		// wait if there are no holes
		if (nrHoles == 0)  
		{
			choice = MetaAction.NOP;
			Printing.spa("no holes");
		}
		
		// if we did not decide yet
		if (choice == null)
		{
			getFeatures();
			
			if (TRAINING) {
				updateTheta();
			}
			
			double valueThink = dotProduct(thetaThink, features);
			double valueAct = dotProduct(thetaAct, features);
			
			if (TRAINING && MathOperations.getRandomDouble() <= EPSILON) {
				choice = MathOperations.getRandomDouble() >= 0.5 ?
						MetaAction.ACT : MetaAction.DELIBERATE;
			} else {
				choice = valueThink > valueAct ? MetaAction.DELIBERATE : MetaAction.ACT;
			}
		}
		
		switch (choice)
		{			
		case DELIBERATE: 
			deliberate(); 
			deliberateForEvent = false;
			justDeliberated = true;
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

	public void getFeatures() {
		
		features[0] = TileworldSettings.DYNAMISM;
		features[1] = justDeliberated ? 1 : -1;
		features[2] = deliberateForEvent ? 1 : -1;
		features[3] = plan.isEmpty() ? 1 : -1;
		features[4] = TileworldSettings.PLANNING_TIME;
	}
	
	public void updateTheta() {
		State newTarget = ShortestPath.closestStateWeighted(currentState, tileworld.getHoles(), tileworld);
		
		double valueThink = newTarget == null ? 0 : newTarget.getReward();
		double valueAct   = currentTarget == null ? 0 : currentTarget.getReward();
		
		for (int i=0; i<FEATURES; i++) {
			thetaThink[i] += ALPHA * (valueThink - thetaThink[i] * features[i]) * features[i];
			thetaAct[i] += ALPHA * (valueAct - thetaAct[i] * features[i]) * features[i];
		}
	}
	
	public double dotProduct(double arr1[], double arr2[]) {
		if (arr1.length != arr2.length) return -1;
		
		double ret = 0;
		
		for (int i=0; i<arr1.length; i++) {
			ret += arr1[i] * arr2[i];
		}
		
		return ret;
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
		justDeliberated = false;
	}
	
	@Override
	public void update() {		
	}
	
	public void updatePlan() {
		removeActionFromPlan();
	}
}
