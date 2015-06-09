package model.mdp.valueiteration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import model.mdp.Action;
import model.mdp.ActionEdge;
import model.mdp.MDP;
import model.mdp.QEdge;
import model.mdp.QState;
import model.mdp.State;

public class ValueIterator
{	
	MDP mdp = null;
	
	// we store these for coloring in the GUI
	HashSet<ActionEdge> optimalActionEdges = new HashSet<ActionEdge>();
	HashSet<QEdge> mostProbableQEdges = new HashSet<QEdge>();
	
	// use these mappings for efficiency so we don't have to look them up every time
	HashMap<State,ArrayList<ActionEdge>> stateToActionEdges = new HashMap<State,ArrayList<ActionEdge>>();
	HashMap<QState,ArrayList<QEdge>> qStateToQEdges = new HashMap<QState,ArrayList<QEdge>>();
	
	// parameters for value iteration: http://artint.info/html/ArtInt_227.html
	double theta = 0.5;
	double gamma = 0.9;
	
	// V contains the V values for each node, while prevV contains those of the previous iteration
	double V[], prevV[];
	
	// the optimal policy will be stored, simply mapping state indices to actions
	Action policy[];
	
	public ValueIterator(MDP mdp)
	{
		this.mdp = mdp;
		initializeMappings();
	}
	
	public void startValueIteration() 
	{
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
		} while (!finished && k < 100);
		
		System.out.println(k + " iterations");
		
		computeEdgeValues();
	}
	
	public Action getPolicy(int i) {
		return policy[i];
	}
	
	public double getValue(int i) {
		return V[i];
	}
	
	public HashSet<ActionEdge> getOptimalActionEdges() {
		return optimalActionEdges;
	}
	
	public HashSet<QEdge> getMostProbableQEdges() {
		return mostProbableQEdges;
	}
		
	private void initializeMappings() 
	{
		System.out.println("--- initializing mappings");
		for (State s : mdp.getStates()) 
		{
			System.out.print("state: " + s.getName());
			ArrayList<ActionEdge> actionEdges = new ArrayList<ActionEdge>();
			
			for (ActionEdge ae : mdp.getActionEdges())
			{
				if (ae.getFromVertex() == s) 
					actionEdges.add(ae);
			}
			
			stateToActionEdges.put(s, actionEdges);
			System.out.println("--> edges: " + actionEdges.size());
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
		
		System.out.println("state " + i + ": " + (a==null?"null":a.getName()) + " (" + max + ")");
		V[i] = max;
		policy[i] = a;
	}
	
	private void computeEdgeValues() 
	{
		for (int i=0; i<mdp.countStates(); i++)
		{
			Action a = policy[i];
			
			if (a == null)
				continue;
			
			State s = mdp.getState(i);
			ActionEdge ae = mdp.getActionEdge(s, a);
			
			if (ae != null)
				optimalActionEdges.add(ae);
			
			QState s2 = ae.getToVertex();
			ArrayList<QEdge> qEdges = mdp.getQEdges(s2);
			
			QEdge qEdge = getMostProbableQEdge(qEdges);
			
			if (qEdge != null) 
				mostProbableQEdges.add(qEdge);			
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
