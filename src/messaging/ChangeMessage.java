package messaging;

import model.mdp.Edge;
import model.mdp.Vertex;
import edu.uci.ics.jung.graph.Graph;


/**
 * @author marc.vanzee
 *
 */
public interface ChangeMessage 
{
	public abstract void modifyGraph(Graph<Vertex<?>,Edge<?,?>> g);
}
