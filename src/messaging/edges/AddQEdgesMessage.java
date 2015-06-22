package messaging.edges;

import model.mdp.Edge;
import model.mdp.QEdge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class AddQEdgesMessage extends ChangeEdgesMessage<QEdge>
{
	public AddQEdgesMessage(QEdge e) {
		super(e);
	}
	
	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) 
	{
		for (QEdge e : edges) {
			g.addEdge(e, e.getFromVertex(), e.getToVertex());
		}
	}
}