package messaging.states;

import java.util.List;

import model.mdp.Edge;
import model.mdp.QState;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class RemoveQStatesMessage extends ChangeVerticesMessage<QState>
{
	public RemoveQStatesMessage(List<QState> vertices) {
		super(vertices);
	}
	
	public RemoveQStatesMessage(QState qState) {
		super(qState);
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g) 
	{
		for (QState v : vertices) {
			g.removeVertex(v);
		}
	}
}
