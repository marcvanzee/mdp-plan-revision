package mdps.elements;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import constants.MathOperations;
import settings.TileworldSettings;

public class State extends Vertex<ActionEdge> 
{
	final Point coord = new Point();
	
	boolean isVisited = false;
	boolean isObstacle = false;
	boolean isHole = false;
	int lifeTime;
	double value;
	
	
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
			
		this.lifeTime = MathOperations.getRandomInt(
				TileworldSettings.HOLE_LIFE_EXP_MIN, TileworldSettings.HOLE_LIFE_EXP_MAX);
		
		setColor();
	}
	
	public void setValue(double d) {
		this.value = d;
	}
	
	public void setCoord(int x, int y) {
		coord.setLocation(x, y);
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
	
	public int getX() {
		return coord.x;
	}
	
	public int getY() {
		return coord.y;
	}
}
