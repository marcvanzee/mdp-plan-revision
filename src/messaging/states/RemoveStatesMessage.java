package messaging.states;

import java.util.List;

import model.mdp.Edge;
import model.mdp.State;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class RemoveStatesMessage extends ChangeVerticesMessage<State>
{
	public RemoveStatesMessage(State s) {
		super(s);
	}
	
	public RemoveStatesMessage(List<State> vertices) {
		super(vertices);
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g) 
	{
		for (State v : vertices) {
			g.removeVertex(v);
		}
	}
}
