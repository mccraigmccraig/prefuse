/*
 * Created on Aug 4, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.action.Action;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Sets the current x coordinate bounds of the item; meant to be used after
 * arranging all the items in a particular layout.
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class SetBoundsFunction extends AbstractAction implements Action, TimelineConstants {
	// (( METHODS )) \\
	/* (non-Javadoc)
	 * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
	 */
	public void run(ItemRegistry registry, double frac) {
        final Iterator nodeIter = registry.getGraph().getNodes();
        while ( nodeIter.hasNext() ) {
            final NodeItem item = registry.getNodeItem((Node)nodeIter.next());//, true);
            final Rectangle2D bounds = item.getBounds();
			item.setAttribute(LEFT_NORMAL, ""+bounds.getMinX());
            item.setAttribute(RIGHT_NORMAL, ""+bounds.getMaxX());
        }
	}
}
