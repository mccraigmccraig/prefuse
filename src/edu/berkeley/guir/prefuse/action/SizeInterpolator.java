package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * Linearly interpolates the size of a VisualItem.
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class SizeInterpolator extends AbstractAction {

	public static final String ATTR_ANIM_FRAC = "animationFrac";

	/**
	 * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
	 */
	public void run(ItemRegistry registry, double frac) {
		double ss, es, s;		
		
		Iterator itemIter = registry.getItems();
		while ( itemIter.hasNext() ) {
			VisualItem item = (VisualItem)itemIter.next();
			ss = item.getStartSize();
			es = item.getEndSize();						
			s = ss + frac * (es - ss);						
			item.setSize(s);
		}		
	} //

} // end of class SizeInterpolator
