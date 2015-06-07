package model;

import java.util.ArrayList;

public class Vertex<E> 
{
	String name;
	ArrayList<E> edges = new ArrayList<E>();
	
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
}
