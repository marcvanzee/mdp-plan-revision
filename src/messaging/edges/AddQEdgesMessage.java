package messaging.edges;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.QEdge;
import mdps.elements.Vertex;

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