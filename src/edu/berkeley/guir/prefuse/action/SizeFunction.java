package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * Simple SizeFunction that blindly returns a size of "1" for all
 * items. Subclasses should override the getSize() method to provide
 * custom size assignment for GraphItems.
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class SizeFunction extends AbstractAction {

	public void run(ItemRegistry registry, double frac) {
		Iterator itemIter = registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			double size = getSize(item);
			item.updateSize(size);
			item.setSize(size);
		}
	} //
	
	public double getSize(GraphItem item) {
		return 1;
	} //

} // end of class SizeFunction
