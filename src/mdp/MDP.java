package mdp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import mdp.elements.Action;
import mdp.elements.ActionEdge;
import mdp.elements.QEdge;
import mdp.elements.QState;
import mdp.elements.State;
import mdp.operations.generators.MDPTransitionGenerator;
import messaging.jung.ChangeMessage;
import messaging.jung.ChangeMessageBuffer;
import messaging.jung.edges.AddActionEdgesMessage;
import messaging.jung.edges.AddQEdgesMessage;
import messaging.jung.edges.RemoveActionEdgesMessage;
import messaging.jung.edges.RemoveQEdgesMessage;
import messaging.jung.states.AddQStatesMessage;
import messaging.jung.states.AddStatesMessage;
import messaging.jung.states.RemoveQStatesMessage;
import messaging.jung.states.RemoveStatesMessage;

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
 * And two kinds of edges:
 * - ActionEdge: An edge going from a State to a QState, containing an action.
 * - QEdge:      An edge going from a QState to a State, containing a reward and a probability.
 * 
 * Both states extend the generic Vertex<E> class, where E represent the outgoing edge.
 * So, State<ActionEdge> extends Vertex<E> and QState<QEdge> extends Vertex<E> as well.
 * 
 * Similarly, ActionEdge<State,QState> and QEdge<QState,State> both extend Edge<V,W> 
 *
 * 
 * This class extends the Observable class, which means it can be observed by gui.DrawPanel.
 * It sends a notification to the DrawPanel when a vertex or edge is added or removed.
 * 
 * @author marc.vanzee
 *
 */
public class MDP
{
	protected final ArrayList<State> states = new ArrayList<State>();
	protected final ArrayList<QState> qStates = new ArrayList<QState>();
	protected final ArrayList<ActionEdge> actionEdges = new ArrayList<ActionEdge>();
	protected final ArrayList<QEdge> qEdges = new ArrayList<QEdge>();
	protected final ArrayList<Action> actions = new ArrayList<Action>();	
	protected final MDPTransitionGenerator tGenerator = new MDPTransitionGenerator(this);
	protected final Random r = new Random();
	protected final ChangeMessageBuffer mBuffer = new ChangeMessageBuffer();
	
	public MDP()  {
	}

	/***********************
	 * GETTERS AND SETTERS
	 ***********************
	 */
	
	public int countStates() {
		return states.size();
	}
	
	public int countQStates() {
		return qStates.size();
	}
	
	public int countActions() {
		return actions.size();
	}
	
	public int countQEdges() {
		return qEdges.size();
	}
	
