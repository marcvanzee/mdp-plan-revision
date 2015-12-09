package UNUSED.old;

import java.util.ArrayList;

public class Agent {
	Model m;
	int x;
	int y;
	
	public Agent(Model m, int x, int y) {
		this.m = m;
		this.x = x;
		this.y = y;
	}
	
	public void move() {
		State_old curState = m.getStates()[x][y];
		
		ArrayList<Action> actions = curState.getActions();
		
		if (actions == null || actions.size() == 0) {
			System.out.println("No action possible!");
			return;
		}
		
		Action selAction = actions.get(0);
		
		ArrayList<Transition> transitions = selAction.getTransitions();
		
		if (transitions == null || transitions.size() == 0) {
			return;
		}
		
		State_old nextState = transitions.get(0).getState();
		
		this.x = nextState.x;
		this.y = nextState.y;
	}
	
}
