package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Font;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Simple <code>FontFunction</code> that blindly returns a null 
 * <code>Font</code> for all items. Subclasses should override the 
 * <code>getFont()</code> method to provide custom Font assignment
 * for GraphItems.
 * 
 * Jul 10, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class DefaultFontFunction extends AbstractPipelineComponent
	implements FontFunction
{

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		Iterator itemIter = m_registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			Font font = getFont(item);
			item.setFont(font);
		}
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.SizeFunction#getSize(edu.berkeley.guir.prefuse.GraphItem)
	 */
	public Font getFont(GraphItem item) {
		return null;
	} //

} // end of class DefaultFontFunction
