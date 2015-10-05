package messaging.jung.edges;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.Edge;
import mdp.elements.QEdge;
import mdp.elements.Vertex;

public class RemoveQEdgesMessage extends ChangeEdgesMessage<QEdge>
{
	public RemoveQEdgesMessage(QEdge e) {
		super(e);
	}
	
	public RemoveQEdgesMessage(List<QEdge> es) {
		super(es);
	}
	
	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (QEdge e : edges) {
			g.removeEdge(e);
		}
	}
}