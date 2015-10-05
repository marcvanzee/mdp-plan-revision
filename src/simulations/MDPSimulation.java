package simulations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import mdp.PopulatedMDP;
import mdp.agent.Agent;
import mdp.elements.Action;
import mdp.elements.QEdge;
import mdp.elements.QState;
import mdp.elements.State;
import mdp.operations.modifiers.PopulatedMDPModifier;
import mdps.operations.generators.GeneralMDPGenerator;

/**
 * A Model consists of a PopulatedMDP (i.e. an MDP and an Agent) and models the evolution of this MDP over time.
 * 
 * @author marc.vanzee
 *
 */
public class MDPSimulation extends Simulation<PopulatedMDP, GeneralMDPGenerator, PopulatedMDPModifier>
{
	final Agent agent;
	
	public MDPSimulation() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
	{
		super(PopulatedMDP.class, GeneralMDPGenerator.class, PopulatedMDPModifier.class);
		this.agent = mdp.getAgent();
	}

	//
	// GETTERS AND SETTERS
	//	
	public double getValue(State s) {
		return this.agent.getValue(s);
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
		
		mdp.reset();
		
		// try adding an observer so that the MDP can send its changes directly to the GUI
				
		mdpGenerator.run();
		
		mdp.addAgentRandomly();
		
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
		
		if (this.agent != null)
			this.agent.step();
		
		// change the MDP (the mdp changer holds a reference to our agent)
		mdpModifier.run();

		// if the agent acted, move the agent and compute its reward
		if (this.agent != null && this.agent.getNextAction() != null)
			moveAgent(this.agent.getCurrentState(), this.agent.getNextAction());
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
		
		this.agent.reward();
		
		this.agent.getCurrentState().setVisited(false);
		this.agent.setCurrentState(qEdge.getToVertex());
		this.agent.getCurrentState().setVisited(true);
	}
}
