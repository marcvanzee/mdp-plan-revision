package model.mdp;

import java.awt.Color;


public class State extends Vertex<ActionEdge> 
{
	boolean visited = false;
	
	public State(String name) {
		super(name);
		this.color = Color.RED;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
		
		this.color = visited ? Color.YELLOW : Color.RED;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 20;
	}	
}
