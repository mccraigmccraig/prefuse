package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Performs tree layout using a vertical, top-down, layout.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class VerticalTreeLayout extends AbstractPipelineComponent 
	implements Layout
{

	public static final String ATTR_ANCHOR = "anchor";

	protected HashMap m_counts;
	protected Point2D m_anchor;
	protected int m_heightInc = 25;
	protected int m_bias = 15;

	/**
	 * Constructor.
	 */
	public VerticalTreeLayout() {
		try {
			m_anchor = new Point2D.Float();
			m_counts = new HashMap();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.layout.AbstractTreeLayout#layout(edu.berkeley.guir.prefuse.graph.Tree, edu.berkeley.guir.prefuse.ItemRegistry)
	 */
	public void process() {		
		Tree t = (Tree)m_graph;
		setAnchor();
		TreeNode root = t.getRoot();
		if ( m_registry.isVisible(root) ) {
			countVisibleDescendants(root);
			setLocation(root, m_anchor.getX(), m_anchor.getY());
			layout(root, (int)m_anchor.getY()+m_heightInc, m_bias, m_display.getSize().width-m_bias);
			m_counts.clear();			
		} else {
			System.err.println("VerticalTreeLayout: Tree root not visible!");
		}
	} //
	
	/**
	 * Compute the co-ordinates of the center of the display.
	 */
	protected void setAnchor() {
		Dimension d = m_display.getSize();
		m_anchor.setLocation(d.width/2, m_bias);
		setAttribute(ATTR_ANCHOR, m_anchor);
	} //

	/**
	 * Computes the number of visible descendant leaf nodes for each visible
	 * node.
	 */
	private int countVisibleDescendants(TreeNode n) {
		int count = 0;
		Iterator childIter = n.getChildren();
		while ( childIter.hasNext() ) {
			TreeNode c = (TreeNode)childIter.next();
			if ( m_registry.isVisible(c) ) {
				count += countVisibleDescendants(c);
			}
		}
		if ( count == 0 ) {
			count = 1;
		}
		setVisibleDescendants(n, count);
		return count;
	} //

	/**
	 * Store the visible descendant count for a node.
	 * @param n
	 * @param count
	 */
	private void setVisibleDescendants(TreeNode n, int count) {
		m_counts.put(n,new Integer(count));
	} //
	
	/**
	 * Retrieve the visible descendant count for a node.b
	 * @param n
	 * @return int
	 */
	private int getVisibleDescendants(TreeNode n) {
		Integer count = (Integer)m_counts.get(n);
		return ( count == null ? 0 : count.intValue() );
	} //
	
	/**
	 * Compute the layout.
	 * @param n
	 * @param r
	 * @param theta1
	 * @param theta2
	 */
	protected void layout(TreeNode n, int h, double x2, double x1) {
		int numDescendants = getVisibleDescendants(n), i = 0;

		if ( numDescendants == 0 ) {
			return;
		}

		double dx  = (x2-x1);
		double dx2 = dx / 2.0;

		double f = 0.0;

		Iterator childIter = n.getChildren();
		while ( childIter.hasNext() ) {
			TreeNode c = (TreeNode)childIter.next();
			double frac = ((double)getVisibleDescendants(c))/numDescendants;
			if ( m_registry.isVisible(c) ) {
				setLocation(c, x1 + f*dx + frac*dx2, h);
				layout(c, h+m_heightInc, x1 + f*dx, x1 + (f+frac)*dx);
				f += frac;
			}
		}
	} //

	/**
	 * Set the (x,y) co-ordinates of the given node
	 * @param n
	 * @param x
	 * @param y
	 */
	protected void setLocation(TreeNode n, double x, double y) {
		NodeItem nitem = m_registry.getNodeItem(n);
		nitem.updateLocation(x,y);
		nitem.setLocation(x,y);
	} //

} // end of class RadialTreeLayout
