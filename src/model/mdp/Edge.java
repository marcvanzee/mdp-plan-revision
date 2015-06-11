package model.mdp;


public class Edge<S,T> {
	S from;
	T to;
	boolean optimal = false; // whether this edge is optimal (best action or most probable) 
	
	public Edge() {		
	}

	
	public Edge(S from, T to) {
		this.from = from;
		this.to = to;
	}
	
	public S getFromVertex() {
		return from;
	}
	
	public T getToVertex() {
		return to;
	}	
	
	public void setOptimal(boolean newOptimal) {
		this.optimal = newOptimal;
	}
	
	public boolean isOptimal() {
		return optimal;
	}
}
