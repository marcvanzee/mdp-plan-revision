package mdps.generators;

import java.util.List;

import mdps.MDP;
import mdps.Tileworld;
import mdps.elements.Action;
import mdps.elements.QEdge;
import mdps.elements.State;
import settings.TileworldSettings;


public class TileworldGenerator extends MDPGenerator
{
	private static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
	
	@Override
	public void run(MDP mdp) {
		
		Tileworld tileworld = ((Tileworld) mdp);
		
		// add actions and store them in an array
		Action[] actionArr = new Action[4];
		
		actionArr[UP] = new Action("up");
		actionArr[RIGHT] = new Action("right");
		actionArr[DOWN] = new Action("down");
		actionArr[LEFT] = new Action("left");
		
		for (Action a : actionArr) {
			tileworld.addAction(a);
		}
		
		int worldSize = TileworldSettings.WORLD_SIZE;
		
		// add states and store them in a two-dimensional array
		State[][] stateArr = new State[worldSize][worldSize];
		
		tileworld.setDimension(worldSize);
		
		for (int i=0; i<worldSize; i++) 
		{
			for (int j=0; j<worldSize; j++) 
			{
				State s = new State("state at (" + i + "," + j + ")");
				s.setCoord(i, j);
				stateArr[i][j] = s;
				
				tileworld.addState(stateArr[i][j], i, j);
			}
		}
		
		// then add all transitions
		// since the domain is completely deterministic, we only have probabilities of 1
		for (int i=0; i<worldSize; i++) 
		{
			for (int j=0; j<worldSize; j++) 
			{
				if (i > 0) tileworld.addTransition(stateArr[i][j], actionArr[LEFT] , stateArr[i-1][j]);
				if (j > 0) tileworld.addTransition(stateArr[i][j], actionArr[UP] , stateArr[i][j-1]);
				if (i < worldSize-1) tileworld.addTransition(stateArr[i][j], actionArr[RIGHT] , stateArr[i+1][j]);
				if (j < worldSize-1) tileworld.addTransition(stateArr[i][j], actionArr[DOWN] , stateArr[i][j+1]);
			}
		}
		
		// add obstacles
		int numObstacles = TileworldSettings.INITIAL_NR_WALLS;
		List<State> obstacles = tileworld.getRandomStates(numObstacles);
		
		for (State obstacle : obstacles) {
			obstacle.setObstacle(true);
			tileworld.addObstacle(obstacle);
		}
		
		// set rewards
		// everything to 0, except for obstacles, they are unreachable
		for (QEdge qe : tileworld.getQEdges()) {
			qe.setReward(0);
		}
		
		tileworld.updateAgent();
		
	}
	
		
}
