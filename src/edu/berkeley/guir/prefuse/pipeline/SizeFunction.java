package edu.berkeley.guir.prefuse.pipeline;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Interface for setting the size attribute of GraphItems.
 * 
 * Apr 30, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface SizeFunction extends ProcessingComponent {

	/**
	 * Calculates the size value that should be used for the given GraphItem.
	 * @param item the item to set the size for
	 * @return the item's calculated size value
	 */
	public double getSize(GraphItem item);

} //
