package model;


public class Edge<S,T> {
	S from;
	T to;
	
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
}
