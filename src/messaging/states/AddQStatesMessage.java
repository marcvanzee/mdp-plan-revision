package messaging.states;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.QState;
import mdps.elements.Vertex;

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
