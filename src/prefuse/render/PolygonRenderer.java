package prefuse.render;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import prefuse.Constants;
import prefuse.data.Schema;
import prefuse.util.GraphicsLib;
import prefuse.visual.VisualItem;


/**
 * <p>Renderer for drawing a polygon. VisualItems must have a data field
 * containing an array of floats that tores the polyon. A {@link Float#NaN}
 * value can be used to mark the end point of the polygon for float arrays
 * larger than their contained points.</p>
 * 
 * <p>A polygon edge type parameter (one of 
 * {@link prefuse.Constants#POLY_TYPE_LINE},
 * {@link prefuse.Constants#POLY_TYPE_CURVE}, or
 * {@link prefuse.Constants#POLY_TYPE_STACK}) determines how the
 * edges of the polygon are drawn. The LINE type result in a standard polygon,
 * with straight lines drawn between each sequential point. The CURVE type
 * causes the edges of the polygon to be interpolated as a cardinal spline,
 * giving a smooth blob-like appearance to the shape. The STACK type is similar
 * to the curve type except that straight line segments (not curves) are used
 * when the slope of the line between two adjacent points is zero. This is
 * useful for drawing stacks of data with otherwise curved edges.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class PolygonRenderer extends AbstractShapeRenderer {

    /**
     * Default data field for storing polygon (float array) values.
     */
    public static final String POLYGON = "_polygon";
    /**
     * A Schema describing the polygon specification.
     */
    public static final Schema POLYGON_SCHEMA = new Schema();
    static {
        POLYGON_SCHEMA.addColumn(POLYGON, float[].class);
    }
    
    private int    m_polyType = Constants.POLY_TYPE_LINE;
    private float  m_controlFrac = 0.10f;
    private String m_polyfield = POLYGON;
    
    private GeneralPath m_path = new GeneralPath();
    
    /**
     * Create a new PolygonRenderer supporting straight lines.
     */
    public PolygonRenderer() {
        this(Constants.EDGE_TYPE_LINE);
    }
    
    /**
     * Create a new PolygonRenderer.
     * @param polyType the polygon edge type, one of
     * {@link prefuse.Constants#POLY_TYPE_LINE},
     * {@link prefuse.Constants#POLY_TYPE_CURVE}, or
     * {@link prefuse.Constants#POLY_TYPE_STACK}).
     */
    public PolygonRenderer(int polyType) {
        m_polyType = polyType;
    }

    /**
     * Get the polygon line type.
     * @return the polygon edge type, one of
     * {@link prefuse.Constants#POLY_TYPE_LINE},
     * {@link prefuse.Constants#POLY_TYPE_CURVE}, or
     * {@link prefuse.Constants#POLY_TYPE_STACK}).
     */
    public int getPolyType() {
        return m_polyType;
    }
    
    /**
     * Set the polygon line type.
     * @param polyType the polygon edge type, one of
     * {@link prefuse.Constants#POLY_TYPE_LINE},
     * {@link prefuse.Constants#POLY_TYPE_CURVE}, or
     * {@link prefuse.Constants#POLY_TYPE_STACK}).
     */
    public void setPolyType(int polyType) {
        if ( polyType < 0 || polyType >= Constants.POLY_TYPE_COUNT ) {
            throw new IllegalArgumentException("Unknown edge type: "+polyType);
        }
        m_polyType = polyType;
    }
    
    /**
     * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {
        float[] poly = (float[])item.get(m_polyfield);
        if ( poly == null ) { return null; }
        
        float x = (float)item.getX();
        float y = (float)item.getY();
        
        // initialize the path
        m_path.reset();
        m_path.moveTo(x+poly[0],y+poly[1]);
        
        if ( m_polyType == Constants.POLY_TYPE_LINE ) {
            // create a polygon
            for ( int i=2; i<poly.length; i+=2 ) {
                if ( Float.isNaN(poly[i]) ) break;
                m_path.lineTo(x+poly[i],y+poly[i+1]);
            }
        } else if ( m_polyType == Constants.POLY_TYPE_CURVE ) {
            // create a closed curve and return it
            return GraphicsLib.cardinalSpline(m_path, poly, 
                    m_controlFrac, true, x, y);
        } else if ( m_polyType == Constants.POLY_TYPE_STACK ) {
            // TODO generalize this correctly
            int np = poly.length/4;
            // set bottom level curve
            GraphicsLib.cardinalSpline(m_path, poly, 0, np, m_controlFrac,false,x,y);
            // straight line to top curve
            m_path.lineTo(x+poly[np*2], y+poly[np*2+1]);
            // set top level curve
            GraphicsLib.cardinalSpline(m_path, poly, np*2, np, m_controlFrac,false,x,y);
            // straight line to bottom curve
            m_path.lineTo(x+poly[0], y+poly[1]);
        }
        m_path.closePath();
        return m_path;
    }

} // end of class PolygonRenderer
