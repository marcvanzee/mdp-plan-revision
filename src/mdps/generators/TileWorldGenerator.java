package mdps.generators;

import java.util.List;

import constants.Settings;
import mdps.MDP;
import mdps.elements.Action;
import mdps.elements.QEdge;
import mdps.elements.State;


public class TileWorldGenerator extends MDPGenerator
{
	private static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
	
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
		
		int worldSize = Settings.WORLD_SIZE;
		
		// add states and store them in a two-dimensional array
		State[][] stateArr = new State[worldSize][worldSize];
		
		for (int i=0; i<worldSize; i++) 
		{
			for (int j=0; j<worldSize; j++) 
			{
				stateArr[i][j] = new State("(" + i + "," + j + ")");
				tileWorld.addState(stateArr[i][j]);
			}
		}
		
		// then add all transitions
		// since the domain is completely deterministic, we only have probabilities of 1
		for (int i=0; i<worldSize; i++) 
		{
			for (int j=0; j<worldSize; j++) 
			{
				if (i > 0) tileWorld.addTransition(stateArr[i][j], actionArr[LEFT] , stateArr[i-1][j]);
				if (j > 0) tileWorld.addTransition(stateArr[i][j], actionArr[UP] , stateArr[i][j-1]);
				if (i < worldSize-1) tileWorld.addTransition(stateArr[i][j], actionArr[RIGHT] , stateArr[i+1][j]);
				if (j < worldSize-1) tileWorld.addTransition(stateArr[i][j], actionArr[DOWN] , stateArr[i][j+1]);
			}
		}
		
		int numObstacles = ((int)(Settings.OBSTACLE_RATE * tileWorld.countStates()));
		List<State> obstacles = tileWorld.getRandomStates(numObstacles);
		
		for (State obstacle : obstacles) {
			obstacle.setObstacle(true);
		}
		
		// set rewards
		// everything to 0, except for obstacles, they are unreachable
		for (QEdge qe : tileWorld.getQEdges()) {
			if (obstacles.contains(qe.getToVertex())) {
				qe.setReward(-Settings.SCORE-Settings.SCORE_SD);
			} else {
				qe.setReward(0);
			}
		}
		
	}
	
		
}
