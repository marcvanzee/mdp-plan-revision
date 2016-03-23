package mdp.operations.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import constants.MathOperations;
import mdp.MDP;
import mdp.elements.Action;
import mdp.elements.State;
import mdp.operations.MDPOperation;
import settings.GeneralMDPSettings;

/**
 * This class generates an arbitrary MDP in the method run(MDP), based on the settings in constants.SimulationSettings.
 * It implements the MDPDynamic interface
 * 
 * The states are generated using the following algorithm. The queue Q contains the nodes that still have to be processes.
 * 
 * Input: MDP, settings.
 * 
 * 1. Create an empty queue Q
 * 2. While (MDP.states.size() < settings.numStates)
 * 3.     If Q is empty: add new state S to Q. 
 * 4.     If MDP.states.size() > 0: connect an arbitrary state T in MDP.states to S (to avoid creating several disconnected graphs)
 * 5.     Select first state S in Q
 * 6.     Select an arbitrary subset A of the number of actions, on average: |A| = settings.avgActionsState
 * 7.     For each action a in A:
 * 8.          Action a is deterministic with probability = settings.pDeterministic
 * 9.          If a is deterministic: 
 * 10.            Find a single successor state. 
 * 11.            If setting.allowCycles = true:
 * 12.                Choose an existing state with p = (1 - settings.pCycle)
 * 13.                If existing state is chosen: Pick one from the existing states 
 * 14.                Else:                        Create a new state T
 * 15.            Else: Create a new state T
 * 16.            Connect S->T and put T in Q
 * 17.        If a is non-deterministic:
 * 18.            Select an arbitrary number of successor states TT with max_size(TT) = settings.maxSuccessorStates
 * 19.            Select m existing nodes with m = n * settings.pCycle
 * 20.            If |m| > MDP.states.size(): m = MDP.states.size()
 * 21.            Create k new nodes with k = n - m and put them in Q
 * 22.            Generate a probability distribution over the n nodes and connect them all to S.
 * 
 */
public class GeneralMDPGenerator extends MDPOperation<MDP>
{
	public GeneralMDPGenerator(MDP mdp) {
		super(mdp);
	}

	private final Random r = new Random();
		
	public void run() 
	{
		
		int countMaxStates = GeneralMDPSettings.NUM_STATES,
				numActions = GeneralMDPSettings.NUM_ACTIONS;
		
		mdp.reset();
		
		// add the number of actions we need
		mdp.addNumActions(numActions);
		
		// we implement the Queue simply using a LinkedList
		LinkedList<State> stateQueue = new LinkedList<State>();
		
		// don't allow cycles for the first state
		boolean cycles = GeneralMDPSettings.CYCLES_ALLOWED;
		GeneralMDPSettings.CYCLES_ALLOWED = false;
		
		while (mdp.countStates() < countMaxStates) 
		{
			// if the queue is empty, create a new state, else use first from queue and remove it
			State s = getNewState(mdp, stateQueue);

			LinkedList<Action> actionSet = generateActionSet(mdp);
			
			for (Action a : actionSet)
			{
				LinkedList<State> newQueue = generateStates(mdp, s, a, true);
				stateQueue.addAll(newQueue);
			}
			
			GeneralMDPSettings.CYCLES_ALLOWED = cycles;
		}
	}
	
	public LinkedList<State> generateStates(MDP mdp, State s, Action a, boolean initial)
	{
		LinkedList<State> returnQueue = new LinkedList<State>();
		
		int countMaxStates = GeneralMDPSettings.NUM_STATES;
		
		// a is deterministic with p = Settings.P_DETERMINISTIC
		if (MathOperations.throw_dice(GeneralMDPSettings.P_DETERMINISTIC))
		{
			State nextState;
			// we have to find a single successor state
			// if no cycles are allowed we have to create a new state
			// if cycles are allowed we create a new state with probability 1-Settings.P_CYCLE
			// we combine these two things in a single if-statement
			if (!GeneralMDPSettings.CYCLES_ALLOWED || MathOperations.throw_dice(1-GeneralMDPSettings.P_CYCLE))
			{
				
				nextState = mdp.addState();
				
				// add the state to the queue so we can process it later
				returnQueue.add(nextState);
			}
			
			// we do not create a new state but reuse an existing one
			else
			{
				nextState = mdp.getRandomState();
			}
			
			// now we are done. simply create a link from s to newState with p=1
			mdp.addTransition(s, a, nextState);
		}
		else 
		{	
			// a is non-deterministic
			// we select an arbitrary number of successor states in [1,Settings.MAX_SUCCESSOR_STATES]
			int numNewStates = r.nextInt(GeneralMDPSettings.MAX_SUCCESSOR_STATES-1)+1;
			
			ArrayList<State> nextStates;
			
			// if we allow no cycles, we have to generate all new states
			if (!GeneralMDPSettings.CYCLES_ALLOWED) 
			{
				// if this is the first time, do not create more states than we have to
				if (initial)
					numNewStates = Math.min(
							countMaxStates - mdp.countStates(), numNewStates);
				
				nextStates = mdp.addStates(numNewStates);				
			}
			
			else
			{
				// if cycles are allowed, select m existing nodes with m = n * Settings.P_CYCLE
				int countMaxExistingStates = (int)(numNewStates *  GeneralMDPSettings.P_CYCLE);
			
				nextStates = mdp.getRandomStates(countMaxExistingStates);
				
				// now create new states
				ArrayList<State> newStates = mdp.addStates(numNewStates - countMaxExistingStates);
				
				// add the new states to the queue
				returnQueue.addAll(newStates);
				
				nextStates.addAll(newStates);
			}
			
			// create transitions from s to the new states (probability distribution is generated in the method)
			mdp.addTransitions(s, a, nextStates);
		}
		
		return returnQueue;
	}
	
	// if the queue is empty, create a new state, else use first from queue and remove it
	private State getNewState(MDP mdp, LinkedList<State> stateQueue) 
	{
		if (stateQueue.size() == 0) 
		{
			State s = mdp.addState();
			
			// if there are already states in the mdp, then connect s to an existing state.
			// this avoids creating multiple disconnected graphs
			if (mdp.countStates() > 1) {
				State s0 = mdp.getRandomState(s);
								
				Action a = mdp.getRandomAction();
				
				mdp.addTransition(s0, a, s);
			}
			
			return s;
		}
		else 
		{
			State s = stateQueue.getFirst();
			stateQueue.removeFirst();
			return s;
		}
	}
	
	// generate a set of actions with size around settings.avgActionsState
	private LinkedList<Action> generateActionSet(MDP mdp) 
	{
		int countActions = mdp.countActions();
		int avgActions = GeneralMDPSettings.AVG_ACTIONS_STATE;
		int variance = GeneralMDPSettings.ACTION_VARIANCE;
		
		// we select randomly avgActions +/- SimulationSettings.actionsVariance
		// if avgActions = n, then we select max(n-actionsVariance,1) to min(numActions,n+actionsVariance) actions.
		int numActions = MathOperations.getRandomInt(avgActions, variance, 1, countActions);
				
		HashSet<Action> actionSetHash = new HashSet<Action>();
		
		// now generate numActions random actions between [0,countActions]
		while (actionSetHash.size() < numActions) 
		{
			int actionIndex = r.nextInt(countActions);
			actionSetHash.add(mdp.getAction(actionIndex));
		}
		
		return new LinkedList<Action>(actionSetHash);
	}
}