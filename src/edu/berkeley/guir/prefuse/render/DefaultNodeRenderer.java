/*
 * Created on Apr 25, 2003
 */
package edu.berkeley.guir.prefuse.render;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.NodeItem;

/**
 * A default implementation of a node that draws itself as a circle.
 * 
 * @author alann
 */
public class DefaultNodeRenderer extends ShapeRenderer {
	protected static Object POSITION_KEY = new Object();
	protected static Object POSITION_TRANSFORM_KEY = new Object();

	// m_radius is class-level, used by aggregate renderer 
	// right now to determine an offset
	static int m_radius = 5;
	private Ellipse2D m_circle =
		new Ellipse2D.Float(0, 0, 2 * m_radius, 2 * m_radius);

	/**
	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.GraphItem)
	 */
	protected Shape getRawShape(GraphItem item) {
		return m_circle;
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRenderType()
	 */
	protected int getRenderType() {
		return RENDER_TYPE_DRAW_AND_FILL;
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getGraphicsSpaceTransform(edu.berkeley.guir.prefuse.GraphItem)
	 */
	protected AffineTransform getGraphicsSpaceTransform(GraphItem item) {
		NodeItem nItem = (NodeItem) item;
		AffineTransform at = new AffineTransform();
		at.translate(nItem.getX() - m_radius, nItem.getY() - m_radius);
		return at;
	} //

} // end of class DefaultNodeRenderer
