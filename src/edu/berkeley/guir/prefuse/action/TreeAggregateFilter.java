package edu.berkeley.guir.prefuse.action;

import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Filter that adds aggregate items for elided subtrees.
 * 
 * Apr 30, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class TreeAggregateFilter extends AbstractAction {

	/**
	 * @see edu.berkeley.guir.prefuse.filter.AbstractPipelineComponent#process()
	 */
	public void run(ItemRegistry registry, double frac) {
		Tree t = (Tree)registry.getGraph();
		
		double sx, sy, ex, ey, stheta, etheta;
		
		Iterator nodeIter = registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			NodeItem nitem  = (NodeItem)nodeIter.next();
			TreeNode node   = (TreeNode)registry.getEntity(nitem);
            if ( nitem.getNumChildren() == 0 && node.getNumChildren() > 0 ) {				
				AggregateItem aggr = registry.getAggregateItem(node, true);
				Point2D       eloc = nitem.getEndLocation();
				Point2D       sloc = nitem.getStartLocation();

				aggr.setLocation   (sloc);
				aggr.updateLocation(eloc);
				aggr.setLocation   (eloc);
				
				setOrientation(aggr);
				
				aggr.setAggregateSize(node.getNumDescendants());
			}
		}
	} //
    
    protected void setOrientation(AggregateItem item) {
        Point2D eloc = item.getEndLocation();
        Point2D sloc = item.getStartLocation();
        Point2D anchor = null;
        
        double ax, ay, sx, sy, ex, ey, etheta, stheta;
        ax = anchor.getX(); ay = anchor.getY();
        sx = sloc.getX()-ax; sy = sloc.getY()-ay;
        ex = eloc.getX()-ax; ey = eloc.getY()-ay;
        
        etheta = Math.atan2(ey, ex);
        stheta = ( sx == 0 && sy == 0 ? etheta : Math.atan2(sy, sx) );
        
        item.setStartOrientation(stheta);
        item.setOrientation(etheta);
        item.setEndOrientation(etheta);
    } //

} // end of class TreeAggregateFilter
