package edu.berkeley.guir.prefusex.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.TreeLayout;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Performs tree layout using a radial layout.
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class RadialTreeLayout extends TreeLayout implements FocusListener {
    
    public static final int DEFAULT_RADIUS = 50;
    protected static final double TWO_PI = 2*Math.PI;
    

    protected int m_maxDepth = 0;
    protected double m_radiusInc;
    protected double m_startTheta, m_endTheta;
    protected boolean m_setTheta = false;
    protected boolean m_autoScale = true;
    
    protected Point2D m_origin;
    protected TreeNode m_prevParent;
    protected ItemRegistry m_registry;
    
    public RadialTreeLayout() {
        this(DEFAULT_RADIUS);
    } //
    
    public RadialTreeLayout(int radius) {
        m_radiusInc  = radius;
        m_prevParent = null;
        m_startTheta = 0;
        m_endTheta = TWO_PI;
    } //

    public double getRadiusIncrement() {
        return m_radiusInc;
    } //
    
    public void setRadiusIncrement(double inc) {
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

    public void run(ItemRegistry registry, double frac) {
        if ( m_registry != registry ) {
            if ( m_registry != null )
                m_registry.getDefaultFocusSet().removeFocusListener(this);
            m_registry = registry;
            m_registry.getDefaultFocusSet().addFocusListener(this);
        }
        
        m_origin = getAnchor(registry);
        NodeItem n = getLayoutRoot(registry);
        RadialParams np = getParams(n);
        
        // count maximum tree depth and number of tree leaves
        m_maxDepth = 0;
        countVisibleDescendants(n, 0);
        
        if ( m_autoScale ) setScale(getBounds(registry));
        if ( !m_setTheta && m_prevParent != null ) {
            NodeItem p = registry.getNodeItem(m_prevParent);
            m_startTheta = calcStartingTheta(n, p);
            m_endTheta = m_startTheta + TWO_PI;
        }                           
        setLocation(n, m_origin.getX(), m_origin.getY());
        np.angle = m_endTheta-m_startTheta;
        layout(n, m_radiusInc, m_startTheta, m_endTheta);
        m_prevParent = null;
    } //
    
    protected void setScale(Rectangle2D bounds) {
        double r = Math.min(bounds.getWidth(),bounds.getHeight())/2.0;
        if ( m_maxDepth > 0 )
            m_radiusInc = (r-40)/m_maxDepth;
    } //

    private double calcStartingTheta(NodeItem n, NodeItem p) {
        if ( p == null ) { return 0; }
        
        Point2D ploc = p.getLocation();
        Point2D nloc = n.getLocation();
        
        double ptheta = Math.atan2(ploc.getY()-nloc.getY(), ploc.getX()-nloc.getX());
        
        int pidx = n.getChildIndex(p);
        int nD = getParams(n).numDescendants;
        int pD = getParams(p).numDescendants;
        int cD = 0;
        for ( int i = 0; i < pidx; i++ ) {
            cD += getParams(n.getChild(i)).numDescendants;
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
        if ( n.getNumChildren() > 0 ) {
            Iterator childIter = n.getChildren();
            while ( childIter.hasNext() ) {
                NodeItem c = (NodeItem)childIter.next();
                count += countVisibleDescendants(c,d+1);
            }
        } else {
            count = 1;
        }
        getParams(n).numDescendants = count;
        return count;
    } //
    
    /**
     * Compute the layout.
     * @param n
     * @param r
     * @param theta1
     * @param theta2
     */
    protected void layout(NodeItem n, double r, double theta1, double theta2) {
        int numDescendants = getParams(n).numDescendants;
        if ( numDescendants == 0 )
            return; // nothing to do, so exit

        double dtheta  = (theta2-theta1);
        double dtheta2 = dtheta / 2.0;

        double frac, f = 0.0;

        Iterator childIter = n.getChildren();
        while ( childIter.hasNext() ) {
            NodeItem c = (NodeItem)childIter.next();
            RadialParams cp = getParams(c);
            frac = ((double)cp.numDescendants)/numDescendants;
            setPolarLocation(c, r, theta1 + f*dtheta + frac*dtheta2);
            cp.angle = frac*dtheta;
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
    protected void setPolarLocation(NodeItem n, double r, double theta) {
        double x = m_origin.getX() + r*Math.cos(theta);
        double y = m_origin.getY() + r*Math.sin(theta);
        n.updateLocation(x,y);
        n.setLocation(x,y);
    } //
    
    public void focusChanged(FocusEvent e) {
        if ( e.getEventType() != FocusEvent.FOCUS_SET )
            return;
        Entity focus, fprev;
        if ( (focus=e.getFirstAdded()) instanceof TreeNode && 
             (fprev=e.getFirstRemoved()) instanceof TreeNode ) {
            TreeNode prev = (TreeNode)fprev;
            for ( ; prev != null && prev.getParent() != focus; prev = prev.getParent() );
            m_prevParent = prev;
        }
    } //
    
    private RadialParams getParams(GraphItem item) {
        RadialParams rp = (RadialParams)item.getVizAttribute("radialParams");
        if ( rp == null ) {
            rp = new RadialParams();
            item.setVizAttribute("radialParams", rp);
        }
        return rp;
    } //
    
    public class RadialParams {
        int numDescendants;
        double angle;
    } //

} // end of class RadialTreeLayout
