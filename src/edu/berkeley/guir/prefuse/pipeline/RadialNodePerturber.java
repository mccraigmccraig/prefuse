package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Naive class that attempts to perturb node positions in a radial layout
 * to improve readability.
 * 
 * Jun 10, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class RadialNodePerturber extends AbstractPipelineComponent
	implements ProcessingComponent
{

	private int m_numIter = 10;
	private int m_perturb = 5;

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#process()
	 */
	public void process() {
		Point2D anchor = (Point2D)m_pipeline.getAttribute(RadialTreeLayout.ATTR_ANCHOR);
				
		for ( int i = 0; i < m_numIter; i++ ) {		
			Iterator nodeIter = m_registry.getNodeItems();
			while ( nodeIter.hasNext() ) {
				NodeItem item   = (NodeItem)nodeIter.next();
				TreeNode node   = (TreeNode)item.getEntity();
				TreeNode parent = node.getParent();
				
				if ( parent == null ) continue;
				
				NodeItem sib1   = getSiblingItem(node,true);
				NodeItem sib2   = getSiblingItem(node,false);
				if ( sib1 == null && sib2 == null ) continue;
				
				Point2D loc = item.getLocation();
				double theta = Math.atan2(loc.getY()-anchor.getY(),loc.getX()-anchor.getX());
				theta = (parent.getChildIndex(node) % 2 == 0 ? theta : theta + Math.PI); 
				
				Rectangle rn = item.getBounds();
				if ( (sib1 != null && sib1.getBounds().intersects(rn)) ||
				     (sib2 != null && sib2.getBounds().intersects(rn)) ) 
				 {									
					perturb(item, m_perturb, theta);
					continue;
				}				
			}
		}		
	} //
	
	protected NodeItem getSiblingItem(TreeNode node, boolean left) {
		TreeNode parent = node.getParent();
		if ( parent == null ) return null;
		int idx = parent.getChildIndex(node);
		int cc = parent.getNumChildren();
		while ( (left && --idx > 0) || (!left && ++idx < cc) ) {
			TreeNode s  = parent.getChild(idx);
			NodeItem si	= m_registry.getNodeItem(s);
			if ( si != null && si.isVisible() ) {
				return si;
			}
		}					
		return null;
	} //

	protected void perturb(NodeItem item, int amount, double direction) {
		double x = item.getX(), y = item.getY();
		x += amount * Math.cos(direction);
		y += amount * Math.sin(direction);
		item.setLocation(x,y);
		item.setEndLocation(x,y);
	} //

} // end of class RadialNodePerturber
