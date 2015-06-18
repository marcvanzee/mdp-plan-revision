package messaging.edges;

import model.mdp.Edge;
import model.mdp.QState;
import model.mdp.State;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class AddActionEdgesMessage extends ChangeEdgesMessage<State, QState>
{
	public AddActionEdgesMessage(Edge<State, QState> e) {
		super(e);
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (Edge<State,QState> e : edges) {
			g.addEdge(e, e.getFromVertex(), e.getToVertex());
		}
	}
}