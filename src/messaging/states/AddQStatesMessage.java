package messaging.states;

import java.util.List;

import model.mdp.Edge;
import model.mdp.QState;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class AddQStatesMessage extends ChangeVerticesMessage<QState>
{
	public AddQStatesMessage(List<QState> vertices) {
		super(vertices);
	}
	
	public AddQStatesMessage(QState v) {
		super(v);
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g) 
	{
		for (QState v : vertices) {
			g.addVertex(v);
		}
	}
}
