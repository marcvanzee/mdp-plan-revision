package mdp.agent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import constants.Printing;
import mdp.Tileworld;
import mdp.algorithms.ShortestPath;
import mdp.elements.Action;
import mdp.elements.State;
import messaging.tileworld.AgentMessage;
import settings.LearningSettings;
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
public class LearningAgent extends Agent
{
	private final LinkedList<Action> plan = new LinkedList<Action>();
	private final StrategyChoice strategyChoice = new StrategyChoice();
	
	private double planningDelay = 0;
	private boolean delay = false;
	
	private double temperature = LearningSettings.INIT_TEMP,
			maxScore = 0, currentScore = 0;
	private int timeoutSteps = 0;
	
	private int countStrategies = 0;
	
	private final Random r =  new Random();
		
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
		int boldness = -1,
				nrHoles = tileworld.getHoles().size();
		
		timeoutSteps++;
		
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
		
		if (timeoutSteps == LearningSettings.TIMEOUT_PERIOD) {
//			System.out.println("### TIMEOUT!!! ### (timeoutstep="+timeoutSteps);
			changeStrategy();
		}
				
		return choice;
	}

	public void updateTemperature() {
		if (temperature/LearningSettings.TEMP_DIVIDER > LearningSettings.TEMP_MIN_VALUE) {
			temperature /= LearningSettings.TEMP_DIVIDER;
//			System.out.println("new temperature " + temperature);
		} else {
			STOP = true;
		}
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
		this.plan.clear();
		this.plan.addAll(ShortestPath.computePlan(currentState, this.currentTarget, tileworld));

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
	
	@Override
	public double getScore() {		
		return super.getScore();
	}
	
	public ReactionStrategy getLearnedStrategy() {
		double maxEff = -Double.MAX_VALUE;
		ReactionStrategy ret = null;;
		
		for (ReactionStrategy rs : ReactionStrategy.values()) {
			double eff = effectiveness(rs);
			if (eff > maxEff) {
				maxEff = eff;
				ret = rs;
			}
		}
		
		return ret;
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	@Override
	public void reward() {
		if (currentState == null || currentState.getReward() == 0) {
			return;
		}
		
		
		currentScore += currentState.getReward();
		
//		System.out.println("currentScore updated: currentScore=" + currentScore);
		
		changeStrategy();
		super.reward();
	}
	
	private void changeStrategy() {
		timeoutSteps = 0;
		
		// currentScore > maxScore sometimes happens when the agent immediately gets a reward, we ignore those cases
		if (currentScore <= maxScore) {
			strategyChoice.addRun(TileworldSettings.REACTION_STRATEGY, currentScore, maxScore);
		} else {
//			System.out.println("finished (currentscore=" + currentScore + ", maxscore=" + maxScore + ", eff=##IGNORED)");
		}
			
		
		TileworldSettings.REACTION_STRATEGY = strategyChoice.getNewStrategy();
				
//		System.out.println("average effectiveness and runs for reaction strategies:");
//		for (ReactionStrategy rs : ReactionStrategy.values()) {
//			System.out.println(rs + ": eff=" + effectiveness(rs) + ", calls=" + strategyChoice.callCount.get(rs));
//		}
//		System.out.println("\nnext using strategy: " + TileworldSettings.REACTION_STRATEGY + "(temp=" + temperature + ")");
		
		maxScore = 0;
		currentScore = 0;	
		
//		System.out.println("maxscore and currentscore reset: maxscore="+maxScore+", currentscore="+currentScore);
		
		countStrategies++;
		
		// decrease temperature after trying LearningSettings.TEMP_DECREASE_STEPS
		if (countStrategies % LearningSettings.TEMP_DECREASE_STEPS == 0) {
			countStrategies = 0;
			updateTemperature();
		}
	}
	
	@Override
	public void inform(AgentMessage message, State s)
	{
		if (message == AgentMessage.HOLE_APPEARS) {
			maxScore += s.getReward();
			//System.out.println("maxscore updated: maxscore="+maxScore);
		}
		super.inform(message, s);
	}
	
	public void updatePlan() {
		removeActionFromPlan();
	}
	
	public double effectiveness(ReactionStrategy rs) {
		double score = strategyChoice.score.get(rs),
				maxScore = strategyChoice.maxScore.get(rs);
		
		return maxScore == 0 ? 0 : score / maxScore;
	}
	
	class StrategyChoice {
		
		HashMap<ReactionStrategy,Double> score = new HashMap<ReactionStrategy,Double>(),
				maxScore = new HashMap<ReactionStrategy,Double>();
		HashMap<ReactionStrategy,Integer> callCount = new HashMap<ReactionStrategy,Integer>();
		
		StrategyChoice() {
			for (ReactionStrategy rs : ReactionStrategy.values()) {
				score.put(rs, 0.0);
				maxScore.put(rs,0.0);
				callCount.put(rs, 0);
			}
		}
		
		ReactionStrategy getNewStrategy() {
			
			double sumScore = 0,
					p = Math.random(),
					cumProb = 0.0;
			
			for (ReactionStrategy rs : ReactionStrategy.values()) {
				sumScore += e(effectiveness(rs)); 
			}
			
			for (ReactionStrategy rs : ReactionStrategy.values()) {
				cumProb += e(effectiveness(rs))/sumScore;
				
				if (p <= cumProb) {
					return rs;
				}
			}
			
			return ReactionStrategy.values()[r.nextInt(ReactionStrategy.values().length)];
			
		}
		
		void addRun(ReactionStrategy rs, double currentScore, double max) {
//			System.out.println("in addrun: rs="+rs+", currenscore="+currentScore+", max="+max);
			int newCallCount = callCount.get(rs) + 1;
			callCount.put(rs, newCallCount);
			score.put(rs, score.get(rs)+currentScore);
			maxScore.put(rs, maxScore.get(rs)+max);
		}
		
		double e(double n) {
			return Math.exp(n/temperature);
		}
	}
}
