package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Color;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Interface through which to set basic color properties for GraphItems.
 * 
 * Apr 30, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface ColorFunction extends ProcessingComponent {

	/**
	 * Returns the foreground color that should be used for the
	 * given GraphItem.
	 * @param item the item to provide the color for
	 * @return the foreground color for the item
	 */
	public Color getColor(GraphItem item);
	
	/**
	 * Returns the fill (background) color that should be used for the
	 * given GraphItem.
	 * @param item the item to provide the color for
	 * @return the fill (background) color for the item
	 */
	public Color getFillColor(GraphItem item);

} // end of interface ColorFunction
