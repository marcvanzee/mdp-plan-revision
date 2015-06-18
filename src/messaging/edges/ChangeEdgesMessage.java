package messaging.edges;

import java.util.LinkedList;
import java.util.List;

import messaging.ChangeMessage;
import model.mdp.Edge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public abstract class ChangeEdgesMessage<S,T> implements ChangeMessage 
{
	List<Edge<S,T>> edges = new LinkedList<Edge<S,T>>();
	
	public abstract void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g);
	
	public ChangeEdgesMessage(List<Edge<S,T>> edges) {
		this.edges.addAll(edges);
	}
	
	public ChangeEdgesMessage(Edge<S,T> e) {
		this.edges.add(e);
	}
}
