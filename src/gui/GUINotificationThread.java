package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.GUIListener;
import main.TaskListener;

public abstract class GUINotificationThread implements Runnable {

	/**
	 * Our list of listeners to be notified upon thread completion.
	 */
	private List<GUIListener> listeners = Collections.synchronizedList( new ArrayList<GUIListener>() );

	/**
	 * Adds a listener to this object. 
	 * @param listener Adds a new listener to this object. 
	 */
	public void addListener( GUIListener listener ){
		listeners.add(listener);
	}

	/**
	 * Removes a particular listener from this object, or does nothing if the listener
	 * is not registered. 
	 * @param listener The listener to remove. 
	 */
	public void removeListener( TaskListener listener ){
		listeners.remove(listener);
	}

	/**
	 * Notifies all listeners that some event has occurred
	 */

	public final void notifyListeners(int event) {
		synchronized ( listeners ){
			for (GUIListener listener : listeners) {
			  listener.threadEvent(this, event);
			}
		}
	}
}