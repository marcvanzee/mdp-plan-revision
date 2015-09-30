package simulations;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import constants.MathOperations;
import mdps.Tileworld;
import mdps.elements.Action;
import mdps.elements.Agent;
import mdps.elements.QEdge;
import mdps.elements.QState;
import mdps.elements.State;
import mdps.factories.MDPType;
import mdps.generators.TileworldGenerator;
import settings.TileworldSettings;

/**
 * A TileWorldSimulation consists of a TileWorld (i.e. an MDP and an Agent) and models the evolution of this TileWorld over time.
 * 
 * @author marc.vanzee
 *
 */
public class TileworldSimulation extends BasicSimulation
{
	private final TileworldGenerator tileWorldGenerator = new TileworldGenerator();
	private final Agent agent;
	private final Tileworld tileworld;
	
	private int agentSteps = 0;
	
	private int nextHole;
	
	public TileworldSimulation() 
	{
		super(MDPType.TILEWORLD);
		agent = ((Tileworld) mdp).getAgent();
		tileworld = (Tileworld) mdp;
	}
	
	//
	// GETTERS AND SETTERS
	//	
	public double getValue(State s) {
		return agent.getValue(s);
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void buildNewModel() 
	{
		steps = 0;
		agentSteps = 0;
		
		if (isRunning)
			timer.cancel();
		
		tileworld.reset();
		
		// generate a tileworld	
		this.tileWorldGenerator.run(mdp);
		
		// add agent
		tileworld.addAgentRandomly(new HashSet<State>(tileworld.getObstacles()));
				
		notifyGUI();
		
		setNextHole();
	}
		
	public void step() 
	{
		steps++;
		nextHole--;
		
		if (nextHole <= 0) {
			addHole();
		}
		
		// first clear the message buffer
		mdp.clearMessageBuffer();
		
		if (steps % TileworldSettings.DYNAMISM == 0) {
			agentSteps++;
			
			// get the next choice by the agent
			if (agent != null)
				agent.step();
					
			// if the agent acted, move the agent and compute its reward
			if (agent != null && agent.getNextAction() != null)
				moveAgent(agent.getCurrentState(), agent.getNextAction());
		}
				
				
		decreaseLifetimeHoles();	
		notifyGUI();
	}
	
	protected void moveAgent(State currentState, Action selectedAction) 
	{
		// in the tileworld, everything is completely deterministic so moving is quite easy.
		// simply select the unique qstate and state and move there.
		
		final QState qState = mdp.getQState(currentState, selectedAction);
		final State newState = mdp.getQEdges(qState).get(0).getToVertex();
				
		agent.getCurrentState().setVisited(false);
		agent.setCurrentState(newState);
		newState.setVisited(true);
		
		agent.reward();
	}
	
	private void addHole() 
	{
		final State hole = tileworld.getRandomEmptyState();
		
		int lifetime = MathOperations.getRandomInt(
				TileworldSettings.HOLE_LIFE_EXP_MIN, TileworldSettings.HOLE_LIFE_EXP_MAX);
	
		int score = MathOperations.getRandomInt(
				TileworldSettings.HOLE_SCORE_MIN, TileworldSettings.HOLE_SCORE_MAX);
		
		hole.setReward(score);
		hole.setLifeTime(lifetime);
		hole.setHole(true);

		tileworld.addHole(hole);
						
		setNextHole();
		
		agent.recomputePolicy();
	}
	
	
	private void setNextHole()
	{
		nextHole = MathOperations.getRandomInt(
				TileworldSettings.HOLE_GESTATION_TIME_MIN, TileworldSettings.HOLE_GESTATION_TIME_MAX);
		System.out.println("next hole in: " + nextHole);
	}
	
	private void decreaseLifetimeHoles() 
	{
		final List<State> toRemove = new LinkedList<State>();
		final State agState = agent.getCurrentState();
		final List<State> holes = tileworld.getHoles();
		
		if (agState.isHole()) 
			agent.recomputePolicy();
		
		for (State hole : holes) {
			hole.decreaseLifetime();
			
			if (hole.getLifetime() <= 0 || hole == agState) {
				toRemove.add(hole);
			}
		}

		for (State hole : toRemove) {
			hole.setHole(false);
			tileworld.removeHole(hole);
			
			hole.setReward(0.0);
		}	
	}
}
