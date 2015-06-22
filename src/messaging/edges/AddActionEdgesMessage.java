package messaging.edges;

import model.mdp.ActionEdge;
import model.mdp.Edge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

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