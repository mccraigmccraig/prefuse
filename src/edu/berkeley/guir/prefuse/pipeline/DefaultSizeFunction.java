package edu.berkeley.guir.prefuse.pipeline;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Simple SizeFunction that blindly returns a size of "0" for all
 * items. Subclasses should override the getSize() method to provide
 * custom size assignment for GraphItems.
 * 
 * Apr 30, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class DefaultSizeFunction extends AbstractPipelineComponent
	implements SizeFunction
{

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		Iterator itemIter = m_registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			double size = getSize(item);
			item.updateSize(size);
			item.setSize(size);
		}
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.SizeFunction#getSize(edu.berkeley.guir.prefuse.GraphItem)
	 */
	public double getSize(GraphItem item) {
		return 0;
	} //

} // end of class DefaultSizeFunction
