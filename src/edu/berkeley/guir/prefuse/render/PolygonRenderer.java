package edu.berkeley.guir.prefuse.render;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.util.GeometryLib;

/**
 * Renders a polygon. Polygon points must be assigned prior to rendering,
 * binding an array of float values (alternating x,y value) to the "polygon"
 * viz attribute. For example, create an array pts of polygon points and then
 * use item.setVizAttribute("polygon", pts).
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class PolygonRenderer extends ShapeRenderer {

    public static final int EDGE_LINE  = 0;
    public static final int EDGE_CURVE = 1;
    
    private int     edgeType = EDGE_LINE;
    private float   controlFrac = 0.10f;
    
    public PolygonRenderer() {
        this(EDGE_LINE);
    } //
    
    public PolygonRenderer(int edgeType) {
        this.edgeType = edgeType;
    } //

    public int getEdgeType() {
        return edgeType;
    } //
    
    public void setEdgeType(int edgeType) {
        this.edgeType = edgeType;
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {
        float[] poly = (float[])item.getVizAttribute("polygon");
        float x = (float)item.getX();
        float y = (float)item.getY();
        
        if ( edgeType == EDGE_LINE ) {
            GeneralPath path = new GeneralPath();
            path.moveTo(x+poly[0],y+poly[1]);
            for ( int i=2; i<poly.length; i+=2 ) {
                path.lineTo(x+poly[i],y+poly[i+1]);
            }
            path.closePath();
            return path;
        } else if ( edgeType == EDGE_CURVE ) {
            GeneralPath path = GeometryLib.cardinalSpline(poly,.25f,true);
            if ( x != 0 || y != 0 ) {
                Shape s = path.createTransformedShape(
                        AffineTransform.getTranslateInstance(x,y));
                return s;
            } else {
                return path;
            }
        }
        return null;
    } //

} // end of class PolygonRenderer
