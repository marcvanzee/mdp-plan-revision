package messaging.jung.edges;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.ActionEdge;
import mdp.elements.Edge;
import mdp.elements.Vertex;

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