package edu.berkeley.guir.prefuse.render;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Interface for GraphItem renderers. Default implementation is suitable for shape drawing.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Alan Newberger
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class ShapeRenderer implements Renderer {
	public static final int RENDER_TYPE_NONE = 0;
	public static final int RENDER_TYPE_DRAW = 1;
	public static final int RENDER_TYPE_FILL = 2;
	public static final int RENDER_TYPE_DRAW_AND_FILL = 3;

	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#render(java.awt.Graphics2D, edu.berkeley.guir.prefuse.GraphItem)
	 */
	public void render(Graphics2D g, GraphItem item) {
		Shape shape = getShape(item);
		if (shape != null)
			drawShape(g, item, shape);
	} //
	
	/**
	 * Draws the specified shape into the provided Graphics context, using
	 * color values determined from the specified GraphItem. Can be used
	 * by subclasses in custom rendering routines. 
	 */
	protected void drawShape(Graphics2D g, GraphItem item, Shape shape) {
		switch (getRenderType()) {
			case RENDER_TYPE_DRAW :
				g.setColor(item.getColor());
				g.draw(shape);
				break;
			case RENDER_TYPE_FILL :
				g.setColor(item.getFillColor());
				g.fill(shape);
				break;
			case RENDER_TYPE_DRAW_AND_FILL :
				g.setColor(item.getFillColor());
				g.fill(shape);
				g.setColor(item.getColor());
				g.draw(shape);
				break;
		}		
	} //

	/**
	 * Returns the shape describing the boundary of an item. Shape should be in
	 * image space. 
	 * TODO? implement a clean way of caching transformed Shapes
	 * @param item the item for which to get the Shape
	 */
	public Shape getShape(GraphItem item) {
		AffineTransform at = getGraphicsSpaceTransform(item);
		Shape rawShape = getRawShape(item);
		if (at == null) {
			return rawShape;
		} else {
			return at.createTransformedShape(rawShape);
		}
	} //

	/**
	 * Return a non-transformed shape for the visual representation of the
	 * item. Subclasses must implement this method.
	 * @param item the GraphItem
	 * @return the "raw", untransformed shape.
	 */
	protected abstract Shape getRawShape(GraphItem item);

	/**
	 * Return the graphics space transform for this item, if any. Subclasses
	 * must implement this method, but it may return null if no transformation
	 * is needed.
	 * @param item the GraphItem
	 * @return the graphics space transform, or null if none
	 */
	protected abstract AffineTransform getGraphicsSpaceTransform(GraphItem item);

	/**
	 * Override to control whether a shape is drawn by its outline, by a fill, or both.
	 * Default is to draw both.
	 * @return the rendering type
	 */
	protected int getRenderType() {
		return RENDER_TYPE_DRAW_AND_FILL;
	}

	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#locatePoint(java.awt.geom.Point2D, edu.berkeley.guir.prefuse.GraphItem)
	 */
	public boolean locatePoint(Point2D p, GraphItem item) {
		Shape s = getShape(item);
		return (s != null ? s.contains(p) : false);
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#getBounds(edu.berkeley.guir.prefuse.GraphItem)
	 */
	public Rectangle getBounds(GraphItem item) {
		Shape s = getShape(item);
		return (s != null ? s.getBounds() : new Rectangle(-1, -1, 0, 0));
	} //

} // end of interface Renderer
