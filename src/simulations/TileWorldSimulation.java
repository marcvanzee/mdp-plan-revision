package simulations;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import constants.Settings;
import mdps.TileWorld;
import mdps.algorithms.MDPValueIterator;
import mdps.elements.Action;
import mdps.elements.ActionEdge;
import mdps.elements.Agent;
import mdps.elements.QEdge;
import mdps.elements.QState;
import mdps.elements.State;
import mdps.elements.StateEdge;
import mdps.factories.MDPType;
import mdps.generators.GeneralMDPGenerator;
import messaging.jung.edges.AddStateEdgesMessage;
import messaging.jung.states.AddStatesMessage;

/**
 * A TileWorldSimulation consists of a TileWorld (i.e. an MDP and an Agent) and models the evolution of this TileWorld over time.
 * 
 * @author marc.vanzee
 *
 */
public class TileWorldSimulation extends BasicSimulation
{
	private final MDPValueIterator valueIterator = new MDPValueIterator();
	private final GeneralMDPGenerator tileWorldGenerator = new GeneralMDPGenerator();
	private final Agent agent;
	private final TileWorld tileworld;
	
	private int nextHole;
	
	public TileWorldSimulation() {
		super(MDPType.TILEWORLD);
		agent = ((TileWorld) mdp).getAgent();
		tileworld = (TileWorld) mdp;
	}
	
	//
	// GETTERS AND SETTERS
	//	
	public double getValue(State s) {
		return valueIterator.getValue(this.mdp.getStates().indexOf(s));
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void buildNewModel() 
	{
		final List<State> states = mdp.getStates();
		steps = 0;
		
		if (isRunning)
			timer.cancel();
		
		mdp.reset();
		
		// generate a tileworld	
		this.tileWorldGenerator.run(mdp);
					
		// now add states and transltions for GUI, leave out qstates because the domain is deterministic
		mdp.addMessage(new AddStatesMessage(states));
		
		for (State s : states) {
			for (ActionEdge ae : s.getEdges()) {
				if (ae.getToVertex().getEdges().size() != 1) continue;
				
				StateEdge se = new StateEdge(s, ae.getToVertex().getEdges().get(0).getToVertex(), ae.getAction());
				
				mdp.addMessage(new AddStateEdgesMessage(se));
				
				((TileWorld)mdp).addStateEdge(se);
			}
		}
		
		notifyGUI();
		
		nextHole = 0;
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
				
		// get the next choice by the agent
		if (agent != null)
			agent.step();
				
		// if the agent acted, move the agent and compute its reward
		if (agent != null && agent.getNextAction() != null)
			moveAgent(agent.getCurrentState(), agent.getNextAction());
				
				
		decreaseLifetimeHoles();	
		notifyGUI();
	}
	
	protected void moveAgent(State currentState, Action selectedAction) 
	{
		// in the tileworld, everything is completely deterministic so moving is quite easy.
		// simply select the unique qstate and state and move there.
		
		final QState qState = mdp.getQState(currentState, selectedAction);
		final QEdge qEdge = mdp.getQEdges(qState).get(0);
		
		agent.reward(qEdge.getReward());
		
		agent.getCurrentState().setVisited(false);
		agent.setCurrentState(qEdge.getToVertex());
		agent.getCurrentState().setVisited(true);
	}
	
	private void addHole() 
	{
		final State hole = tileworld.getRandomEmptyState();
		final Random r = new Random();
		
		hole.setHole(true);

		tileworld.addHole(hole);
				
		final int low = Settings.SCORE - Settings.SCORE_SD,
				high = Settings.SCORE + Settings.SCORE_SD,
				score = r.nextInt(high-low) + low;
		
		for (QEdge qe : tileworld.getQEdges()) {
			if (qe.getToVertex() == hole) {
				qe.setReward(score);
			}
		}
		
		setNextHole();
		agent.clearPolicy();
	}
	
	
	private void setNextHole()
	{
		final Random r = new Random();
		final int low = Settings.GESTATION_PERIOD - Settings.GESTATION_PERIOD_SD,
				high = Settings.GESTATION_PERIOD + Settings.GESTATION_PERIOD_SD;
		
		nextHole = r.nextInt(high-low) + low;
	}
	
	private void decreaseLifetimeHoles() 
	{
		final List<State> toRemove = new LinkedList<State>();
		final State agState = agent.getCurrentState();
		final List<State> holes = tileworld.getHoles();
		
		if (agState.isHole()) 
			agent.clearPolicy();
		
		for (State hole : holes) {
			hole.decreaseLifetime();
			
			if (hole.getLifetime() <= 0 || hole == agState) {
				toRemove.add(hole);
			}
		}

		for (State hole : toRemove) {
			hole.setHole(false);
			tileworld.removeHole(hole);
			
			for (QEdge qe : mdp.getQEdges()) {
				if (qe.getToVertex() == hole) {
					qe.setReward(0);
				}
			}
		}	
	}
}
