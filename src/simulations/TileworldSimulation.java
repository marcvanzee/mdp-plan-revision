package simulations;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import constants.MathOperations;
import constants.Printing;
import mdp.Tileworld;
import mdp.agent.Agent;
import mdp.agent.Angel;
import mdp.agent.MetaAction;
import mdp.agent.ShortestPathAgent;
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
	private int hypothesisDepth = TileworldSettings.HYPOTHESIS_DEPTH;
	
	public TileworldSimulation() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
	{
		super(Tileworld.class, TileworldGenerator.class, TileworldModifier.class);
		agent = mdp.getAgent();
		
		if (agent instanceof Angel)
			((Angel) agent).setSimulation(this);
	}
		
	public Tileworld getTileworld() {
		return mdp;
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


		if (agent instanceof Angel)
			((Angel) agent).reset();
		
		else if (agent instanceof ShortestPathAgent)
			((ShortestPathAgent) agent).reset();
		
		else
			agent.reset();
		
		// generate a tileworld	
		this.mdpGenerator.run();
				
		if (TileworldSettings.TEST_ENV)
		{
			addTestAgent();
			addTestHoles();
		}
		else
		{
			// add agent
			mdp.addAgentRandomly(new HashSet<State>(mdp.getObstacles()));
		
			// add holes
			for (int i=0; i<TileworldSettings.INITIAL_NR_HOLES; i++)
					addHole();
		}
	
		notifyGUI();
		
		setNextHole();	
		
		if (agent instanceof Angel)
			// load a new minimal tileworld representation of the current tileworld
			((Angel) agent).updateSimulation();
	}
	
	private void addTestAgent()
	{
		mdp.addAgentAt(2, 2);
	}
	
	private void addTestHoles()
	{
		addObstacle(mdp.getState(2, 0));
		addObstacle(mdp.getState(3, 0));
		addObstacle(mdp.getState(0, 3));
		addObstacle(mdp.getState(1, 1));
		addObstacle(mdp.getState(4, 1));
		addObstacle(mdp.getState(4, 2));
		addObstacle(mdp.getState(0, 3));
		addObstacle(mdp.getState(3, 3));
		addObstacle(mdp.getState(4, 3));
		addObstacle(mdp.getState(0, 4));
		addObstacle(mdp.getState(2, 4));
		addObstacle(mdp.getState(3, 4));
		
		addHole(mdp.getState(1,0),127,127);
		
		/*
		if (steps == 0)
		{
			addHole(mdp.getState(0, 4), 10, 10);
		}
		
		else if (steps == 4)
		{
			addHole(mdp.getState(2, 0), 4, 40);
		}*/
	}
		
	public void startSimulation(int maxSteps) {
		while (steps < maxSteps) {
			step();
		}
	}
		
	public void step() 
	{		
		//if (steps % 1000 == 0)
		//	System.out.println("<sim> at step " + steps);
		Printing.sim("Step");
		
		if (TileworldSettings.TEST_ENV)
		{
			addTestHoles();
			removeHoleIfVisited();
		}
		else if (nextHole <= 0) {
			addHole();
			
			// if the agent happens to be on the location where the hole has just 
			// been created, reward it and remove the hole
			removeHoleIfVisited();
			setNextHole();
		}
		
		if (steps % TileworldSettings.DYNAMISM == 0) 
		{
			Printing.sim("agent steps");
			
			MetaAction metaAct = agent.step();
			
			Printing.sim("agent did " + metaAct);
					
			// if the agent acted, move the agent and compute its reward
			if (metaAct == MetaAction.ACT && agent.getNextAction() != null)
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
		
		addHole(hole, lifetime, score);
		
	}
	
	private void addHole(State hole, int lifetime, int score)
	{
		if (mdp.getHoles().contains(hole))
			return;
		
		hole.setReward(score);
		hole.setLifeTime(lifetime);
		hole.setHole(true);
		
		this.maxScore += score;		

		mdp.addHole(hole);
		
		agent.inform(AgentMessage.HOLE_APPEARS, hole);
	}
	
	private void addObstacle(State hole)
	{
		hole.setObstacle(true);
		mdp.addObstacle(hole);
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
		{
			removeHole(s);
		}
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
		hole.setLifeTime(0);
		mdp.removeHole(hole);
		
		hole.setReward(0.0);
		
		
	}
}
