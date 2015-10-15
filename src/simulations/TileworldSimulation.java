package simulations;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import constants.MathOperations;
import mdp.Tileworld;
import mdp.agent.Agent;
import mdp.agent.Angel;
import mdp.agent.MetaAction;
import mdp.elements.Action;
import mdp.elements.QState;
import mdp.elements.State;
import mdp.operations.generators.TileworldGenerator;
import mdp.operations.modifiers.TileworldModifier;
import messaging.tileworld.AgentMessage;
import settings.TileworldSettings;

/**
 * A TileWorldSimulation consists of a TileWorld (i.e. an MDP and an Agent) and models the evolution of this TileWorld over time.
 * 
 * @author marc.vanzee
 *
 */
public class TileworldSimulation extends Simulation<Tileworld,TileworldGenerator, TileworldModifier>
{	
	private final Agent agent;
	
	private double maxScore = 0;
	private int nextHole;
	private boolean isHypothetical = false;
	private int hypothesisDepth = TileworldSettings.HYPOTHESIS_DEPTH;

	public TileworldSimulation() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
	{
		super(Tileworld.class, TileworldGenerator.class, TileworldModifier.class);
		agent = mdp.getAgent();
		
		if (agent instanceof Angel)
			// the agent requires a reference to this simulation for hy
			((Angel) agent).setSimulation(this);
	}
	
	public TileworldSimulation(TileworldSimulation tws) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
	{
		this();
		
		buildEmptyTileworld();
		
		copyValues(tws);
		
		// copy everything of tw into this simulation
		Tileworld tw = tws.getTileworld();
		
		mdp.copyValues(tw);
		

		// copy everything from the agent in tw to our agent
		if (agent instanceof Angel && tw.getAgent() instanceof Angel)
			((Angel)agent).copyValues(((Angel)tw.getAgent()));
		else
			agent.copyValues(tw.getAgent());
	}
	
	public void copyValues(TileworldSimulation tws)
	{
		this.steps = tws.getSteps();
		this.maxScore = tws.getMaxScore();
		this.nextHole = tws.getNextHole();
		this.isHypothetical = true;
		this.hypothesisDepth = tws.getHypothesisDepth() - 1;
	}
	
	public Tileworld getTileworld() {
		return mdp;
	}
	
	public boolean isHypothetical() {
		return this.isHypothetical;
	}
	
	public int getHypothesisDepth() {
		return this.hypothesisDepth;
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void buildNewModel() 
	{
		steps = 0;
		maxScore = 0;
		
		mdp.reset();
		agent.reset();
		
		// generate a tileworld	
		this.mdpGenerator.run();
		
		// add agent
		mdp.addAgentRandomly(new HashSet<State>(mdp.getObstacles()));
		
		// add holes
		for (int i=0; i<TileworldSettings.INITIAL_NR_HOLES; i++)
				addHole();
	
		notifyGUI();
		
		// schedule next hole and tell event to deliberate
		setNextHole();		
	}
	
	public void buildEmptyTileworld()
	{
		this.mdpGenerator.buildEmptyTileworld();
	}
	
	public void startSimulation(int maxSteps) {
		while (steps < maxSteps) {
			step();
		}
	}
		
	public void step() 
	{		
		if (nextHole <= 0) {
			addHole();
			
			// if the agent happens to be on the location where the hole has just 
			// been created, reward it and remove the hole
			removeHoleIfVisited();
			setNextHole();
		}
		
		if (steps % TileworldSettings.DYNAMISM == 0) {
			
			MetaAction metaAct = agent.step();
					
			// if the agent acted, move the agent and compute its reward
			if (metaAct != MetaAction.NOP && agent.getNextAction() != null)
			{
				moveAgent(agent.getCurrentState(), agent.getNextAction());
				
				agent.updatePlan();
				agent.reward();
			}
		}
		
		steps++;
		nextHole--;
		
		// first clear the message buffer
		mdp.clearMessageBuffer();
			
		decreaseLifetimeHoles();
		removeHoleIfVisited();
		notifyGUI();
		
		// keep stepping until the agent has done something
		// this is to ensure that a hypothetical simulation continues
		// until the agent has either deliberated or acted
		if (isHypothetical && !agent.hasExecutedAction())
			step();
	}
	
	public double getAgentScore() {
		return agent.getScore();
	}
	
	public double getMaxScore() {
		return maxScore;
	}
	
	public int getNextHole() {
		return nextHole;
	}
	
	protected void moveAgent(State currentState, Action selectedAction) 
	{
		// in the tileworld, everything is completely deterministic so moving is quite easy.
		// simply select the unique qstate and state and move there.
		
		QState qState = mdp.getQState(currentState, selectedAction);
		State newState = mdp.getQEdges(qState).get(0).getToVertex();

		agent.getCurrentState().setVisited(false);
		agent.setCurrentState(newState);
		newState.setVisited(true);
	}
	
	private void addHole() 
	{
		final State hole = mdp.getRandomEmptyState();
		
		int lifetime = MathOperations.getRandomInt(
				TileworldSettings.HOLE_LIFE_EXP_MIN, TileworldSettings.HOLE_LIFE_EXP_MAX);
	
		int score = MathOperations.getRandomInt(
				TileworldSettings.HOLE_SCORE_MIN, TileworldSettings.HOLE_SCORE_MAX);
		
		hole.setReward(score);
		hole.setLifeTime(lifetime);
		hole.setHole(true);
		
		this.maxScore += score;		

		mdp.addHole(hole);
		
		agent.inform(AgentMessage.HOLE_APPEARS, hole);
	}
	
	
	private void setNextHole()
	{
		nextHole = MathOperations.getRandomInt(
				TileworldSettings.HOLE_GESTATION_TIME_MIN, TileworldSettings.HOLE_GESTATION_TIME_MAX);
		//System.out.println("next hole in: " + nextHole);
	}
	
	private void decreaseLifetimeHoles() 
	{
		final List<State> toRemove = new LinkedList<State>();
		final List<State> holes = mdp.getHoles();
		
		for (State hole : holes) {
			hole.decreaseLifetime();
			
			if (hole.getLifetime() <= 0) {
				toRemove.add(hole);
			}
		}
		
		removeHoles(toRemove);
	}
	
	private void removeHoleIfVisited()
	{
		State s = agent.getCurrentState();
		
		if (s.isHole())
			removeHole(s);
	}
	
	private void removeHoles(List<State> holes)
	{
		for (State hole : holes) { 
			removeHole(hole);
			agent.inform(AgentMessage.HOLE_DISAPPEARS, hole);
		}
	}
	
	private void removeHole(State hole)
	{
		hole.setHole(false);
		mdp.removeHole(hole);
		
		hole.setReward(0.0);
		
		
	}
}
