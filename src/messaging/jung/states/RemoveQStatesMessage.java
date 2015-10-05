package messaging.jung.states;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.Edge;
import mdp.elements.QState;
import mdp.elements.Vertex;

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
