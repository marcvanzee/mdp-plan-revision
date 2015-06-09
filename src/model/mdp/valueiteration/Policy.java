package model.mdp.valueiteration;

import java.util.ArrayList;
import java.util.Iterator;

import model.mdp.Action;
import model.mdp.State;

/**
 * A policy is simply an assignment from states to actions
 * 
 * @author marc.vanzee
 *
 */
public class Policy implements Iterable<State>
{
	ArrayList<State> states = new ArrayList<State>();
	ArrayList<Action> actions = new ArrayList<Action>();
	
	public Policy() 
	{
		
	}
	
	public void add(State s, Action a) {
		states.add(s);
		actions.add(a);
	}

	@Override
	public Iterator<State> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
