package model;

import java.util.LinkedList;
import java.util.List;

import messaging.ChangeMessageBuffer;
import model.mdp.QEdge;
import model.mdp.State;
import model.mdp.StateEdge;
import model.mdp.operations.TileWorldChanger;

/**
 * This is the Kinny and Georgeff TileWorld
 * 
 * @author marc.vanzee
 *
 */
public class TileWorld extends PopulatedMDP 
{
	List<State> holes = new LinkedList<State>();
	List<StateEdge> ses = new LinkedList<StateEdge>();
	
	public TileWorld() {
		super();
		mdpChanger = new TileWorldChanger();
	}
	
	/**
	 * Evolves the MDP one step:
	 * 1. Agent deliberates or acts, depending on its current state and the MDP.
	 * 2. The MDP evolves, depending on the choice of the agent and the simulation settings.
	 *    -> If the agent chooses to deliberate, the MDP will evolve less than if it chooses to act
	 *    -> The evolution settings are specified in model.SimulationSettings.
	 */
	public void step() 
	{
		// first clear the message buffer
		mBuffer = new ChangeMessageBuffer();
		
		// get the next choice by the agent
		if (agent != null)
			agent.step();
		
		// if the agent acted, move the agent and compute its reward
		if (agent != null && agent.getNextAction() != null)
			moveAgent(agent.getCurrentState(), agent.getNextAction());
		
		
		decreaseLifetimeHoles();		
	}
	
	public void addHole(State hole) {
		holes.add(hole);
	}
	
	public StateEdge getStateEdge(State s1, State s2) {
		for (StateEdge se :ses) {
			if (s1 == se.getFromVertex() && s2 == se.getToVertex())
				return se;
		}
		return null;
	}
	
	public List<StateEdge> getStateEdges() {
		return this.ses;
	}
	
	private void decreaseLifetimeHoles() {
		List<State> toRemove = new LinkedList<State>();
		
		State agState = agent.getCurrentState();
		
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
			this.holes.remove(hole);
			
			for (QEdge qe : getQEdges()) {
				if (qe.getToVertex() == hole) {
					qe.setReward(0);
				}
			}
		}
		
		
	}
	
	public State getRandomEmptyState() 
	{
		State ret = null;
		
		do {
			ret = getRandomState(agent.getCurrentState());
			if (ret.isHole() || ret.isObstacle()) {
				ret = null;
			}
		} while (ret == null);
		
		return ret;
	}
	
	public void addStateEdge(StateEdge se) {
		ses.add(se);
	}
	
}
