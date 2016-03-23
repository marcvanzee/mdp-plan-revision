package messaging.jung.states;

import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.Edge;
import mdp.elements.Vertex;
import messaging.jung.ChangeMessage;

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
