package model.mdp.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.SimulationSettings;
import model.mdp.Action;
import model.mdp.ActionEdge;
import model.mdp.MDP;
import model.mdp.QEdge;
import model.mdp.QState;
import model.mdp.State;
import constants.MathOperations;

public class MDPTransitionGenerator 
{
	private final MDP mdp;
	private final SimulationSettings settings = SimulationSettings.getInstance();
	protected final Random r = new Random();
	
	public MDPTransitionGenerator(MDP mdp) {
		this.mdp = mdp;
	}
	
	public void add(State s, Action a, ArrayList<State> toStates) 
	{
		ArrayList<Double> probabilities = generateProbDistribution(toStates.size());
		
		for (int i=0; i<toStates.size(); i++) 
		{
			add(s, a, toStates.get(i), probabilities.get(i));
		}
	}
	
	/**
	 * The function addTransition is overloaded. 
	 * - If no probability is given, it is assumed to be 1
	 * - if no reward is given, it is generated in [minReward,maxReward] from settings
	 */
	
	public void add(State s1, Action a, State s2) 
	{
		add(s1, a, s2, 1.0);
	}

	public void add(State s1, Action a, State s2, double probability) 
	{
		double minReward = settings.getMinReward();
		double maxReward = settings.getMaxReward();
		
		double reward = r.nextDouble() * (maxReward - minReward) + minReward;
		
		add(s1, a, s2, probability, reward);
	}
	
	public void add(State s1, Action a, State s2, double probability, double reward) 
	{
		if (s1 == null || a == null || s2 == null) {
			System.err.println("Trying to add a transition and reward but one of the arguments is null.");
			return;
		}
		
		List<State> states = mdp.getStates();
		List<Action> actions = mdp.getActions();
			
		if (!states.contains(s1) || !states.contains(s2) || !actions.contains(a)) {
			System.err.println("Trying to add transition and reward from " + s1.getName() + " to " 
					 + s2.getName() + " by action " + a.getName() + ", but the states or action do not exist in MDP.");
			return;
		}
		
		// first check whether there already exists a qState for this action
		QState qState = mdp.getQState(s1, a);
		
		// if not, create a new qState and a new edge from s1 to this qState
		if (qState == null) {
			
			qState = mdp.createQState();
			ActionEdge edge = mdp.createActionEdge(s1, qState, a);
			s1.addEdge(edge);
		}
		
		// if the qState existed already, check whether there already exists a QEdge from qState to s2
		// if so, do nothing
		else {
			if (mdp.getQEdge(qState, s2) != null) {
				System.err.println("Trying to add transition and reward from " + s1.getName() + " to " 
					 + s2.getName() + " by action " + a.getName() + ", but there already exists a transition here.");
				return;
			}
			
		}
		
		QEdge qEdge = mdp.createQEdge(qState, s2, probability, reward);
		qState.addEdge(qEdge);
	}
	
	private ArrayList<Double> generateProbDistribution(int countVariables) 
	{
		ArrayList<Double> probabilities = new ArrayList<Double>();
		
		double sum = 0;
	    
		for (int i=0; i<countVariables; i++) {
			double rand = r.nextDouble();
			
			probabilities.add(rand);
	    	sum += rand;
	    }
		
		for (int i=0; i < probabilities.size(); i++) {
			double d = probabilities.get(i);
			probabilities.set(i, MathOperations.round(d/sum,2));
	    }
	    
		return probabilities;
	}
}
