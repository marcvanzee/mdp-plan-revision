package messaging.edges;

import java.util.LinkedList;
import java.util.List;

import messaging.ChangeMessage;
import model.mdp.Edge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public abstract class ChangeEdgesMessage<E> implements ChangeMessage 
{
	List<E> edges = new LinkedList<E>();
	
	public abstract void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g);
	
	public ChangeEdgesMessage(List<E> edges) {
		this.edges.addAll(edges);
	}
	
	public ChangeEdgesMessage(E e) {
		this.edges.add(e);
	}
}
