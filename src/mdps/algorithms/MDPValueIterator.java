package mdps.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mdps.MDP;
import mdps.Tileworld;
import mdps.elements.Action;
import mdps.elements.ActionEdge;
import mdps.elements.QEdge;
import mdps.elements.QState;
import mdps.elements.State;
import settings.ValueIterationSettings;

/**
 * TODO: This is now completely optimized for Tileworld!
 * 
 * @author marc.vanzee
 *
 */
public class MDPValueIterator
{	
	final protected MDP mdp;
	
	// use these mappings for efficiency so we don't have to look them up every time
	final Map<State,ArrayList<ActionEdge>> stateToActionEdges = new HashMap<State,ArrayList<ActionEdge>>();
	
	// only works in deterministic domain
	// we create a hashmap from s -> map(a,s)
	final Map<State,Map<Action,State>> stateActionToState = new HashMap<State,Map<Action,State>>();
	final Map<QState,ArrayList<QEdge>> qStateToQEdges = new HashMap<QState,ArrayList<QEdge>>();
		
	// V contains the V values for each node, while prevV contains those of the previous iteration
	final Map<State, Double> V = new HashMap<State,Double>(), 
			prevV = new HashMap<State,Double>();
	
	// the optimal policy will be stored, simply mapping states to actions
	final Map<State,Action> policy = new HashMap<State,Action>();
		
	double theta = ValueIterationSettings.THETA,
			gamma = ValueIterationSettings.GAMMA; 
		
	//
	// CONSTRUCTORS
	//
	
	public MDPValueIterator(MDP mdp)
	{
		this.mdp = mdp;		
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public Map<State,Action> getPolicy() {
		return this.policy;
	}
	
	public Action getPolicy(State s) {
		return policy.get(s);
	}
	
	public double getValue(State s) {
		return V.get(s);
	}
	
	//
	// OTHER PUBLIC METHODS
	//
	
	public void update() {
		initializeMappings();
	}
	
	public void run(MDP mdp) 
	{
		// in the tileworld we do not have to recompute the mappings because the environment is static
		if (! (mdp instanceof Tileworld))
			initializeMappings();
		
		for (State s : mdp.getStates())
			V.put(s, 0.0);
		
		// we don't use highlights
		// removeHighlights();
		
		int k = 0;
		
		boolean finished = true;
		
		do {
			finished = true;
			k++;
			for (State s : mdp.getStates())
			{
				
				prevV.put(s, V.get(s));
				
				updateV(s);
				
				if ((V.get(s) - prevV.get(s)) >= theta) {
					finished = false;
				}
				
			}
		} while (k < ValueIterationSettings.ITERATIONS && !finished);
		
		computePolicy();
		
		setOptimalVerticesAndEdges();
	}
	
	private void updateV(State s) 
	{
		double max = Integer.MIN_VALUE;
		
		if (stateToActionEdges.size() == 0)
			return;
		
		for (ActionEdge ae : stateToActionEdges.get(s)) 
		{
			QState toState = ae.getToVertex();
			
			if (!qStateToQEdges.containsKey(toState))
				continue;
			
			// only one successor state since domain is deterministic
			State s2 = qStateToQEdges.get(toState).get(0).getToVertex();
			
			double reward = s.getReward(),
					previousV = prevV.containsKey(s2) ? prevV.get(s2) : V.get(s2);
			
			double v = reward + gamma * previousV;
			
			if (v > max)
				max = v;
		}
		
		V.put(s, max);
	}
	
	// we compute mappings in advance so we have constant lookup time during execution
	private void initializeMappings() 
	{
		// state to action edges mappings
		// and (state->(action->state) mappings (for deterministic domains)
		// and initial values for value iteration
		for (State s : mdp.getStates()) 
		{
			// value iteration
			V.put(s, 0.0);
			
			ArrayList<ActionEdge> actionEdges = new ArrayList<ActionEdge>();
			Map<Action,State> asMap = new HashMap<Action,State>();
			
			for (ActionEdge ae : mdp.getActionEdges())
			{
				if (ae.getFromVertex() == s) 
				{
					Action a = ae.getAction();
					QState qs = ae.getToVertex();
					List<QEdge> qes = qs.getEdges();
					
					if (qes.size() == 1) {
						State s2 = qes.get(0).getToVertex();
						if (!s2.isObstacle()) {
							asMap.put(a, s2);
							actionEdges.add(ae);
						}
					}
				}					
			}
			
			stateActionToState.put(s, asMap);
			stateToActionEdges.put(s, actionEdges);
		}
		
		// qstate to qedges mappings
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
	
	private void computePolicy()
	{
		for (State s : mdp.getStates()) 
		{
			if (s.isObstacle())
				continue;
			
			s.setValue(V.get(s));
			
			double max = Double.MIN_VALUE;
			Action a = null;
			
			for (Map.Entry<Action, State> entry : stateActionToState.get(s).entrySet()) 
			{
				State s2 = entry.getValue();
				double value = V.get(s2);
				
				if (value > max) 
				{
					max = value;
					a = entry.getKey();
				}
			}
			
			policy.put(s, a);
		}
	}
	
	// TODO: optimized for tileworld, change again for general setting
	private void setOptimalVerticesAndEdges() 
	{
		for (State s : mdp.getStates())
		{
			s.setValue(V.get(s));
			
			// highlight the actions that are chosen by the policy
			Action a = policy.get(s);
			
			((Tileworld) mdp).addStatePolicy(s, stateActionToState.get(s).get(a));
			
			/*
			if (a == null)
				continue;
			
			ActionEdge ae = stateToActionEdges.get(s);
			
			if (ae != null)
				ae.setOptimal(true);
			
			// also highlight the most probable outcome for each action in the policy
			
			QState s2 = ae.getToVertex();
			ArrayList<QEdge> qEdges = mdp.getQEdges(s2);
			
			QEdge qEdge = getMostProbableQEdge(qEdges);
			
			if (qEdge != null) 
				qEdge.setOptimal(true);*/
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
