package messaging.jung;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import mdp.elements.Edge;
import mdp.elements.Vertex;

/**
 * A complex change is simply a list of changes to the graph that have to be carried out in order.
 * 
 * @author marc.vanzee
 *
 */
public class ChangeMessageBuffer implements Iterable<ChangeMessage>, ChangeMessage
{
	private final List<ChangeMessage> changes = new LinkedList<ChangeMessage>();
	
	@Override
	public Iterator<ChangeMessage> iterator() {
		return changes.iterator();
	}

	@Override
	public void modifyGraph(Graph<Vertex<?>, Edge<?, ?>> g) {
		for (ChangeMessage c : changes) {
			c.modifyGraph(g);
		}
	}
	
	public void addMessage(ChangeMessage cm) {
		changes.add(cm);
	}
	
	public void clear() {
		changes.clear();
	}
	
	public List<ChangeMessage> getChanges() {
		return changes;
	}
	
}
