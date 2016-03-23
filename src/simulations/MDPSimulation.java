package simulations;

import java.lang.reflect.InvocationTargetException;

import mdp.MDP;
import mdp.operations.generators.GeneralMDPGenerator;
import mdp.operations.modifiers.MDPModifier;

/**
 * A Model consists of a PopulatedMDP (i.e. an MDP and an Agent) and models the evolution of this MDP over time.
 * 
 * @author marc.vanzee
 *
 */
public class MDPSimulation extends Simulation<MDP, GeneralMDPGenerator, MDPModifier>
{
	public MDPSimulation() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
	{
		super(MDP.class, GeneralMDPGenerator.class, MDPModifier.class);
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void buildNewModel() 
	{
		mdp.reset();
		
		// try adding an observer so that the MDP can send its changes directly to the GUI
				
		mdpGenerator.run();
		
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
		
		
		// change the MDP (the mdp changer holds a reference to our agent)
		mdpModifier.run();

	}
}
