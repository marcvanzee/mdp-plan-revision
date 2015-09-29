package mdps;

import java.util.LinkedList;
import java.util.List;

import mdps.elements.State;

/**
 * This is the Kinny and Georgeff TileWorld
 * 
 * @author marc.vanzee
 *
 */
public class Tileworld extends PopulatedMDP 
{
	final List<State> holes = new LinkedList<State>();	
	final List<State> obstacles = new LinkedList<State>();	
	private State[][] stateArr = null;
	
	public Tileworld() {
		super();
	}
		
	public void setDimension(int d) {
		stateArr = new State[d][d];
	}
	
	public void addHole(State hole) {
		holes.add(hole);
	}
	
	public void removeHole(State hole) {
		this.holes.remove(hole);
	}
	
	
	public List<State> getHoles() {
		return holes;
	}
	
	public void addObstacle(State o) {
		obstacles.add(o);
	}
	
	public void removeObstacle(State o) {
		obstacles.remove(o);
	}
	
	public List<State> getObstacles() {
		return obstacles;
	}
	
	public void addState(State s, int i, int j) {
		if (i < 0 || j < 0 || stateArr == null || stateArr[0] == null || stateArr.length < i || stateArr[0].length < j)
			return;
		
		stateArr[i][j] = s;
		
		addState(s);
	}
			
	public State getRandomEmptyState() 
	{
		State ret = null;
		
		do {
			ret = getRandomState(agent.getCurrentState());
			if (ret.isHole() || ret.isObstacle()) {
				ret = null;
			}
		} while (ret == null);
		
		return ret;
	}
	
}
