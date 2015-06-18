package messaging.edges;

import java.util.LinkedList;

import messaging.ChangeMessage;
import model.mdp.Edge;
import model.mdp.QEdge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class RemoveQEdgesMessage implements ChangeMessage
{
	LinkedList<QEdge> edges = new LinkedList<QEdge>();
	
	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (QEdge e : edges) {
			g.removeEdge(e);
		}
	}
	
	public void removeEdges(LinkedList<QEdge> edges) {
		edges.addAll(edges);
	}
}