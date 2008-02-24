package prefuse.render;

import java.awt.Shape;

import prefuse.ShapeBuilder;
import prefuse.visual.VisualItem;

/**
 * Renderer for drawing simple shapes using VisualItem.SHAPEBUILDER.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ShapeRenderer extends AbstractShapeRenderer {

    private int m_baseSize = 10;

    /**
     * Creates a new ShapeRenderer with default base size of 10 pixels.
     */
    public ShapeRenderer() {
    }

    /**
     * Creates a new ShapeRenderer with given base size.
     * @param size the base size in pixels
     */
    public ShapeRenderer(int size) {
       setBaseSize(size);
    }

    /**
     * Sets the base size, in pixels, for shapes drawn by this renderer. The
     * base size is the width and height value used when a VisualItem's size
     * value is 1. The base size is scaled by the item's size value to arrive
     * at the final scale used for rendering.
     * @param size the base size in pixels
     */
    public void setBaseSize(int size) {
        m_baseSize = size;
    }

    /**
     * Returns the base size, in pixels, for shapes drawn by this renderer.
     * @return the base size in pixels
     */
    public int getBaseSize() {
        return m_baseSize;
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    @Override
	protected Shape getRawShape(VisualItem<?> item) {
        ShapeBuilder builder = item.getShapeBuilder();
        
        if(builder == null) {
        	return null;
        }
        
        double x = item.getX();
        if ( Double.isNaN(x) || Double.isInfinite(x) ) {
			x = 0;
		}
        double y = item.getY();
        if ( Double.isNaN(y) || Double.isInfinite(y) ) {
			y = 0;
		}
        double width = m_baseSize*item.getSize();

        // Center the shape around the specified x and y
        if ( width > 1 ) {
            x = x-width/2;
            y = y-width/2;
        }
        return builder.createShape(x, y, width, width);
    }

} // end of class ShapeRenderer
