package messaging.edges;

import java.util.List;

import model.mdp.Edge;
import model.mdp.QEdge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

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