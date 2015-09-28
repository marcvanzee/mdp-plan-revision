package messaging.jung.edges;

import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.Vertex;
import messaging.jung.ChangeMessage;

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
