package messaging.states;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.QState;
import mdps.elements.Vertex;

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
