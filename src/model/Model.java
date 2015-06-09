package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;

import model.mdp.Action;
import model.mdp.ActionEdge;
import model.mdp.MDP;
import model.mdp.QEdge;
import model.mdp.State;
import model.mdp.valueiteration.ValueIterator;

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
	
	private Settings settings;
	private MDP mdp;
	private Random r = new Random();
	private ValueIterator valueIterator;
	
	boolean computedValueIteration = false;
	
	public void buildNewModel(Settings settings) 
	{
		this.settings = settings;
		mdp = new MDP(settings);
			
		generateModel();
		
		setChanged();
		notifyObservers();
	}
	
	public void computeOptimalPolicy()
	{
		valueIterator = new ValueIterator(mdp);
		valueIterator.startValueIteration();
		
		computedValueIteration = true;
		
		setChanged();
		notifyObservers();
	}
	
	public boolean computedValueIteration() {
		return computedValueIteration;
	}
	
	public MDP getMDP() {
		return mdp;
	}
	
	public HashSet<ActionEdge> getOptimalActionEdges() {
		return valueIterator.getOptimalActionEdges();
	}
	
	public HashSet<QEdge> getMostProbableQEdges() {
		return valueIterator.getMostProbableQEdges();
	}
	
	public double getValue(State s) {
		return valueIterator.getValue(mdp.getStates().indexOf(s));
	}
	
	/**
	 * generate the states as follows:
	 * 
	 * generate a new state S and put in a Queue Q
	 * Q contains all the states that have to be processed
	 * 
	 * create empty queue Q
	 * while size(states) < settings.numStates
	 * - if Q is empty: add state to Q
	 * - select first state S in Q
	 * - select an arbitrary subset A of the number of actions, average: |A| = settings.avgActionsState
	 * - for each action a in A:
	 *     - a is deterministic with p = Settings.P_DETERMINISTIC
	 *     - if a is deterministic: 
	 *         - find a single successor state. 
	 *         - if cycles are allowed, choose an existing state with 1 - Settings.P_CYCLE
	 *             - if existing state is chosen, pick one from the existing states 
	 *             - else create a new state, connect it and put it in Q
	 *         - else, create a new state, connect it and put it in Q
	 *     - else:
	 *         - select an arbitrary number of successor states with max(n)=Settings.MAX_SUCCESSOR_STATES
	 *         - select existing m existing nodes with m = n * Settings.P_CYCLE
	 *         - if |m| > size(states), let m = size(states)
	 *         - create k new nodes with k = n - m and put them in Q
	 *         - generate a probability distribution over the n nodes and connect them.
	 * 
	 */	
	private void generateModel() 
	{
		int countMaxStates = settings.getNumStates(),
				numActions = settings.getNumActions();
		
		mdp.reset(settings);
		
		// add the number of actions we need
		mdp.addNumActions(numActions);
		
		// we implement the Queue simply using a LinkedList
		LinkedList<State> stateQueue = new LinkedList<State>();
		
		while (mdp.countStates() < countMaxStates) 
		{
			// if the queue is empty, create a new state, else use first from queue and remove it
			State s = getNewState(stateQueue);

			System.out.println("--- state " + s.getName());
			
			LinkedList<Action> actionSet = generateActionSet();
			
			System.out.println("actions: " + actionSet.size());
						
			for (Action a : actionSet)
			{
				System.out.println("action " + a.getName());
				// a is deterministic with p = Settings.P_DETERMINISTIC
				if (throw_dice(Settings.P_DETERMINISTIC))
				{
					State nextState;
					System.out.println("deterministic");
					// we have to find a single successor state
					// if no cycles are allowed we have to create a new state
					// if cycles are allowed we create a new state with probability 1-Settings.P_CYCLE
					// we combine these two things in a single if-statement
					if (!settings.allowCycles() || throw_dice(1-Settings.P_CYCLE))
					{
						nextState = mdp.addState();
						
						System.out.println("connect with new state (" + nextState.getName() + ")");
						
						// add the state to the queue so we can process it later
						stateQueue.add(nextState);
					}
					
					// we do not create a new state but reuse an existing one
					else
					{
						
						nextState = mdp.getRandomState();
						
						System.out.println("connected to existing state (" + nextState);
					}
					
					// now we are done. simply create a link from s to newState with p=1
					mdp.addTransition(s, a, nextState);
				}
				else 
				{
					System.out.println("nondeterministc");
					// a is non-deterministic
					// we select an arbitrary number of successor states in [0,Settings.MAX_SUCCESSOR_STATES]
					int numNewStates = r.nextInt(Settings.MAX_SUCCESSOR_STATES);
					
					System.out.println("adding " + numNewStates + " new states");
					
					ArrayList<State> nextStates;
					
					// if we allow no cycles, we have to generate all new states
					if (!settings.allowCycles()) 
					{
						System.out.println("no cycles");
						
						// but do not create more states than we have to
						// do not create more states than numStates 
						int countMaxNewStates = countMaxStates - mdp.countStates();
						numNewStates = Math.min(countMaxNewStates, numNewStates);
						
						nextStates = mdp.addStates(numNewStates);				
					}
					
					else
					{
						System.out.println("cycles");
						// if cycles are allowed, select m existing nodes with m = n * Settings.P_CYCLE
						int countMaxExistingStates = (int)(numNewStates *  Settings.P_CYCLE);
					
						nextStates = mdp.getRandomStates(countMaxExistingStates);
						
						System.out.println("connecting to " + nextStates.size() + " existing states.");
						
						// now create new states
						ArrayList<State> newStates = mdp.addStates(numNewStates - countMaxExistingStates);
						
						System.out.println("connecting to " + newStates.size() + " existing states.");
						
						// add the new states to the queue
						stateQueue.addAll(newStates);
						
						nextStates.addAll(newStates);
					
					}
					
					System.out.println("adding " + nextStates.size() + " new states");
					// create transitions from s to the new states (probability distribution is generated in the method)
					mdp.addTransitions(s, a, nextStates);
				}
			}
		}		
	}
	
	
	// if the queue is empty, create a new state, else use first from queue and remove it
	private State getNewState(LinkedList<State> stateQueue) 
	{
		if (stateQueue.size() == 0) 
		{
			return mdp.addState();
		}
		else 
		{
			State s = stateQueue.getFirst();
			stateQueue.removeFirst();
			return s;
		}
	}
	
	// generate a set of actions with size around settings.avgActionsState
	private LinkedList<Action> generateActionSet() 
	{
		int countActions = mdp.countActions();
		int avgActions = settings.getAvgActionsState();
		
		// we select randomly avgActions +/- 3
		// so if avgActions = 5, then we select 2 to 8 actions.
		// if avgActions-3 < 1, then we select in [0,avgActions+3]
		// if avgActions+3 > numActions. then we select in [avgActions-3,numActions]
		int minActions = Math.max(1, avgActions-3);
		int maxActions = Math.min(countActions, avgActions+3);
		int numActions = r.nextInt((maxActions - minActions) + 1) + minActions;
		
		HashSet<Action> actionSetHash = new HashSet<Action>();
		
		// now generate numActions random actions between [0,countActions]
		while (actionSetHash.size() < numActions) 
		{
			int actionIndex = r.nextInt(countActions);
			actionSetHash.add(mdp.getAction(actionIndex));
		}
		
		System.out.println("actions generated: " + actionSetHash.size());
		return new LinkedList<Action>(actionSetHash);
	}
	
	private boolean throw_dice(double d) {
		return r.nextDouble() < d;
	}

}
