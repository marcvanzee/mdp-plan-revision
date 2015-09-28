package model.mdp;

import java.awt.Color;
import java.util.Random;

import model.Settings;

public class State extends Vertex<ActionEdge> 
{
	boolean isVisited = false;
	boolean isObstacle = false;
	boolean isHole = false;
	int lifeTime;
	
	public State(String name) {
		super(name);
		this.color = Color.RED;
	}
	
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
		
		setColor();
	}
	
	public void setObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
		
		setColor();
	}
	
	public void setHole(boolean isHole) {
		this.isHole = isHole;
			
		Random r = new Random();
	
		int low = Settings.LIFE_EXPECTANCY - Settings.LIFE_EXPECTANCY_SD,
				high = Settings.LIFE_EXPECTANCY + Settings.LIFE_EXPECTANCY_SD;
		
		this.lifeTime = r.nextInt(high-low) + low;
		
		setColor();
	}
	
	private void setColor() {
		this.color = isVisited ? Color.YELLOW : 
			(isObstacle ? Color.BLACK :
				(isHole ? Color.GREEN : Color.RED) 
			);
	}
	
	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
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
