package edu.berkeley.guir.prefuse.action;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * 
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DistortionLayout extends Layout {

    Point2D m_tmp = new Point2D.Double();
    double a = 0.05;
    double b = 0.1;
    double d = 1.7;
    
    public void run(ItemRegistry registry, double frac) {
        Rectangle2D bounds = getLayoutBounds(registry);
        Point2D anchor = /*correct(*/getLayoutAnchor()/*, bounds)*/;
        Point2D origLoc;
        Iterator iter = registry.getItems();
        while ( iter.hasNext() ) {
            GraphItem item = (GraphItem)iter.next();
            if ( item.isFixed() ) continue;
            transformPoint(item.getEndLocation(), item.getLocation(), 
                           anchor, bounds);
        }
    } //
    
    private Point2D correct(Point2D anchor, Rectangle2D bounds) {
        if ( anchor == null ) return anchor;
        double x = anchor.getX(), y = anchor.getY();
        double w = bounds.getWidth(), h = bounds.getHeight();
        double bx = b*w+bounds.getX(), by = b*h+bounds.getY();
        x = (x<bx ? bx : (x>w-bx ? w-bx : x));
        y = (y<by ? by : (y>h-by ? h-by : y));
        m_tmp.setLocation(x,y);
        return m_tmp;
    } //
    
//    protected void transformPoint(Point2D o, Point2D p, 
//            Point2D anchor, Rectangle2D bounds)
//    {
//        if ( anchor == null ) {
//            p.setLocation(o);
//            return;
//        }
//        double x, y, v;
//        double w = bounds.getWidth(), h = bounds.getHeight();
//        double px = o.getX(), ax = anchor.getX();
//        v = px - ax;
//        if ( Math.abs(v) <= a*w ) {
//            x = v*b/a + ax; // in focus
//        } else {
//            // out of focus
//            x = (v<0?-1:1)*((Math.abs(v)-a*w)*((1-b)/(1-a)) + b*w) + ax;
//        }
//        
//        double py = o.getY(), ay = anchor.getY();
//        v = py - ay;
//        if ( Math.abs(v) <= a*h ) {
//            y = v*b/a + ay; // in focus
//        } else {
//            // out of focus
//            y = (v<0?-1:1)*((Math.abs(v)-a*h)*((1-b)/(1-a)) + b*h) + ay;
//        }
//        p.setLocation(x,y);
//    } //
    
    /**
     * Calculates a Cartesian graphical fisheye distortion
     */
    protected void transformPoint(Point2D o, Point2D p, 
            Point2D anchor, Rectangle2D bounds)
    {
        if ( anchor == null ) {
            p.setLocation(o);
            return;
        }
        double x, y, v, dmax;
        double w = bounds.getWidth(), h = bounds.getHeight();
        double px = o.getX(), ax = anchor.getX();
        dmax = Math.max(ax-bounds.getMinX(), bounds.getMaxX()-ax);
        v = Math.abs(px - ax)/dmax;
        x = (d+1)/(d+(1/v));
        x = (px<ax?-1:1)*dmax*x + ax;
        
        double py = o.getY(), ay = anchor.getY();
        dmax = Math.max(ay-bounds.getMinY(), bounds.getMaxY()-ay);
        v = Math.abs(py - ay) / dmax;
        y = (d+1)/(d+(1/v));
        y = (py<ay?-1:1)*dmax*y + ay;
        
        p.setLocation(x,y);
    } //
    
} // end of class DistortionLayout
