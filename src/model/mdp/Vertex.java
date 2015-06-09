package model.mdp;

import java.awt.Color;
import java.util.ArrayList;

public class Vertex<E> 
{
	String name;
	ArrayList<E> edges = new ArrayList<E>();
	Color color = Color.BLACK;
	
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
	
	public void setColor(Color newColor) {
		this.color = newColor;
	}
}
