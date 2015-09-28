package model;

import factories.MDPType;
import model.mdp.State;
import model.mdp.operations.GeneralMDPGenerator;
import model.mdp.operations.MDPValueIterator;

/**
 * A Model consists of a PopulatedMDP (i.e. an MDP and an Agent) and models the evolution of this MDP over time.
 * 
 * @author marc.vanzee
 *
 */
public class MDPSimulation extends BasicSimulation
{
	private final MDPValueIterator valueIterator = new MDPValueIterator();
	private final GeneralMDPGenerator mdpGenerator = new GeneralMDPGenerator();
	
	public MDPSimulation() {
		super(MDPType.POPULATED_MDP);
	}
	
	//
	// GETTERS AND SETTERS
	//	
	public double getValue(State s) {
		return valueIterator.getValue(mdp.getStates().indexOf(s));
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
				
		mdpGenerator.run(mdp);
		
		if (Settings.ADD_AGENT)
			((PopulatedMDP) mdp).addAgent();
		
		steps = 0;
		
		notifyGUI();
	}
}
