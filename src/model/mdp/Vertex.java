package model.mdp;

import java.awt.Color;
import java.util.ArrayList;

public abstract class Vertex<E> 
{
	protected String name;
	protected ArrayList<E> edges = new ArrayList<E>();
	protected Color color = Color.BLACK;
	
	public Vertex() {}
	
	public Vertex(String newName) {
		this.name = newName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean addEdge(E e) {
		if (e == null || edges.contains(e))
			return false;
		
		edges.add(e);
		return true;
	}
	
	public ArrayList<E> getEdges() {
		return edges;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public abstract int getSize();
}
