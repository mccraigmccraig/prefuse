package edu.berkeley.guir.prefusex.distortion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 * Creates a graphical fisheye distortion of a graph view. This distortion 
 * allocates more space to items near the layout anchor and less space to 
 * items further away, magnifying space near the anchor and demagnifying 
 * distant space.
 * </p>
 * 
 * <p>
 * For more details on this form of transformation, see Manojit Sarkar and 
 * Marc H. Brown, "Graphical Fisheye Views of Graphs", in Proceedings of 
 * CHI'92, Human Factors in Computing Systems, p. 83-91, 1992. Available
 * online at <a href="http://citeseer.ist.psu.edu/sarkar92graphical.html">
 * http://citeseer.ist.psu.edu/sarkar92graphical.html</a>. 
 * </p>
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FisheyeDistortion extends Distortion {

    private double dx, dy; // distortion factors
    
    /**
     * Creates a new FisheyeDistortion with default distortion factor.
     */
    public FisheyeDistortion() {
        this(4);
    } //
    
    /**
     * Creates a new FisheyeDistortion with the given distortion factor
     * for use along both the x and y directions.
     * @param dfactor the distortion factor (same for both axes)
     */
    public FisheyeDistortion(double dfactor) {
        this(dfactor, dfactor);
    } //
    
    /**
     * Creates a new FisheyeDistortion with the given distortion factors
     * along the x and y directions.
     * @param xfactor the distortion factor along the x axis
     * @param yfactor the distortion factor along the y axis
     */
    public FisheyeDistortion(double xfactor, double yfactor) {
        dx = xfactor;
        dy = yfactor;
    } //
    
    /**
     * Returns the distortion factor for the x-axis.
     * @return returns the distortion factor for the x-axis.
     */
    public double getXDistortionFactor() {
        return dx;
    } //

    /**
     * Sets the distortion factor for the x-axis.
     * @param d The distortion factor to set.
     */
    public void setXDistortionFactor(double d) {
        this.dx = d;
    } //
    
    /**
     * Returns the distortion factor for the y-axis.
     * @return returns the distortion factor for the y-axis.
     */
    public double getYDistortionFactor() {
        return dy;
    } //

    /**
     * Sets the distortion factor for the y-axis.
     * @param d The distortion factor to set.
     */
    public void setYDistortionFactor(double d) {
        this.dy = d;
    } //
    
    /**
     * Calculates a Cartesian graphical fisheye distortion.
     */
    protected void transformPoint(Point2D o, Point2D p, 
            Point2D anchor, Rectangle2D bounds)
    {
        double x = fisheye(o.getX(), anchor.getX(), dx,
                bounds.getMinX(), bounds.getMaxX());
        double y = fisheye(o.getY(), anchor.getY(), dy,
                bounds.getMinY(), bounds.getMaxY());
        p.setLocation(x,y);
    } //
    
    private double fisheye(double x, double a, double d, double min, double max) {
        if ( d != 0 ) {
            double v, m = (x<a ? a-min : max-a);
            if ( m == 0 ) m = max-min;
            v = Math.abs(x - a) / m;
            v = (d+1)/(d+(1/v));
            return (x<a?-1:1)*m*v + a;
        } else {
            return x;
        }
    } //

} // end of class FisheyeDistortion
