package edu.berkeley.guir.prefuse.collections;

import java.util.Comparator;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.NodeItem;

/**
 * Comparator that sorts items based on type and focus status.
 * 
 * Jul 9, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class DefaultItemComparator implements Comparator {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if ( !(o1 instanceof VisualItem && o2 instanceof VisualItem) ) {
			throw new IllegalArgumentException();
		}
		
		VisualItem item1 = (VisualItem)o1;
		VisualItem item2 = (VisualItem)o2;
        
		boolean f1 = item1.isFocus();
		boolean f2 = item2.isFocus();
		
		if ( item1 instanceof NodeItem ) {
			if ( item2 instanceof NodeItem ) {
				return ( f1 && !f2 ? 1 : (!f1 && f2 ? -1 : 0) );			
			} else {
				return 1;
			}
		} else if ( item2 instanceof NodeItem ) {
			return -1;
		} else if ( item1 instanceof EdgeItem ) {
			if ( item2 instanceof EdgeItem ) {
				return ( f1 && !f2 ? 1 : (!f1 && f2 ? -1 : 0) );
			} else {
				return 1;
			}
		} else if ( item2 instanceof EdgeItem ) {
			return -1;
		} else if ( item1 instanceof AggregateItem ) {
			if ( item2 instanceof AggregateItem ) {
				return ( f1 && !f2 ? 1 : (!f1 && f2 ? -1 : 0) );
			} else {
				return 1;
			}
		} else if ( item2 instanceof AggregateItem ) {
			return -1;
		} else {
			return 0;
		}		
	} //

} // end of class DefaultItemComparator
