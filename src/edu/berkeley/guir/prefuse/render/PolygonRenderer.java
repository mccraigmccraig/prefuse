package edu.berkeley.guir.prefuse.render;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.util.GeometryLib;

/**
 * Renders a polygon. Polygon points must be assigned prior to rendering,
 * binding an array of float values (alternating x,y value) to the "polygon"
 * viz attribute. For example, create an array pts of polygon points and then
 * use item.setVizAttribute("polygon", pts). A Float.NaN value can be used to
 * mark the end point of the polygon for float arrays larger than their
 * contained points.
 * 
 * The edge type parameter (one of EDGE_LINE or EDGE_CURVE) determines how the
 * edges of the polygon are drawn. EDGE_LINE results in a standard polygon, with
 * straight lines drawn between each sequential point. EDGE_CURVE causes the
 * edges of the polygon to be interpolated as a cardinal spline, giving a smooth
 * blob-like appearance to the shape.
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class PolygonRenderer extends ShapeRenderer {

    public static final int EDGE_LINE  = 0;
    public static final int EDGE_CURVE = 1;
    
    private int   m_edgeType = EDGE_LINE;
    private float m_controlFrac = 0.10f;
    
    private GeneralPath m_path = new GeneralPath();
    
    public PolygonRenderer() {
        this(EDGE_LINE);
    } //
    
    public PolygonRenderer(int edgeType) {
        m_edgeType = edgeType;
    } //

    public int getEdgeType() {
        return m_edgeType;
    } //
    
    public void setEdgeType(int edgeType) {
        if ( edgeType != EDGE_LINE && edgeType != EDGE_CURVE ) {
            throw new IllegalArgumentException("Unknown edge type.");
        }
        m_edgeType = edgeType;
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {
        float[] poly = (float[])item.getVizAttribute("polygon");
        if ( poly == null ) { return null; }
        
        float x = (float)item.getX();
        float y = (float)item.getY();
        
        if ( m_edgeType == EDGE_LINE ) {
            m_path.reset();
            m_path.moveTo(x+poly[0],y+poly[1]);
            for ( int i=2; i<poly.length; i+=2 ) {
                if ( Float.isNaN(poly[i]) ) break;
                m_path.lineTo(x+poly[i],y+poly[i+1]);
            }
            m_path.closePath();
            return m_path;
        } else if ( m_edgeType == EDGE_CURVE ) {
            m_path.reset();
            return GeometryLib.cardinalSpline(m_path, poly, 
                    m_controlFrac, true, x, y);
        }
        return null;
    } //

} // end of class PolygonRenderer
