package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * The RendererFactory is responsible for providing the proper Renderer
 * instance for drawing a given GraphItem.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public interface RendererFactory {

	/**
	 * Return the appropriate renderer to draw the given GraphItem.
	 * @param item the item for which to retrieve the renderer
	 * @return the Renderer for the given GraphItem
	 */
	public Renderer getRenderer(GraphItem item);

} // end of interface RendererFactory
