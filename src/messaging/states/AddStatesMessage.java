package messaging.states;

import java.util.List;

import model.mdp.ActionEdge;
import model.mdp.Edge;
import model.mdp.State;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

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
