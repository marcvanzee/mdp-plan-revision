package messaging.jung;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.Edge;
import mdp.elements.Vertex;

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
