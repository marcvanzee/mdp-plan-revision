package simulations;

import java.util.ArrayList;
import java.util.Random;

import mdps.PopulatedMDP;
import mdps.algorithms.MDPValueIterator;
import mdps.elements.Action;
import mdps.elements.Agent;
import mdps.elements.QEdge;
import mdps.elements.QState;
import mdps.elements.State;
import mdps.factories.MDPType;
import mdps.generators.GeneralMDPGenerator;
import mdps.modifiers.GeneralMDPModifier;

/**
 * A Model consists of a PopulatedMDP (i.e. an MDP and an Agent) and models the evolution of this MDP over time.
 * 
 * @author marc.vanzee
 *
 */
public class MDPSimulation extends BasicSimulation
{
	private final MDPValueIterator valueIterator;
	private final GeneralMDPGenerator mdpGenerator = new GeneralMDPGenerator();
	private final GeneralMDPModifier mdpChanger;
	private final Agent agent;
	private final PopulatedMDP populatedMDP;
	
	public MDPSimulation() {
		super(MDPType.POPULATED_MDP);
		
		populatedMDP = ((PopulatedMDP) mdp);
		
		agent = populatedMDP.getAgent();
		
		mdpChanger = new GeneralMDPModifier(agent);
		
		valueIterator = new MDPValueIterator(mdp);
	}
	
	//
	// GETTERS AND SETTERS
	//	
	public double getValue(State s) {
		return valueIterator.getValue(s);
	}
	
	public Agent getAgent() {
		return this.agent;
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void buildNewModel() 
	{
		if (isRunning)
			timer.cancel();
		
		populatedMDP.reset();
		
		// try adding an observer so that the MDP can send its changes directly to the GUI
				
		mdpGenerator.run(populatedMDP);
		
		populatedMDP.addAgentRandomly();
		
		steps = 0;
		
		notifyGUI();
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
		// first clear the message buffer of the mdp
		mdp.clearMessageBuffer();
		
		// get the next choice by the agent
		final Agent agent = populatedMDP.getAgent();
		
		if (agent != null)
			agent.step();
		
		// change the MDP (the mdp changer holds a reference to our agent)
		mdpChanger.run(mdp);

		// if the agent acted, move the agent and compute its reward
		if (agent != null && agent.getNextAction() != null)
			moveAgent(agent.getCurrentState(), agent.getNextAction());
	}
	
	protected void moveAgent(State currentState, Action selectedAction) 
	{
		final QState qState = mdp.getQState(currentState, selectedAction);
		final ArrayList<QEdge> qEdges = mdp.getQEdges(qState);
		
		// pick a random number [0,1] and determine which qEdge is selected
		double rand = (new Random()).nextDouble();
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
		
		agent.reward();
		
		agent.getCurrentState().setVisited(false);
		agent.setCurrentState(qEdge.getToVertex());
		agent.getCurrentState().setVisited(true);
	}
}
