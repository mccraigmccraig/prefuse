package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Factory interface from which to retrieve GraphItem renderers.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface RendererFactory {

	/**
	 * Return the appropriate renderer to draw the given GraphItem.
	 * @param item the item for which to retrieve the renderer
	 * @return the Renderer for the given GraphItem
	 */
	public Renderer getRenderer(GraphItem item);

} // end of interface RendererFactory
