package edu.berkeley.guir.prefuse.render;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import edu.berkeley.guir.prefuse.GraphItem;

/**
 * A default implementation of a node renderer that draws itself as a circle.
 * 
 * @author alann
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DefaultNodeRenderer extends ShapeRenderer {

	private int m_radius = 5;
	private Ellipse2D m_circle =
		new Ellipse2D.Float(0, 0, 2 * m_radius, 2 * m_radius);

    /**
     * Creates a new DefaultNodeRenderer with default base
     * radius (5 pixels).
     */
    public DefaultNodeRenderer() {
    } //
    
    /**
     * Creates a new DefaultNodeRenderer with given base radius.
     * @param r the base radius for node circles
     */
    public DefaultNodeRenderer(int r) {
       setRadius(r);
    } //
    
    /**
     * Sets the radius of the circle drawn to represent a node.
     * @param r the radius value to set
     */
    public void setRadius(int r) {
        m_radius = r;
        m_circle.setFrameFromCenter(0,0,r,r);
    } //
    
    /**
     * Gets the radius of the circle drawn to represent a node.
     * @param r the radius value
     */
    public int getRadius() {
        return m_radius;
    } //
    
	/**
	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.GraphItem)
	 */
	protected Shape getRawShape(GraphItem item) {
        double r = m_radius*item.getSize();
        m_circle.setFrame(item.getX()-r,item.getY()-r,2*r,2*r);
		return m_circle;
	} //

} // end of class DefaultNodeRenderer
