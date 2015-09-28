package messaging.edges;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.StateEdge;
import mdps.elements.Vertex;

// this message is actually not valid for an MDP, but it is used
// in the TileWorld so we can leave out the qstates in the visualisation

public class AddStateEdgesMessage extends ChangeEdgesMessage<StateEdge>
{
	public AddStateEdgesMessage(StateEdge e) {
		super(e);
	}
	
	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (StateEdge e : edges) {
			g.addEdge(e, e.getFromVertex(), e.getToVertex());
		}
	}
}
