package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Color;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Simple ColorFunction which blindly returns "black" when a color is
 * requested. Subclasses should override the getColor() and getFillColor()
 * methods to provide custom color selection functions.
 * 
 * Apr 30, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class DefaultColorFunction extends AbstractPipelineComponent 
	implements ColorFunction
{
	public void process() {
		Iterator itemIter = m_registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			Color c = getColor(item), o = item.getColor();
			if ( o == null ) item.setColor(getInitialColor(item));			
			item.updateColor(c);			
			item.setColor(c);
			
			c = getFillColor(item); o = item.getFillColor();
			if ( o == null ) item.setFillColor(getInitialFillColor(item));
			item.updateFillColor(c);			
			item.setFillColor(c);
		}
	} //

	protected Color getInitialColor(GraphItem item) {
		return getColor(item);
	} //
	
	protected Color getInitialFillColor(GraphItem item) {
		return getFillColor(item);
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.ColorFunction#getColor(edu.berkeley.guir.prefuse.GraphItem)
	 */
	public Color getColor(GraphItem item) {
		return Color.BLACK;
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.ColorFunction#getFillColor(edu.berkeley.guir.prefuse.GraphItem)
	 */
	public Color getFillColor(GraphItem item) {
		return Color.BLACK;
	} //

} // end of class DefaultColorFunction
