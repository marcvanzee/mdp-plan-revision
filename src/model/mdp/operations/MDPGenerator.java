package model.mdp.operations;

import model.mdp.MDP;

/**
 * The MDPOperation abstract class can be inherited by any class that implements a operation on an MDP.
 * 
 * An operation can be changing an MDP, generating an MDP, or computing something for the MDP
 * 
 * @author marc.vanzee
 *
 */
public abstract class MDPGenerator 
{	
	protected MDP mdp = null;
	
	public abstract void run(MDP mdp); // run the operation
}
