package edu.berkeley.guir.prefuse.pipeline;

import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.NodeItem;
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
public class TreeAggregateFilter extends AbstractPipelineComponent
	implements Filter
{

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		Tree t = (Tree)m_graph;
		
		int minDOI = getIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI);
		if ( minDOI == Integer.MIN_VALUE ) {
			throw new IllegalStateException("Minimum DOI is not set!");
		}
		
		Point2D anchor = (Point2D)getAttribute(RadialTreeLayout.ATTR_ANCHOR);
		if ( anchor == null ) {
			throw new IllegalStateException("Anchor point not set!");
		}
		
		double ax, ay, sx, sy, ex, ey, stheta, etheta;
		ax = anchor.getX(); ay = anchor.getY();
		
		Iterator nodeIter = m_registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			NodeItem nitem  = (NodeItem)nodeIter.next();
			TreeNode node   = (TreeNode)m_registry.getEntity(nitem);
			if ( node.getNumChildren() > 0 && (int)nitem.getDOI() == minDOI ) {				
				AggregateItem aggr = m_registry.getAggregateItem(node, true);
				Point2D       eloc = nitem.getEndLocation();
				Point2D       sloc = nitem.getStartLocation();
				
				sx = sloc.getX(); sy = sloc.getY();
				ex = eloc.getX(); ey = eloc.getY();
				
				aggr.setLocation   (sx, sy);
				aggr.updateLocation(ex, ey);
				aggr.setLocation   (ex, ey);
				
				sx -= ax; sy -= ay;
				ex -= ax; ey -= ay;
				
				etheta = Math.atan2(ey, ex);
				stheta = ( sx == 0 && sy == 0 ? etheta : Math.atan2(sy, sx) );
								
				aggr.setStartOrientation(stheta);
				aggr.setOrientation(etheta);
				aggr.setEndOrientation(etheta);
				
				aggr.setAggregateSize(node.getNumDescendants());
				
				/// XXX DEBUG
				//System.out.println("nitem (x,y)="+eloc.getX()+","+eloc.getY());
			}
		}
	} //

} // end of class TreeAggregateFilter
