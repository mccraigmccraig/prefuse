package edu.berkeley.guir.prefuse.action;

import java.awt.Font;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;

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
public class FontFunction extends AbstractAction {

	public void run(ItemRegistry registry, double frac) {
		Iterator itemIter = registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			Font font = getFont(item);
			item.setFont(font);
		}
	} //
	
	public Font getFont(GraphItem item) {
		return null;
	} //

} // end of class FontFunction
