package edu.berkeley.guir.prefuse.collections;

import java.util.Comparator;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.NodeItem;

/**
 * Basic comparator that treats GraphItems in the following manner:
 *   NodeItem > EdgeItem > AggregateItem
 * All items of the same type are considered equal.
 * 
 * Jun 2, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class DOIItemComparator implements Comparator {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if ( !(o1 instanceof GraphItem && o2 instanceof GraphItem) ) {
			throw new IllegalArgumentException();
		}
		
		GraphItem item1 = (GraphItem)o1;
		GraphItem item2 = (GraphItem)o2;
		
		if ( item1 instanceof NodeItem ) {
			if ( item2 instanceof NodeItem ) {
				double doi1 = ((NodeItem)item1).getDOI();
				double doi2 = ((NodeItem)item2).getDOI();				
				return ( doi1 > doi2 ? 1 : ( doi1 == doi2 ? 0 : -1 ) );				
			} else {
				return 1;
			}
		} else if ( item2 instanceof NodeItem ) {
			return -1;
		} else if ( item1 instanceof EdgeItem ) {
			if ( item2 instanceof EdgeItem ) {
				double doi1a = ((EdgeItem)item1).getFirstNode().getDOI();
				double doi2a = ((EdgeItem)item2).getFirstNode().getDOI();
				double doi1b = ((EdgeItem)item1).getSecondNode().getDOI();
				double doi2b = ((EdgeItem)item2).getSecondNode().getDOI();
				double doi1 = Math.max(doi1a, doi1b);
				double doi2 = Math.max(doi2a, doi2b);				
				return ( doi1 > doi2 ? 1 : ( doi1 == doi2 ? 0 : -1 ) );
			} else {
				return 1;
			}
		} else if ( item2 instanceof EdgeItem ) {
			return -1;
		} else if ( item1 instanceof AggregateItem ) {
			if ( item2 instanceof AggregateItem ) {
				double doi1 = ((AggregateItem)item1).getNodeItem().getDOI();
				double doi2 = ((AggregateItem)item2).getNodeItem().getDOI();				
				return ( doi1 > doi2 ? 1 : ( doi1 == doi2 ? 0 : -1 ) );
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
