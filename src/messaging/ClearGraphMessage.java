package messaging;

import java.util.ArrayList;

import model.mdp.Edge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class ClearGraphMessage implements ChangeMessage {

	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) {
		if (g.getVertexCount() > 0)
    	{

    		ArrayList<Vertex<?>> vertices = new ArrayList<Vertex<?>>(g.getVertices());
    		for (Vertex<?> v : vertices) {
    			g.removeVertex(v);
    		}
    	}
    	if (g.getEdgeCount() > 0) 
    	{
    		ArrayList<Edge<?,?>> edges = new ArrayList<Edge<?,?>>(g.getEdges());
    		for (Edge<?,?> e : edges) {
    			g.removeEdge(e);
    		}
    	}
		
	}

}
