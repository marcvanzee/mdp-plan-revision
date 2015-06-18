package model.mdp.operations;

import java.util.ArrayList;
import java.util.HashMap;

import model.mdp.Action;
import model.mdp.ActionEdge;
import model.mdp.MDP;
import model.mdp.QEdge;
import model.mdp.QState;
import model.mdp.State;
import constants.MathOperations;

public class MDPValueIterator extends MDPOperation
{	
	// use these mappings for efficiency so we don't have to look them up every time
	HashMap<State,ArrayList<ActionEdge>> stateToActionEdges = new HashMap<State,ArrayList<ActionEdge>>();
	HashMap<QState,ArrayList<QEdge>> qStateToQEdges = new HashMap<QState,ArrayList<QEdge>>();
		
	// V contains the V values for each node, while prevV contains those of the previous iteration
	double V[], prevV[];
	
	// the optimal policy will be stored, simply mapping state indices to actions
	Action policy[];
	
	double theta = settings.getTheta(),
			gamma = settings.getGamma();
		
	//
	// CONSTRUCTORS
	//
	
	public MDPValueIterator()
	{
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public Action[] getPolicy() {
		return this.policy;
	}
	
	public Action getPolicy(int i) {
		return policy[i];
	}
	
	public double getValue(int i) {
		return V[i];
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void run(MDP mdp) 
	{
		this.mdp = mdp;
		
		initializeMappings();
		
		int countStates = mdp.countStates();
		
		// V:S->R is a function from states to rational numbers
		// we implement this simply as an array of doubles.
		// all doubles are initialized to 0 by default.
		V = new double[countStates];
		prevV = new double[countStates];
		policy = new Action[countStates];
				
		int k = 0;
		boolean finished = true;
		
		do {
			k++;
			for (int i=0; i<countStates; i++)
			{
				prevV[i] = V[i];
				setMaxValue(i);
				
				if ((V[i] - prevV[i]) >= theta) 
					finished = false;
				
			}
		} while (!finished && k < settings.getValueIterations());
		
		setOptimalVerticesAndEdges();
	}
	
	
	//
	// PRIVATE METHODS
	//
	
	private void initializeMappings() 
	{
		for (State s : mdp.getStates()) 
		{
			ArrayList<ActionEdge> actionEdges = new ArrayList<ActionEdge>();
			
			for (ActionEdge ae : mdp.getActionEdges())
			{
				if (ae.getFromVertex() == s) 
					actionEdges.add(ae);
			}
			
			stateToActionEdges.put(s, actionEdges);
		}
		
		for (QState qs: mdp.getQStates())
		{
			ArrayList<QEdge> qEdges = new ArrayList<QEdge>();
			
			for (QEdge qe : mdp.getQEdges())
			{
				if (qe.getFromVertex() == qs)
					qEdges.add(qe);
			}
			
			qStateToQEdges.put(qs, qEdges);
		}
	}
	
	private void setMaxValue(int i) 
	{
		double max = Integer.MIN_VALUE;
		Action a = null;
		
		State s = mdp.getState(i);
		
		if (stateToActionEdges.size() == 0)
			return;
		
		for (ActionEdge ae : stateToActionEdges.get(s)) 
		{
			double sum = 0;
			QState toState = ae.getToVertex();
			
			if (!qStateToQEdges.containsKey(toState))
				continue;
			
			for (QEdge qe : qStateToQEdges.get(toState))
			{
				sum += qe.getProbability() * (qe.getReward() + gamma * prevV[i]);
			}
			
			if (sum > max) {
				max = sum;
				a = ae.getAction();
			}
		}
		
		V[i] = max;
		policy[i] = a;
	}
	
	private void setOptimalVerticesAndEdges() 
	{
		for (int i=0; i<mdp.countStates(); i++)
		{
			// set the name of the state to its value obtained from value iteration
			State s = mdp.getState(i);
			double stateValue = V[i];
			
			s.setName((stateValue < 0.01 ? 0 : MathOperations.round(stateValue,2))+"");
			
			// highlight the actions that are chosen by the policy
			Action a = policy[i];
			
			if (a == null)
				continue;
			
			ActionEdge ae = mdp.getActionEdge(s, a);
			
			if (ae != null)
				ae.setOptimal(true);
			
			// also highlight the most probable outcome for each action in the policy
			
			QState s2 = ae.getToVertex();
			ArrayList<QEdge> qEdges = mdp.getQEdges(s2);
			
			QEdge qEdge = getMostProbableQEdge(qEdges);
			
			if (qEdge != null) 
				qEdge.setOptimal(true);
		}
	}
	
	private QEdge getMostProbableQEdge(ArrayList<QEdge> qEdges) 
	{
		QEdge mostProbableQEdge = null;
		double maxProbability = 0;
		
		for (QEdge qEdge : qEdges) 
		{
			double prob = qEdge.getProbability();
			
			if (prob > maxProbability)
			{
				maxProbability = prob;
				mostProbableQEdge = qEdge;
			}
		}
		
		return mostProbableQEdge;
	}

}
