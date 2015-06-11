package model;

import java.util.Observable;

import messaging.MessageType;
import model.mdp.MDP;
import model.mdp.State;
import model.mdp.operations.MDPGenerator;
import model.mdp.operations.MDPValueIterator;

/**
 * A model is the entire simulation.
 * Currently it's just an MDP.
 * 
 * This is the MODEL
 * 
 * @author marc.vanzee
 *
 */
public class Model extends Observable 
{
	private MDP mdp;
	private MDPValueIterator valueIterator;
	private MDPGenerator mdpGenerator;
	
	public void buildNewModel() 
	{
		mdp = new MDP();
		mdpGenerator = new MDPGenerator(mdp);
		mdpGenerator.run();
		
		notify(MessageType.RELOAD_MDP);
	}
	
	public void computeOptimalPolicy()
	{
		valueIterator = new MDPValueIterator(mdp);
		valueIterator.run();
		
		notify(MessageType.REFRESH_MDP);
	}
	
	public MDP getMDP() {
		return mdp;
	}
	
	public double getValue(State s) {
		return valueIterator.getValue(mdp.getStates().indexOf(s));
	}

	private void notify(int type) {
		setChanged();
		notifyObservers(type);
	}
}
