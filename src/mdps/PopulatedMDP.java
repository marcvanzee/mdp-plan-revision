package mdps;

import mdps.elements.Agent;

/**
 * A PopulatedMDP consists of an MDP and an agent.
 * 
 * @author marc.vanzee
 *
 */
public class PopulatedMDP extends MDP
{
	final Agent agent = new Agent(this);
	
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
}
