package mdps;

import java.util.LinkedList;
import java.util.List;

import mdps.elements.State;
import mdps.elements.StateEdge;

/**
 * This is the Kinny and Georgeff TileWorld
 * 
 * @author marc.vanzee
 *
 */
public class TileWorld extends PopulatedMDP 
{
	List<State> holes = new LinkedList<State>();
	List<StateEdge> ses = new LinkedList<StateEdge>();
	
	public TileWorld() {
		super();
	}
		
	public void addHole(State hole) {
		holes.add(hole);
	}
	
	public List<State> getHoles() {
		return holes;
	}
	
	public void removeHole(State hole) {
		this.holes.remove(hole);
	}
	
	public StateEdge getStateEdge(State s1, State s2) {
		for (StateEdge se :ses) {
			if (s1 == se.getFromVertex() && s2 == se.getToVertex())
				return se;
		}
		return null;
	}
	
	public List<StateEdge> getStateEdges() {
		return this.ses;
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
	
	public void addStateEdge(StateEdge se) {
		ses.add(se);
	}
	
}
