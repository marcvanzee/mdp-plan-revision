package messaging.edges;

import model.mdp.Edge;
import model.mdp.QState;
import model.mdp.State;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class AddQEdgesMessage extends ChangeEdgesMessage<QState, State>
{
	public AddQEdgesMessage(Edge<QState, State> e) {
		super(e);
	}
	
	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (Edge<QState,State> e : edges) {
			g.addEdge(e, e.getFromVertex(), e.getToVertex());
		}
	}
}