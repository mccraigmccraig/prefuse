package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

/**
 * Inteface for classes to monitor graph item focus events.
 * 
 * Apr 26, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface FocusListener extends EventListener {

	/**
	 * Callback to inform listeners of changes to the set of
	 * selected focus entities in the visualization.
	 * @param e a FocusEvent describing the focus change.
	 */
	public void focusChanged(FocusEvent e);

} // end of interface FocusListener
