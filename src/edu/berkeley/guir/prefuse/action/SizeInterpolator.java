package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * Linearly interpolates the size of a GraphItem.
 * 
 * Apr 27, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class SizeInterpolator extends AbstractAction {

	public static final String ATTR_ANIM_FRAC = "animationFrac";

	/**
	 * @see edu.berkeley.guir.prefuse.filter.AbstractPipelineComponent#process()
	 */
	public void run(ItemRegistry registry, double frac) {
		double ss, es, s;		
		
		Iterator itemIter = registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			ss = item.getStartSize();
			es = item.getEndSize();						
			s = ss + frac * (es - ss);						
			item.setSize(s);
		}		
	} //

} // end of class SizeInterpolator
