package edu.berkeley.guir.prefuse.action;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * Abstract class providing convenience methods for graph layout algorithms.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class Layout extends AbstractAction {

    protected Rectangle2D m_bounds = null;
    protected Point2D     m_anchor = null;
    
    public Rectangle2D getBounds() {
        return m_bounds;
    } //
    
    public Rectangle2D getBounds(ItemRegistry registry) {
        if ( m_bounds != null )
            return m_bounds;
        Display d;
        if ( registry != null && (d=registry.getDisplay(0)) != null )
            return new Rectangle(0,0,d.getWidth(),d.getHeight());
        else
            return new Rectangle();
    } //
    
    public void setBounds(Rectangle2D b) {
        m_bounds = b;
    } //
    
    public Point2D getAnchor() {
        return m_anchor;
    } //
    
    public Point2D getAnchor(ItemRegistry registry) {
        if ( m_anchor != null )
            return m_anchor;
        double x = 0, y = 0;
        if ( registry != null ) {
            Display d = registry.getDisplay(0);
            x = d.getWidth()/2; y = d.getHeight()/2;
        }
        return new Point2D.Double(x,y);
    } //
    
    public void setAnchor(Point2D a) {
        m_anchor = a;
    } //
    
    public abstract void run(ItemRegistry registry, double frac);

} // end of abstract class Layout
