package messaging.states;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.ActionEdge;
import mdps.elements.Edge;
import mdps.elements.State;
import mdps.elements.Vertex;

public class AddStatesMessage extends ChangeVerticesMessage<State>
{
	public AddStatesMessage(List<State> vertices) {
		super(vertices);
	}
	
	public AddStatesMessage(State v) {
		super(v);
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g) 
	{
		for (Vertex<ActionEdge> v : vertices) {
			g.addVertex(v);
		}
	}
}
