package messaging.jung;

import edu.uci.ics.jung.graph.Graph;
import mdps.elements.Edge;
import mdps.elements.Vertex;


/**
 * @author marc.vanzee
 *
 */
public interface ChangeMessage 
{
	public abstract void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g);
}
