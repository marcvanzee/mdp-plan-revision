package messaging.jung.states;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.ActionEdge;
import mdp.elements.Edge;
import mdp.elements.State;
import mdp.elements.Vertex;

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
