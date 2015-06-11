package model;

import model.mdp.State;

/**
 * An agent resides in an MDP. It has two action:
 * 1. Move: 
 *     Move to the next state according to the computed policy.
 *     This is only possible if a policy has been computed.
 *     
 * 2. Deliberate:
 *     Recompute a policy for the current MDP.
 *     
 * The agent executes the following algorithm:
 * 
 * 
 * 1. While simulation not ended:
 * 2.     If no policy exists: deliberate.
 * 3.     If next action in the policy is not executable: deliberate.
 * 4.     Deliberate with p = SimulationSettings.pDeliberate.
 * 5.     If not deliberate: execute next action on the policy.
 * 
 * @author marc.vanzee
 *
 */
public class Agent 
{
	private State currentState;
	
	public Agent() {
		
	}
	
	//public 
}
