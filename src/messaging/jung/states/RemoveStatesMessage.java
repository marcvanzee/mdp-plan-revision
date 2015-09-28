package messaging.jung.states;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.State;
import mdps.elements.Vertex;

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
