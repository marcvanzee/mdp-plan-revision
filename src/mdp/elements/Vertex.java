package mdp.elements;

import java.awt.Color;
import java.util.ArrayList;

public abstract class Vertex<E> 
{
	protected String name = "";
	protected ArrayList<E> edges = new ArrayList<E>();
	protected Color color = Color.BLACK;
	int indegree = 0, outdegree = 0;
	
	public Vertex() {}
	
	public abstract int getSize();
	
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
	
	public String toString() {
		return name; 
	}
	
	public void incIndegree() {
		this.indegree++;
	}
	
	public void decIndegree() {
		this.indegree--;
	}
	
	public int getIndegree() {
		return this.indegree;
	}
	
	public void incOutdegree() {
		this.outdegree++;
	}
	
	public void decOutdegree() {
		this.outdegree--;
	}
	
	public int getOutdegree() {
		return this.outdegree;
	}
	
	
}
