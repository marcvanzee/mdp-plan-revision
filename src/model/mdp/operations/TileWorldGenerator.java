package model.mdp.operations;

import model.mdp.Action;
import model.mdp.MDP;
import model.mdp.State;

public class TileWorldGenerator extends MDPOperation
{
	private static final int WORLD_SIZE = 5,
			UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
	
	@Override
	public void run(MDP tileWorld) {
		
		// add actions and store them in an array
		Action[] actionArr = new Action[4];
		
		actionArr[UP] = new Action("up");
		actionArr[RIGHT] = new Action("right");
		actionArr[DOWN] = new Action("down");
		actionArr[LEFT] = new Action("left");
		
		for (Action a : actionArr) {
			tileWorld.addAction(a);
		}
		
		// add states and store them in a two-dimensional array
		State[][] stateArr = new State[WORLD_SIZE][WORLD_SIZE];
		
		for (int i=0; i<WORLD_SIZE; i++) 
		{
			for (int j=0; j<WORLD_SIZE; j++) 
			{
				stateArr[i][j] = new State("(" + i + "," + j + ")");
				tileWorld.addState(stateArr[i][j]);
			}
		}
		
		// then add all transitions
		// since the domain is completely deterministic, we only have probabilities of 1
		for (int i=0; i<WORLD_SIZE; i++) 
		{
			for (int j=0; j<WORLD_SIZE; j++) 
			{
				if (i > 0) tileWorld.addTransition(stateArr[i][j], actionArr[LEFT] , stateArr[i-1][j]);
				if (j > 0) tileWorld.addTransition(stateArr[i][j], actionArr[UP] , stateArr[i][j-1]);
				if (i < WORLD_SIZE-1) tileWorld.addTransition(stateArr[i][j], actionArr[RIGHT] , stateArr[i+1][j]);
				if (j < WORLD_SIZE-1) tileWorld.addTransition(stateArr[i][j], actionArr[DOWN] , stateArr[i][j+1]);
			}
		}
	}
	
		
}
