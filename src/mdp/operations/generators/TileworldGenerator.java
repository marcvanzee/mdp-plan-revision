package mdp.operations.generators;

import java.util.LinkedList;

import constants.MathOperations;
import mdp.Tileworld;
import mdp.elements.State;
import mdp.elements.TileworldActionType;
import mdp.operations.MDPOperation;
import settings.TileworldSettings;


public class TileworldGenerator extends MDPOperation<Tileworld>
{	
	public TileworldGenerator(Tileworld mdp) {
		super(mdp);
	}
	
	@Override
	public void run() 
	{
		buildEmptyTileworld();
	
		if (TileworldSettings.TEST_ENV) {
			buildTestSetup();
			return;
		}
		
		// add walls
		int numWalls = TileworldSettings.INITIAL_NR_WALLS;
		
		final LinkedList<State> curWall = new LinkedList<State>();
		
		while (numWalls > 0) 
		{
			// create a wall
			
			curWall.clear();
			State curState = null;
			
			int wallSize = MathOperations.getRandomInt(
					TileworldSettings.WALL_SIZE_MIN, TileworldSettings.WALL_SIZE_MAX);
			
			while (wallSize > 0)
			{
				// add a block for the wall
				if (curState == null) {
					curState = mdp.getRandomEmptyState();
					
					if (curState == null) {
						System.out.println("No empty state found!");
						break;
					}
				}
				
				else 
				{
					for (State s2 : curWall) {
						curState = mdp.getRandomEmptyNeighbor(s2);
						if (curState != null) break;
					}
					
					if (curState == null) {
						// unable to expand the wall, so just leave it like it is
						break;
					}
				}
				
				curState.setObstacle(true);
				mdp.addObstacle(curState);
				curWall.addFirst(curState);
				
				wallSize--;
			}
			
			numWalls--;			
		}
		mdp.updateAgent();
	}
	
	public void buildEmptyTileworld() 
	{
		// add tileworld actions
		mdp.addDefaultActions();
		
		int worldSize = TileworldSettings.WORLD_SIZE;
		
		// add states and store them in a two-dimensional array
		State[][] stateArr = new State[worldSize][worldSize];
		
		mdp.setDimension(worldSize);
		
		for (int i=0; i<worldSize; i++) 
		{
			for (int j=0; j<worldSize; j++) 
			{
				State s = new State("state at (" + i + "," + j + ")");
				s.setCoord(i, j);
				stateArr[i][j] = s;
				
				mdp.addState(stateArr[i][j], i, j);
			}
		}
		
		// then add all transitions
		// since the domain is completely deterministic, we only have probabilities of 1
		for (int i=0; i<worldSize; i++) 
		{
			for (int j=0; j<worldSize; j++) 
			{
				if (i > 0) mdp.addTransition(stateArr[i][j], TileworldActionType.LEFT , stateArr[i-1][j]);
				if (j > 0) mdp.addTransition(stateArr[i][j], TileworldActionType.UP , stateArr[i][j-1]);
				if (i < worldSize-1) mdp.addTransition(stateArr[i][j], TileworldActionType.RIGHT , stateArr[i+1][j]);
				if (j < worldSize-1) mdp.addTransition(stateArr[i][j], TileworldActionType.DOWN , stateArr[i][j+1]);
			}
		}
		
		mdp.updateAgent();
	}
	
	public void buildTestSetup()
	{
		
	}
}
