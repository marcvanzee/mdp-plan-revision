package model.mdp.operations;

import model.SimulationSettings;
import model.mdp.MDP;

/**
 * The MDPOperation abstract class can be inherited by any class that implements a operation on an MDP.
 * 
 * An operation can be changing an MDP, generating an MDP, or computing something for the MDP
 * 
 * @author marc.vanzee
 *
 */
public abstract class MDPOperation 
{	
	protected MDP mdp = null;
	protected SimulationSettings settings = SimulationSettings.getInstance();
	
	public abstract void run(MDP mdp); // run the operation
}
