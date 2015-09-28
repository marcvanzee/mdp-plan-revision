package messaging.edges;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.ActionEdge;
import mdps.elements.Edge;
import mdps.elements.Vertex;

public class AddActionEdgesMessage extends ChangeEdgesMessage<ActionEdge>
{
	public AddActionEdgesMessage(ActionEdge e) {
		super(e);
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (ActionEdge e : edges) {
			g.addEdge(e, e.getFromVertex(), e.getToVertex());
		}
	}
}