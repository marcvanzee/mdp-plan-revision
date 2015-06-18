package messaging.edges;

import model.mdp.Edge;
import model.mdp.QState;
import model.mdp.State;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class RemoveActionEdgesMessage extends ChangeEdgesMessage<State, QState>
{
	public RemoveActionEdgesMessage(Edge<State, QState> e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (Edge<State,QState> e : edges) {
			g.removeEdge(e);
		}
	}
}