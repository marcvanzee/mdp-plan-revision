package model;

import gui.Settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import constants.Operations;

/**
 * A Markov Decision Process (S,A,T,Pr,R) is structure containing:
 * S: A set of states
 * A: A set of actions
 * T: A transition function: T(s,a,s') is the probability of ending 
 *    up in state s' by taking action a in s. We can also write this
 *    as a conditional probability: Pr(s'|s,a).
 * R: reward function. R(s,a,s')
 * 
 * We implement the MDP using two kinds of states:
 * - State:   An agent is positioned in a state, and can choose to execute an action a in A.
 * - Q-State: After executing the action, the agent ends up in a Q-state. From here, "nature" 
 *            decides in what state the agent ends up in, depending on the transition function T
 *
 * @author marc.vanzee
 *
 */
public class MDP {
	
	ArrayList<State> states;
	ArrayList<QState> qStates;
	ArrayList<ActionEdge> actionEdges;
	ArrayList<QEdge> qEdges;
	
	Settings settings = new Settings();
	
	ArrayList<Action> actions = new ArrayList<Action>();
	
	Random r = new Random();
	
	/***********************
	 * CONSTRUCTORS
	 ***********************
	 */
	public MDP(Settings settings) {
		init(settings);
	}
	
	/***********************
	 * GETTERS AND SETTERS
	 ***********************
	 */
	
	public int countStates() {
		return states.size();
	}
	
	public int countActions() {
		return actions.size();
	}
	
	public Action getAction(int index) {
		return actions.get(index);
	}
	
	public ArrayList<State> getStates() {
		return states;
	}
	
	public ArrayList<QState> getQStates() {
		return qStates;
	}
	
	public ArrayList<ActionEdge> getActionEdges() {
		return actionEdges;
	}
	
	public ArrayList<QEdge> getQEdges() {
		return qEdges;
	}
	
	public State getRandomState()
	{
		int index = r.nextInt(states.size());
		return states.get(index);
	}
	
	/***********************
	 * OTHER PUBLIC METHODS
	 ***********************
	 */
	
	public ArrayList<State> getRandomStates(int numStates) 
	{
		if (numStates > states.size()) 
		{
			return null;
		} else if (numStates == states.size()) {
			return states;
		} 
		
		HashSet<State> randomStates = new HashSet<State>();
		
		while (randomStates.size() < numStates) 
		{
			randomStates.add(getRandomState());
		}
		
		return new ArrayList<State>(randomStates);
	}
	
	
	
	public void addState(State state) {
		states.add(state);
	}
	
	public void addStates(ArrayList<State> states) {
		this.states.addAll(states);
	}
	
	public void addAction(Action a) {
		actions.add(a);
	}
	
	public void addActions(ArrayList<Action> actions) {
		this.actions.addAll(actions);
	}
	
	// add transitions from s to a list of state. 
	// the probability distribution over the list is generated in the method
	public void addTransitions(State s, Action a, ArrayList<State> toStates) 
	{
		ArrayList<Double> probabilities = generateProbDistribution(toStates.size());
		
		for (int i=0; i<toStates.size(); i++) 
		{
			addTransition(s, a, toStates.get(i), probabilities.get(i));
		}
	}
	
	/**
	 * The function addTransition is overloaded. 
	 * - If no probability is given, it is assumed to be 1
	 * - if no reward is given, it is generated in [minReward,maxReward] from settings
	 */
	
	public void addTransition(State s1, Action a, State s2) 
	{
		addTransition(s1, a, s2, 1.0);
	}

	public void addTransition(State s1, Action a, State s2, double probability) 
	{
		double minReward = settings.getMinReward();
		double maxReward = settings.getMaxReward();
		
		double reward = r.nextDouble() * (maxReward - minReward) + minReward;
		
		addTransition(s1, a, s2, 1.0, reward);
	}
	
	public void addTransition(State s1, Action a, State s2, double probability, double reward) 
	{
		if (s1 == null || a == null || s2 == null) {
			System.err.println("Trying to add a transition and reward but one of the arguments is null.");
			return;
		}
			
		if (!states.contains(s1) || !states.contains(s2) || !actions.contains(a)) {
			System.err.println("Trying to add transition and reward from " + s1.getName() + " to " 
					 + s2.getName() + " by action " + a.getName() + ", but the states or action do not exist in MDP.");
			return;
		}
		
		// first check whether there already exists a qState for this action
		QState qState = getQState(s1, a);
		
		// if not, create a new qState and a new edge from s1 to this qState
		if (qState == null) {
			
			qState = createQState();
			ActionEdge edge = createActionEdge(s1, qState, a);
			s1.addEdge(edge);
		}
		
		// if the qState existed already, check whether there already exists a QEdge from qState to s2
		// if so, do nothing
		else {
			if (getQEdge(qState, s2) != null) {
				System.err.println("Trying to add transition and reward from " + s1.getName() + " to " 
					 + s2.getName() + " by action " + a.getName() + ", but there already exists a transition here.");
				return;
			}
			
		}
		
		QEdge qEdge = createQEdge(qState, s2, probability, reward);
		qState.addEdge(qEdge);
	}
	
	public void reset(Settings settings) {
		init(settings);
	}

	
	// add a new state and return it so it can directly be used
	public State addState()
	{
		State s = new State(states.size() + "");
		states.add(s);
		return s;
	}
	
	// add a number of new states and return the states themselves in a list
	public ArrayList<State> addStates(int numStates) 
	{
		ArrayList<State> newStates = new ArrayList<State>();
		
		for (int i=0; i<numStates; i++) {
			newStates.add(addState());
		}
		
		return newStates;
	}
	
	public void addNumActions(int numActions) 
	{
		int currentActions = actions.size();
		
		for (int i=0; i<numActions; i++) 
		{
			int actionID= currentActions + i;
			actions.add(new Action(Integer.toString(actionID)));
		}
	}
	
	public String toString() {
		String str = "printing MDP.\n";
		str += "number of states: " + states.size() + "\n";
		str += "number of actions: " + actions.size() + "\n";
		str += "number of action edges: " + actionEdges.size() + "\n";
		str += "number of q edges: " + qEdges.size() + "\n";
		
		return str;
	}
	
	/***********************
	 * PRIVATE METHODS
	 ***********************
	 */
	
	private QState getQState(State s, Action a) {
		for (ActionEdge edge : s.getEdges()) {
			
			if (edge.getAction() == a)
				return edge.getToVertex();
		}
		
		return null;
	}
	
	private QEdge getQEdge(QState qState, State s) {
		for (QEdge edge : qState.getEdges()) {
			
			if (edge.getToVertex() == s)
				return edge;
		}
		
		return null;
	}

	private void init(Settings settings) {
		this.settings = settings;
		states = new ArrayList<State>();
		qStates = new ArrayList<QState>();
		actionEdges = new ArrayList<ActionEdge>();
		qEdges = new ArrayList<QEdge>();
	}
		
	private QState createQState() {
		QState qState = new QState();
		this.qStates.add(qState);
		
		return qState;
	}
	
	private ActionEdge createActionEdge(State s, QState qState, Action a) {
		ActionEdge edge = new ActionEdge(s, qState, a);
		this.actionEdges.add(edge);
		
		return edge;
	}
	
	private QEdge createQEdge(QState qState, State s, double prop,
			double reward) {
		QEdge qEdge = new QEdge(qState, s, prop, reward);
		this.qEdges.add(qEdge);
		
		return qEdge;
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
			probabilities.set(i, Operations.round(d/sum,2));
	    }
	    
		return probabilities;
	}
}
