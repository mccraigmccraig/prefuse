package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Font;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Interface for setting the font attribute of GraphItems.
 * 
 * Jul 10, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface FontFunction extends ProcessingComponent {

	/**
	 * Calculates the size value that should be used for the given GraphItem.
	 * @param item the item to set the size for
	 * @return the item's calculated size value
	 */
	public Font getFont(GraphItem item);

} // end of interface FontFunction
