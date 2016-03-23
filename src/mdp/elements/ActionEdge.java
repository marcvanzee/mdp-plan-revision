package mdp.elements;

public class ActionEdge extends Edge<State,QState> {
	Action a;
	
	public ActionEdge(State from, QState to, Action a) {
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
