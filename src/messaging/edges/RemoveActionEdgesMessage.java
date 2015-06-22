package messaging.edges;

import java.util.List;

import model.mdp.ActionEdge;
import model.mdp.Edge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class RemoveActionEdgesMessage extends ChangeEdgesMessage<ActionEdge>
{
	public RemoveActionEdgesMessage(ActionEdge e) {
		super(e);
	}
	
	public RemoveActionEdgesMessage(List<ActionEdge> es) {
		super(es);
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (ActionEdge e : edges) {
			g.removeEdge(e);
		}
	}
}