	public Action getAction(int index) {
		return actions.get(index);
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public List<State> getStates() {
		return states;
	}
	
	public List<QState> getQStates() {
		return qStates;
	}
	
	public List<ActionEdge> getActionEdges() {
		return actionEdges;
	}
	
	public List<QEdge> getQEdges() {
		return qEdges;
	}
	
	public State getState(int i) {
		return states.get(i);
	}
	
	public int getStateIndex(State s) {
		return states.indexOf(s);
	}
	
	public ChangeMessageBuffer getMessageBuffer() {
		return mBuffer;
	}
	
	public Action getRandomAction()
	{
		int index = r.nextInt(actions.size());
		return actions.get(index);
	}
	
	public State getRandomState(Set<State> exclude)
	{
		if (exclude.size() >= states.size())
			return null;
		
		State s0 = null;
		
		do {
			s0 = getRandomState();
		} while (exclude.contains(s0) || s0 == null);
		
		return s0;
	}
	
	public State getRandomState(State exclude)
	{
		State s0 = null;
		
		do {
			s0 = getRandomState();
		} while (s0 == exclude || s0 == null);
		
		return s0;
	}
	
	public State getRandomState()
	{
		int index = r.nextInt(states.size());
		return states.get(index);
	}
	
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
	
	public ActionEdge getActionEdge(State s, Action a) 
	{
		if (actionEdges == null) 
			return null;

		for (ActionEdge ae : actionEdges) 
		{
			if ((ae.getFromVertex() == s) && (ae.getAction() == a))
				return ae;
		}
		
		return null;
	}
	
	public ArrayList<QEdge> getQEdges(QState s)
	{
		if (this.qEdges == null)
			return null;
		
		ArrayList<QEdge> qes = null;
		
		for (QEdge qe: this.qEdges) 
		{
			if (qe.getFromVertex() == s)
			{
				if (qes == null)
					qes = new ArrayList<QEdge>();
				
				qes.add(qe);
			}
		}
		
		return qes;
	}
	
	public QEdge getQEdge(QState qState, State s) {
		for (QEdge edge : qState.getEdges()) {
			
			if (edge.getToVertex() == s)
				return edge;
		}
		
		return null;
	}
	
	public QState getQState(State s, Action a) {
		for (ActionEdge edge : s.getEdges()) {
			
			if (edge.getAction().getName().equals(a.getName()))
				return edge.getToVertex();
		}
		
		return null;
	}
	
	/***********************
	 * OTHER PUBLIC METHODS
	 ***********************
	 */
	
	//
	// MESSAGING METHODS
	//
	
	public void addState(State state) {
		states.add(state);
		
		addMessage(new AddStatesMessage(state));
	}
	
	public void addStates(List<State> states) {
		this.states.addAll(states);		
		
		addMessage(new AddStatesMessage(states));
	}
	
	// add a new state and return it so it can directly be used
	public State addState()
	{
		State s = new State(states.size() + "");
		states.add(s);
		
		addMessage(new AddStatesMessage(s));
		
		return s;
	}
	
	public QState createQState() {
		QState qState = new QState();
		this.qStates.add(qState);
		
		addMessage(new AddQStatesMessage(qState));
		
		return qState;
	}
	
	public ActionEdge createActionEdge(State s, QState qState, Action a) 
	{
		ActionEdge edge = new ActionEdge(s, qState, a);
		this.actionEdges.add(edge);
		
		addMessage(new AddActionEdgesMessage(edge));
		
		return edge;
	}
	
	public QEdge createQEdge(QState qState, State s, double prop) 
	{
		QEdge qEdge = new QEdge(qState, s, prop);
		this.qEdges.add(qEdge);
		
		addMessage(new AddQEdgesMessage(qEdge));
		
		return qEdge;
	}
	
	public void removeState(State s) 
	{
		ArrayList<ActionEdge> aes = s.getEdges();
		
		for (ActionEdge ae : aes)
		{
			QState qs = ae.getToVertex();
			ArrayList<QEdge> qes = qs.getEdges();
			
			qStates.remove(qs);
			
			qEdges.removeAll(qes);
			
			addMessage(new RemoveQStatesMessage(qs));
			addMessage(new RemoveQEdgesMessage(qes));
		}
		
		actionEdges.removeAll(aes);
		states.remove(s);
		
		addMessage(new RemoveActionEdgesMessage(aes));
		addMessage(new RemoveStatesMessage(s));
				
	}
	
	public void removeRandomState() {
		removeState(getRandomState());
	}
	
	public void removeRandomState(State exclude) 
	{
		State s = getRandomState(exclude);
		removeState(s);
	}
		
		
	//
	// OTHER METHODS
	//
		
	public String toString() {
		String str = "printing MDP.\n";
		str += "number of states: " + states.size() + "\n";
		str += "number of actions: " + actions.size() + "\n";
		str += "number of action edges: " + actionEdges.size() + "\n";
		str += "number of q edges: " + qEdges.size() + "\n";
		
		return str;
	}
		
	public void reset() {
		states.clear();
		qStates.clear();
		actionEdges.clear();
		qEdges.clear();
		
		mBuffer.clear();
	}
	
	public void clearMessageBuffer() {
		mBuffer.clear();
	}
	
	public void addAction(Action a) {
		actions.add(a);
	}
	
	// TODO: VERY BAD!!!
	public Action getAction(String name) {
		for (Action a : actions) {
			if (a.getName().equals(name))
				return a;
		}
		
		return null;
	}
	
	public void addActions(ArrayList<Action> actions) {
		this.actions.addAll(actions);
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
	
	public void addTransition(State s, Action a, State nextState) {
		tGenerator.add(s, a, nextState);
	}

	public void addTransitions(State s, Action a, ArrayList<State> nextStates) {
		tGenerator.add(s, a, nextStates);
	}
	
	public void addMessage(ChangeMessage cm) {
		mBuffer.addMessage(cm);
	}
}
