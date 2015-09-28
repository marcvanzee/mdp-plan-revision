package mdps.elements;

public class StateEdge extends Edge<State,State> {
	Action a;
	
	public StateEdge(State from, State to, Action a) {
		super(from, to);
		this.a = a;
	}
	
	public Action getAction() {
		return a;
	}
	
	public String toString() {
		return a.getName();
	}
}
