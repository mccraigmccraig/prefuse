package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.Pipeline;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Performs tree layout using a radial layout.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class RadialTreeLayout extends AbstractPipelineComponent 
	implements Layout, FocusListener
{

	public static final String ATTR_ANCHOR = "anchor";

	protected static final double TWO_PI = 2*Math.PI;
	protected int m_startRadius = 0;
	protected int m_radiusInc = 50;
	protected int m_maxDepth = 0;
	protected Point2D m_anchor;
	protected HashMap m_counts, m_width;
	protected TreeNode m_prevParent;
	protected double m_startTheta, m_endTheta;
	protected boolean m_setTheta = false;
	protected boolean m_autoScale = true;

	public RadialTreeLayout() {
		try {
			m_anchor     = (Point2D)GraphItem.POINT_TYPE.newInstance();
			m_counts     = new HashMap();
			m_width      = new HashMap();
			m_prevParent = null;
			m_startTheta = 0;
			m_endTheta = TWO_PI;	
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //

	public void init(Pipeline pipeline) {
		super.init(pipeline);
		pipeline.getItemRegistry().addFocusListener(this);
	} //
	
	public void reset() {
		m_pipeline.getItemRegistry().removeFocusListener(this);
		m_prevParent = null;
		super.reset();
	} //

	public void setStartRadius(int r) {
		m_startRadius = r;
	} //

	public int getRadiusIncrement() {
		return m_radiusInc;
	} //
	
	public void setRadiusIncrement(int inc) {
		m_radiusInc = inc;
	} //

	public boolean getAutoScale() {
		return m_autoScale;
	} //
	
	public void setAutoScale(boolean s) {
		m_autoScale = s;
	} //

	public void setStartTheta(double t) {
		m_startTheta = t;
		m_setTheta = true;
	} //
	
	public void setEndTheta(double t) {
		m_endTheta = t;
		m_setTheta = true;
	} //

	public double getAngularWidth(NodeItem n) {
		Double w = (Double)m_width.get(n);
		return ( w == null ? -1 : w.doubleValue() );
	} //
	
	private void setAngularWidth(NodeItem n, double w) {
		//System.out.println("setting angw: " + n.getAttribute("FullName") + " = " + w);
		m_width.put(n, new Double(w));
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.layout.AbstractTreeLayout#layout(edu.berkeley.guir.prefuse.graph.Tree, edu.berkeley.guir.prefuse.ItemRegistry)
	 */
	public void process() {
		m_counts.clear();
		m_width.clear();
		m_maxDepth = 0;
		
		Tree t = (Tree)m_graph;
		setAnchor();
		TreeNode root = t.getRoot();
		if ( m_registry.isVisible(root) ) {
            NodeItem r = m_registry.getNodeItem(root);
			countVisibleDescendants(r, 0);
			if ( m_autoScale ) setScale();
			if ( !m_setTheta && m_prevParent != null ) {
                NodeItem p = m_registry.getNodeItem(m_prevParent);
				m_startTheta = calcStartingTheta(r, p);
				m_endTheta = m_startTheta + TWO_PI;
			}							
			setLocation(r, m_anchor.getX(), m_startRadius+m_anchor.getY());
			setAngularWidth(r,m_endTheta-m_startTheta);
			layout(r, m_startRadius+m_radiusInc, m_startTheta, m_endTheta);
			m_prevParent = null;
		} else {
			System.err.println("RadialTreeLayout: Tree root not visible!");
		}
	} //
	
	/**
	 * Compute the co-ordinates of the center of the display.
	 */
	protected void setAnchor() {
		Dimension d = m_display.getSize();
		m_anchor.setLocation(d.width/2, d.height/2);
		setAttribute(ATTR_ANCHOR, m_anchor);
	} //
	
	protected void setScale() {
		Dimension d = m_display.getSize();
		int r = Math.min(d.width,d.height)/2;
		if ( m_maxDepth > 0 )
			m_radiusInc = (r-40)/m_maxDepth;
	} //

	private double calcStartingTheta(NodeItem n, NodeItem p) {
		if ( p == null ) { return 0; }
		
		Point2D ploc = p.getLocation();
		Point2D nloc = n.getLocation();
		
		double ptheta = Math.atan2(ploc.getY()-nloc.getY(), ploc.getX()-nloc.getX());
		
		int pidx = n.getChildIndex(p);
		int nD = getVisibleDescendants(n);
		int pD = getVisibleDescendants(p);
		int cD = 0;
		for ( int i = 0; i < pidx; i++ ) {
			cD += getVisibleDescendants(n.getChild(i));
		}
		double f = (cD + ((double)pD) / 2.0) / ((double)nD);	
		double theta = ptheta - f*TWO_PI;
		
		return theta;		
	} //

	/**
	 * Computes the number of visible descendant leaf nodes for each visible
	 * node.
	 */
	private int countVisibleDescendants(NodeItem n, int d) {
		if ( d > m_maxDepth ) m_maxDepth = d;		
		int count = 0;
		Iterator childIter = n.getChildren();
		while ( childIter.hasNext() ) {
			NodeItem c = (NodeItem)childIter.next();
			count += countVisibleDescendants(c,d+1);
		}
		if ( count == 0 )
			count = 1;
		setVisibleDescendants(n, count);
		return count;
	} //

	/**
	 * Store the visible descendant count for a node.
	 * @param n
	 * @param count
	 */
	private void setVisibleDescendants(NodeItem n, int count) {
		m_counts.put(n,new Integer(count));
	} //
	
	/**
	 * Retrieve the visible descendant count for a node.b
	 * @param n
	 * @return int
	 */
	private int getVisibleDescendants(NodeItem n) {
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
	protected void layout(NodeItem n, int r, double theta1, double theta2) {
		int numDescendants = getVisibleDescendants(n), i = 0;

		if ( numDescendants == 0 ) {
			return;
		}

		double dtheta  = (theta2-theta1);
		double dtheta2 = dtheta / 2.0;

		double frac, f = 0.0;

		Iterator childIter = n.getChildren();
		while ( childIter.hasNext() ) {
			NodeItem c = (NodeItem)childIter.next();			
			frac = ((double)getVisibleDescendants(c))/numDescendants;
			setPolarLocation(c, r, theta1 + f*dtheta + frac*dtheta2);
			setAngularWidth(c,frac*dtheta);
			layout(c, r+m_radiusInc, theta1 + f*dtheta, theta1 + (f+frac)*dtheta);
			f += frac;
		}
	} //

	/**
	 * Set the (x,y) co-ordinates of the given node
	 * @param n
	 * @param x
	 * @param y
	 */
	protected void setLocation(NodeItem n, double x, double y) {
		n.updateLocation(x,y);
		n.setLocation(x,y);
	} //

	/**
	 * Set the position of the given node, given in polar co-ordinates.
	 * @param n
	 * @param r
	 * @param theta
	 */
	protected void setPolarLocation(NodeItem n, int r, double theta) {
		double x = Math.round(m_anchor.getX() + r*Math.cos(theta));
		double y = Math.round(m_anchor.getY() + r*Math.sin(theta));
        n.updateLocation(x,y);
        n.setLocation(x,y);
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.event.FocusListener#focusChanged(edu.berkeley.guir.prefuse.event.FocusEvent)
	 */
	public void focusChanged(FocusEvent e) {
		if ( e.getType() == FocusEvent.FOCUS_SET ) {
			TreeNode focus = (TreeNode)e.getFocus();
			TreeNode prev  = (TreeNode)e.getPreviousFocus();
			for ( ; prev != null && prev.getParent() != focus; prev = prev.getParent() );
			m_prevParent = prev;
		}
	} //

} // end of class RadialTreeLayout
