package model.mdp;

import java.awt.Color;

public class State extends Vertex<ActionEdge> 
{
	boolean visited = false;
	boolean isObstacle = false;
	boolean isHole = false;
	
	public State(String name) {
		super(name);
		this.color = Color.RED;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
		
		this.color = visited ? Color.YELLOW : Color.RED;
	}
	
	public void setObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
		
		this.color = isObstacle ? Color.BLACK : Color.RED;
		
		System.out.println("obstacle: " + isObstacle);
	}
	
	public void setHole(boolean isHole) {
		this.isHole = isHole;
		
		this.color = isHole ? Color.GREEN : Color.RED;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 20;
	}	
}
