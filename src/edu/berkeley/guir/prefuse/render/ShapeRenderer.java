package edu.berkeley.guir.prefuse.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * An abstract implementation of the Renderer interface supporting the
 * drawing of basic shapes. Subclasses should override the
 * {@link getRawShape(edu.berkeley.guir.prefuse.GraphItem) getRawShape}
 * which return the shape to draw. Optionally, subclasses can also override the
 * {@link getGraphicsSpaceTransform(edu.berkeley.guir.prefuse.GraphItem)
 * getGraphicsSpaceTransform} to apply a desired <code>AffineTransform</code>
 * to the shape. For more efficient rendering, subclasses should use a
 * single shape instance in memory, and update its parameters on each call
 * to getRawShape, rather than allocating a new Shape object each time.
 * 
 * @version 1.0
 * @author Alan Newberger
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class ShapeRenderer implements Renderer {
	public static final int RENDER_TYPE_NONE = 0;
	public static final int RENDER_TYPE_DRAW = 1;
	public static final int RENDER_TYPE_FILL = 2;
	public static final int RENDER_TYPE_DRAW_AND_FILL = 3;

    private int m_renderType = RENDER_TYPE_DRAW_AND_FILL;
    
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
	    Paint itemColor = item.getColor();
        if ( itemColor == null ) itemColor = Color.BLACK;
        Paint fillColor = item.getFillColor();
        if ( fillColor == null ) fillColor = Color.BLACK;
		switch (getRenderType()) {
			case RENDER_TYPE_DRAW :
				g.setPaint(itemColor);
				g.draw(shape);
				break;
			case RENDER_TYPE_FILL :
				g.setPaint(fillColor);
				g.fill(shape);
				break;
			case RENDER_TYPE_DRAW_AND_FILL :
				g.setPaint(fillColor);
				g.fill(shape);
				g.setPaint(itemColor);
				g.draw(shape);
				break;
		}		
	} //

	/**
	 * Returns the shape describing the boundary of an item. Shape should be in
	 * image space.
	 * @param item the item for which to get the Shape
	 */
	public Shape getShape(GraphItem item) {
        // TODO? implement a clean way of caching transformed Shapes
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
	 * can implement this method, otherwise it will return null to indicate
     * no transformation is needed.
	 * @param item the GraphItem
	 * @return the graphics space transform, or null if none
	 */
	protected AffineTransform getGraphicsSpaceTransform(GraphItem item) {
        return null;   
    } //

	/**
	 * Returns a value indicating if a shape is drawn by its outline, by a 
     * fill, or both. The default is to draw both.
	 * @return the rendering type
	 */
	public int getRenderType() {
		return m_renderType;
	} //
    
    /**
     * Sets a value indicating if a shape is drawn by its outline, by a fill, 
     * or both. The default is to draw both.
     * @param type the new rendering type. Should be one of
     *  RENDER_TYPE_NONE, RENDER_TYPE_DRAW, RENDER_TYPE_FILL, or
     *  RENDER_TYPE_DRAW_AND_FILL.
     */
    public void setRenderType(int type) {
        if ( type < RENDER_TYPE_NONE || type > RENDER_TYPE_DRAW_AND_FILL ) {
            throw new IllegalArgumentException("Unrecognized render type.");
        }
        m_renderType = type;
    } //
    
	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#locatePoint(java.awt.geom.Point2D, edu.berkeley.guir.prefuse.GraphItem)
	 */
	public boolean locatePoint(Point2D p, GraphItem item) {
		Shape s = getShape(item);
		return (s != null ? s.contains(p) : false);
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#getBoundsRef(edu.berkeley.guir.prefuse.GraphItem)
	 */
	public Rectangle getBoundsRef(GraphItem item) {
		Shape s = getShape(item);
		return (s != null ? s.getBounds() : new Rectangle(-1, -1, 0, 0));
	} //

} // end of interface Renderer
