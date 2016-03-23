package messaging.jung.edges;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.ActionEdge;
import mdp.elements.Edge;
import mdp.elements.Vertex;

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