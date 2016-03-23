package messaging.jung.edges;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.Edge;
import mdp.elements.QEdge;
import mdp.elements.Vertex;

public class AddQEdgesMessage extends ChangeEdgesMessage<QEdge>
{
	public AddQEdgesMessage(QEdge e) {
		super(e);
	}
	
	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (QEdge e : edges) {
			g.addEdge(e, e.getFromVertex(), e.getToVertex());
		}
	}
}