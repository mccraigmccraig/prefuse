package edu.berkeley.guir.prefusex.distortion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.Layout;

/**
 * Abstract class providing skeletal implementation for space-distortion
 * techniques.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class Distortion extends Layout {

    Point2D m_tmp = new Point2D.Double();
    
    public void run(ItemRegistry registry, double frac) {
        Rectangle2D bounds = getLayoutBounds(registry);
        Point2D anchor = correct(getLayoutAnchor(), bounds);
        Iterator iter = registry.getItems();
        while ( iter.hasNext() ) {
            GraphItem item = (GraphItem)iter.next();
            if ( item.isFixed() ) continue;
            
            if ( anchor != null ) {
                transformPoint(item.getEndLocation(), 
                               item.getLocation(), 
                               anchor, bounds);
            } else {
                item.getLocation().setLocation(item.getEndLocation());
            }
        }
    } //
    
    private Point2D correct(Point2D anchor, Rectangle2D bounds) {
        if ( anchor == null ) return anchor;
        double x = anchor.getX(), y = anchor.getY();
        double x1 = bounds.getMinX(), y1 = bounds.getMinY();
        double x2 = bounds.getMaxX(), y2 = bounds.getMaxY();
        x = (x < x1 ? x1 : (x > x2 ? x2 : x));
        y = (y < y1 ? y1 : (y > y2 ? y2 : y));
        
        m_tmp.setLocation(x,y);
        return m_tmp;
    } //
    
    /**
     * Transforms the undistorted point <code>o</code> to the distorted point
     * <code>p</code>, subject to the given layout anchor (or focus) and
     * bounds.
     * @param o the original, undistorted point
     * @param p Point2D in which to store coordinates of the transformed point
     * @param anchor the anchor or focus point of the display
     * @param bounds the layout bounds
     */
    protected abstract void transformPoint(Point2D o, Point2D p, 
            Point2D anchor, Rectangle2D bounds);

} // end of abstract class Distortion
