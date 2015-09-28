package model;

import java.util.ArrayList;

import messaging.ChangeMessageBuffer;
import model.mdp.Action;
import model.mdp.MDP;
import model.mdp.QEdge;
import model.mdp.QState;
import model.mdp.State;
import model.mdp.operations.MDPChanger;
import model.mdp.operations.MDPGenerator;

/**
 * A PopulatedMDP consists of an MDP and an agent, and the interaction between these two entities.
 * 
 * @author marc.vanzee
 *
 */
public class PopulatedMDP extends MDP
{
	Agent agent;
	MDPGenerator mdpChanger = new MDPChanger(agent);
	
	//
	// CONSTRUCTORS
	//
	public PopulatedMDP() {
		super();
	}
	
	//
	// GETTERS AND SETTERS
	//
	public Agent getAgent() {
		return agent;
	}
		
	//
	// OTHER PUBLIC METHODS
	//
	
	public void addAgent() {
		agent = new Agent(this);
		setVertexColor();
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
		
		// change the MDP (the mdp changer holds a reference to our agent)
		mdpChanger.run(this);

		// if the agent acted, move the agent and compute its reward
		if (agent != null && agent.getNextAction() != null)
			moveAgent(agent.getCurrentState(), agent.getNextAction());
	}
	
	protected void moveAgent(State currentState, Action selectedAction) 
	{
		QState qState = getQState(currentState, selectedAction);
		
		ArrayList<QEdge> qEdges = getQEdges(qState);
		
		// pick a random number [0,1] and determine which qEdge is selected
		double rand = r.nextDouble();
		
		double sum = 0;
		int i;
		for (i=0; i<qEdges.size(); i++) {
			if (sum >= rand) {
				break;
			}
			sum += qEdges.get(i).getProbability();
		}
		
		i--;
		QEdge qEdge = qEdges.get(i);
		
		agent.reward(qEdge.getReward());
		
		agent.getCurrentState().setVisited(false);
		agent.setCurrentState(qEdge.getToVertex());
		
		setVertexColor();
	}
	
	private void setVertexColor() {
		agent.getCurrentState().setVisited(true);
	}
}
