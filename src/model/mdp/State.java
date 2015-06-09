package model.mdp;


public class State extends Vertex<ActionEdge> {
	String name;
	
	public State(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
