package messaging.jung;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.Edge;
import mdp.elements.Vertex;


/**
 * @author marc.vanzee
 *
 */
public interface ChangeMessage 
{
	public abstract void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g);
}
