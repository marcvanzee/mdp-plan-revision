package mdps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import constants.MathOperations;
import mdps.elements.State;
import settings.TileworldSettings;

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
	final Map<State,State> statePolicy = new HashMap<State,State>();
	
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
	
	public State getState(int x, int y)
	{
		return 
				stateArr != null && stateArr[0] != null &&
				x >= 0 && y >= 0 &&
				x < stateArr.length && y < stateArr[0].length ? 
						stateArr[x][y] : null;
	}
	
	public void addObstacle(State o) {
		obstacles.add(o);
	}
	
	public void removeObstacle(State o) {
		obstacles.remove(o);
	}
	
	public void addStatePolicy(State s1, State s2) {
		statePolicy.put(s1, s2);
	}
	
	public Map<State,State> getStatePolicy() {
		return statePolicy;
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
	
	public State getRandomEmptyNeighbor(State s)
	{
		int x = s.getX(),
				y = s.getY();
		
		List<State> neighbors = new LinkedList<State>();
		
		State l = getState(x-1,y), r = getState(x+1,y),
				a = getState(x,y-1), b = getState(x,y+1);
		
		
		if (l != null && !l.isObstacle()) neighbors.add(getState(x-1,y));
		if (r != null && !r.isObstacle()) neighbors.add(getState(x+1, y));
		if (a != null && !a.isObstacle()) neighbors.add(getState(x,y-1));
		if (b != null && !b.isObstacle()) neighbors.add(getState(x, y+1));
		
		return neighbors.size() > 0 ?
				neighbors.get(MathOperations.getRandomInt(0, neighbors.size())) : null;
		
	}
	
	public void reset() {
		holes.clear();
		obstacles.clear();
		stateArr = null;
		statePolicy.clear();
		super.reset();
	}
	
}
