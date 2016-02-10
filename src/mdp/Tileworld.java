package mdp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constants.MathOperations;
import mdp.agent.Agent;
import mdp.agent.Angel;
import mdp.agent.LearningAgent;
import mdp.agent.ShortestPathAgent;
import mdp.agent.ValueIterationAgent;
import mdp.elements.Action;
import mdp.elements.State;
import mdp.elements.TileworldActionType;
import settings.TileworldSettings;

/**
 * This is the Kinny and Georgeff TileWorld
 * 
 * @author marc.vanzee
 *
 */
public class Tileworld extends MDP
{
	private final List<State> holes = new LinkedList<State>();	
	private final List<State> obstacles = new LinkedList<State>();	
	private State[][] stateArr = null;
	private final Map<State,State> statePolicy = new HashMap<State,State>();
	private final Map<TileworldActionType,Action> actionMap = new HashMap<TileworldActionType,Action>();
	protected final Agent agent;
	
	public Tileworld() {
		
		switch(TileworldSettings.ALGORITHM) {
		case SHORTEST_PATH: 	this.agent = new ShortestPathAgent(this); break;
		case ANGELIC:			this.agent = new Angel(this); break;
		case VALUE_ITERATION:	this.agent = new ValueIterationAgent(this); break;
		case LEARNING:			this.agent = new LearningAgent(this); break;
		default:				this.agent = null;
		}
	}
		
	public void setDimension(int d) {
		stateArr = new State[d][d];
		
		for (int i = 0; i<d; i++)
		{
			stateArr[i] = new State[d];
		}
	}
	
	public int getDimension() {
		return stateArr.length;
	}
	
	public void addHole(State hole) {
		holes.add(hole);
	}
	
	public void removeHole(State hole) {
		this.holes.remove(hole);
	}
	
	public State[][] getStateArray()
	{
		return this.stateArr;
	}
		
	public State getStateAtSameCoord(State s)
	{
		return s == null ? null : stateArr[s.getX()][s.getY()];
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
	
	public int countObstacles()
	{
		return obstacles.size();
	}
	
	public Action getAction(TileworldActionType tat)
	{
		return actionMap.get(tat);
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
		
		if ((holes.size() + obstacles.size() + 1) == states.size()) {
			// no free states left!
			return null;
		}
		
		
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
				neighbors.get(MathOperations.getRandomInt(0, neighbors.size()-1)) : null;
		
	}
	
	public List<State> getNeighbors(State s) 
	{
		int x = s.getX(),
				y = s.getY(),
				worldSize = TileworldSettings.WORLD_SIZE;
		
		List<State> ret = new LinkedList<State>();
		
		if (x > 0) {
			State left = getState(x-1,y);
			if (!left.isObstacle())
				ret.add(left);
		}
		
		if (y > 0) {
			State above = getState(x,y-1);
			if (!above.isObstacle())
				ret.add(above);
		}
		
		if (x < worldSize-1) {
			State right = getState(x+1,y);
			if (!right.isObstacle())
				ret.add(right);
		}
		
		if (y < worldSize-1) {
			State below = getState(x,y+1);
			if (!below.isObstacle())
				ret.add(below);
		}
		
		return ret;
	}
	
	public void reset() {
		holes.clear();
		obstacles.clear();
		stateArr = null;
		statePolicy.clear();
		super.reset();
	}
	
	public void addDefaultActions()
	{
		for (TileworldActionType tat : TileworldActionType.values())
		{
			Action a = new Action(tat);
			addAction(a);
			actionMap.put(tat, a);
		}
	}
	
	public void addTransition(State s1, TileworldActionType tat, State s2)
	{
		addTransition(s1, actionMap.get(tat), s2);
	}
	
	public Agent getAgent() {
		return agent;
	}
		
	public void updateAgent() {
		agent.update();
	}
	
	public void addAgentRandomly() {
		agent.setCurrentStateRandomly();
	}
	
	public void addAgentRandomly(Set<State> excludeStates) {
		agent.setCurrentState(getRandomState(excludeStates));
	}
	
	public void addAgentAt(int x, int y)
	{
		agent.setCurrentState(getState(x, y));
	}
	
}
