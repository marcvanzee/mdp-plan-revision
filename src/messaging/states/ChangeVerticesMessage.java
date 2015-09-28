package messaging.states;

import java.util.LinkedList;
import java.util.List;

import messaging.ChangeMessage;
import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.Vertex;

public abstract class ChangeVerticesMessage<T> implements ChangeMessage 
{
	List<T> vertices = new LinkedList<T>();
	
	public abstract void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g);
	
	public ChangeVerticesMessage(List<T> vertices) {
		this.vertices.addAll(vertices);
	}
	
	public ChangeVerticesMessage(T v) {
		this.vertices.add(v);
	}
}
