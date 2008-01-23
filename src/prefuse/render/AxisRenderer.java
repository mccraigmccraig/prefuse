package prefuse.render;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import prefuse.Alignment;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.VisualItem;

/**
 * Renderer for drawing an axis tick mark and label.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class AxisRenderer extends AbstractShapeRenderer {

    private final Line2D      m_line = new Line2D.Double();
    private final Rectangle2D m_box  = new Rectangle2D.Double();

    private Alignment m_xalign;
    private Alignment m_yalign;
    private int m_ascent;

    /**
     * Create a new AxisRenderer. By default, axis labels are drawn along the
     * left edge and underneath the tick marks.
     */
    public AxisRenderer() {
        this(Alignment.LEFT, Alignment.BOTTOM);
    }

    /**
     * Create a new AxisRenderer.
     * @param xalign the horizontal alignment for the axis label. One of
     * {@link Alignment#LEFT}, {@link Alignment#RIGHT},
     * or {@link Alignment#CENTER}.
     * @param yalign the vertical alignment for the axis label. One of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM},
     * or {@link Alignment#CENTER}.
     */
    public AxisRenderer(Alignment xalign, Alignment yalign) {
        m_xalign = xalign;
        m_yalign = yalign;
    }

    /**
     * Set the horizontal alignment of axis labels.
     * @param xalign the horizontal alignment for the axis label. One of
     * {@link Alignment#LEFT}, {@link Alignment#RIGHT},
     * or {@link Alignment#CENTER}.
     */
    public void setHorizontalAlignment(Alignment xalign) {
        m_xalign = xalign;
    }

    /**
     * Set the vertical alignment of axis labels.
     * @param yalign the vertical alignment for the axis label. One of
     * {@link Alignment#TOP}, {@link Alignment#BOTTOM},
     * or {@link Alignment#CENTER}.
     */
    public void setVerticalAlignment(Alignment yalign) {
        m_yalign = yalign;
    }

    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    @Override
	protected Shape getRawShape(VisualItem<?> item) {
        double x1 = item.getDouble(VisualItem.X);
        double y1 = item.getDouble(VisualItem.Y);
        double x2 = item.getDouble(VisualItem.X2);
        double y2 = item.getDouble(VisualItem.Y2);
        m_line.setLine(x1,y1,x2,y2);

        if ( !item.canGetString(VisualItem.LABEL) ) {
			return m_line;
		}

        String label = item.getString(VisualItem.LABEL);
        if ( label == null ) {
			return m_line;
		}

        FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(item.getFont());
        m_ascent = fm.getAscent();
        int h = fm.getHeight();
        int w = fm.stringWidth(label);

        double tx, ty;

        // get text x-coord
        switch ( m_xalign ) {
        case FAR_RIGHT:
            tx = x2 + 2;
            break;
        case FAR_LEFT:
            tx = x1 - w - 2;
            break;
        case CENTER:
            tx = x1 + (x2-x1)/2 - w/2;
            break;
        case RIGHT:
            tx = x2 - w;
            break;
        case LEFT:
        default:
            tx = x1;
        }
        // get text y-coord
        switch ( m_yalign ) {
        case FAR_TOP:
            ty = y1-h;
            break;
        case FAR_BOTTOM:
            ty = y2;
            break;
        case CENTER:
            ty = y1 + (y2-y1)/2 - h/2;
            break;
        case TOP:
            ty = y1;
            break;
        case BOTTOM:
        default:
            ty = y2-h;
        }
        m_box.setFrame(tx,ty,w,h);
        return m_box;
    }

    /**
     * @see prefuse.render.Renderer#render(java.awt.Graphics2D, prefuse.visual.VisualItem)
     */
    @Override
    public void render(Graphics2D g, VisualItem<?> item) {
    	Shape s = getShape(item);
    	GraphicsLib.paint(g, item, m_line, getStroke(item), getRenderType(item));

    	// check if we have a text label, if so, render it
    	if ( item.canGetString(VisualItem.LABEL) ) {
	    	float x = (float)m_box.getMinX();
	    	float y = (float)m_box.getMinY() + m_ascent;

	    	// draw label background
	    	GraphicsLib.paint(g, item, s, null, RenderType.FILL);

	    	String str = item.getString(VisualItem.LABEL);
	    	AffineTransform origTransform = g.getTransform();
	    	AffineTransform transform = this.getTransform(item);
	    	if ( transform != null ) g.setTransform(transform);

	    	g.setFont(item.getFont());
	    	g.setColor(ColorLib.getColor(item.getTextColor()));
	    	g.drawString(str, x, y);

	    	if ( transform != null ) g.setTransform(origTransform);
    	}
	}

    /**
     * @see prefuse.render.Renderer#locatePoint(java.awt.geom.Point2D, prefuse.visual.VisualItem)
     */
    @Override
	public boolean locatePoint(Point2D p, VisualItem<?> item) {
        Shape s = getShape(item);
        if ( s == null ) {
            return false;
        } else if ( s == m_box && m_box.contains(p) ) {
            return true;
        } else {
            double width = Math.max(2, item.getSize());
            double halfWidth = width/2.0;
            return s.intersects(p.getX()-halfWidth,
                                p.getY()-halfWidth,
                                width,width);
        }
    }

    /**
     * @see prefuse.render.Renderer#setBounds(prefuse.visual.VisualItem)
     */
    @Override
	public void calculateBounds(VisualItem<?> item, Rectangle2D bounds) {
        if ( !m_manageBounds ) {
        	bounds.setRect(item.getX(), item.getY(), 0, 0);
			return;
		}
        Shape shape = getShape(item);
        if ( shape == null ) {
        	bounds.setRect(item.getX(), item.getY(), 0, 0);
        } else if ( shape == m_line ) {
            GraphicsLib.calculateBounds(item, shape, getStroke(item), bounds);
        } else {
            m_box.add(m_line.getX1(),m_line.getY1());
            m_box.add(m_line.getX2(),m_line.getY2());
        	bounds.setRect(m_box.getMinX(), m_box.getMinY(), m_box.getWidth(), m_box.getHeight());
			return;
        }
    }

} // end of class AxisRenderer
