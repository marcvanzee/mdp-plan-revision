package model.mdp;

import java.awt.Color;
import java.util.Random;

import model.Settings;

public class State extends Vertex<ActionEdge> 
{
	boolean visited = false;
	boolean isObstacle = false;
	boolean isHole = false;
	int lifeTime;
	
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
				
		Random r = new Random();
	
		int low = Settings.LIFE_EXPECTANCY - Settings.LIFE_EXPECTANCY_SD,
				high = Settings.LIFE_EXPECTANCY + Settings.LIFE_EXPECTANCY_SD;
		
		this.lifeTime = r.nextInt(high-low) + low;
	}
		
	public void decreaseLifetime() {
		this.lifeTime--;
	}
	
	public int getLifetime() {
		return lifeTime;
	}
	
	public boolean isObstacle() {
		return isObstacle;
	}
	
	public boolean isHole() {
		return isHole;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return isHole? 40 : 20;
	}	
}
