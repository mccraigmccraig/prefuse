package edu.berkeley.guir.prefuse.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.PixelGrabber;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.VisualItem;

/**
 * Renders AggregateItems as images retrieved from the ImageFactory.
 * 
 * @author alann
 */
public class DefaultAggregateRenderer implements Renderer {
	private static final double HALF_PI = Math.PI / 2;

	private ImageFactory m_imageFactory = new ImageFactory();
	private AffineTransform at         = new AffineTransform();
	private Point2D         m_tmpPoint = new Point2D.Float();
	private Rectangle2D     m_tmpRect  = new Rectangle2D.Float();
	private int[]           m_pixel    = new int[1];

	/**
	 * override render() to draw an image.
	 */
	public void render(Graphics2D g, VisualItem item) {
		AggregateItem aItem = (AggregateItem) item;
		Image image = m_imageFactory.getImage(aItem);
		if ( image != null )
			g.drawImage(image, getTransform(aItem, image), null);
	} //

	protected AffineTransform getTransform(AggregateItem aItem, Image image) {
		at.setToTranslation(aItem.getX(), aItem.getY());
		at.rotate(aItem.getOrientation() - HALF_PI);
		//at.translate(0, -DefaultNodeRenderer.m_radius);
		at.translate(-image.getWidth(null) / 2, 0);
		return at;
	} //

	/**
	 * Perhaps more efficient than 'createInverse' call on an AffineTransform; does not perform 'new'.
	 */
	protected AffineTransform getInverseTransform(AggregateItem aItem, Image image) {
		at.setToTranslation(image.getWidth(null) / 2, 0);
		//at.translate(0, DefaultNodeRenderer.m_radius);
		at.rotate(HALF_PI - aItem.getOrientation());
		at.translate(-aItem.getX(), -aItem.getY());
		return at;
	} //

    /**
     * @see edu.berkeley.guir.prefuse.render.Renderer#locatePoint(java.awt.geom.Point2D, edu.berkeley.guir.prefuse.VisualItem)
     */
	public boolean locatePoint(Point2D p, VisualItem item) {
		AggregateItem aItem = (AggregateItem) item;
		Image image = m_imageFactory.getImage(aItem);
		if ( image == null ) { return false; }
		getInverseTransform(aItem, image).transform(p, m_tmpPoint);

		int x = (int) m_tmpPoint.getX();
		int y = (int) m_tmpPoint.getY();
		if (x > 0 && x < image.getWidth(null) && y > 0 && y < image.getHeight(null)) 
		{
			PixelGrabber pg = new PixelGrabber(image, x, y, 1, 1, m_pixel, 0, 1);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if ((m_pixel[0] >> 24) != 0)
				return true;
		}
		return false;
	} //
	
    /**
     * @see edu.berkeley.guir.prefuse.render.Renderer#getBoundsRef(edu.berkeley.guir.prefuse.VisualItem)
     */
	public Rectangle2D getBoundsRef(VisualItem item) {
		AggregateItem aItem = (AggregateItem) item;
		Image image = m_imageFactory.getImage(aItem);
		if ( image == null ) { return new Rectangle(-1,-1,0,0);	}
		m_tmpRect.setRect(0,0,image.getWidth(null),image.getHeight(null));
		AffineTransform at = getTransform(aItem, image);
		return at.createTransformedShape(m_tmpRect).getBounds();
	} //

} // end of class DefaultAggregateRenderer
