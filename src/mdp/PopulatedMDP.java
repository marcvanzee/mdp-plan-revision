package mdp;

import java.util.Set;

import mdp.agent.Agent;
import mdp.elements.State;

/**
 * A PopulatedMDP consists of an MDP and an agent.
 * 
 * @author marc.vanzee
 *
 */
public class PopulatedMDP extends MDP
{
	protected final Agent agent = new Agent(this);
	
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
	
	public void updateAgent() {
		agent.update();
	}
	
	public void addAgentRandomly() {
		agent.setCurrentStateRandomly();
	}
	
	public void addAgentRandomly(Set<State> excludeStates) {
		agent.setCurrentState(getRandomState(excludeStates));
	}
}
