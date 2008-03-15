/**
 *
 */
package prefuse.util.ui;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A line defined by polar coordinates (a start position, a radius and an angle).
 * 
 * @author Anton Marsden
 */
public class PolarLine2D extends Line2D
{
    private double x;
    private double y;
    private double theta;
    private double r;

    /**
     * Construct a new line.
     * 
     * @param start the start coordinate of the line
     * @param end the end coordinate of the line
     */
    public PolarLine2D( Point2D start, Point2D end )
    {
        setLine( start.getX(), start.getY(), end.getX(), end.getY() );
    }

    /**
     * Construct a new line from an existing line.
     * 
     * @param line the line to get the coordinates from.
     */
    public PolarLine2D( Line2D line )
    {
        setLine( line.getX1(), line.getY1(), line.getX2(), line.getY2() );
    }

    /**
     * Construct a new line.
     * 
     * @param x the starting X coordinate
     * @param y the starting Y coordinate
     * @param r the radius (length) of the line
     * @param theta the angle of the line
     */
    public PolarLine2D( double x, double y, double r, double theta )
    {
        this.x     = x;
        this.y     = y;
        this.r     = r;
        this.theta = theta;
    }

    /**
     * 
     * @return the radius (length) of the line
     */
    public double getRadius()
    {
        return r;
    }

    /**
     * Set the radius (length) of the line.
     * 
     * @param radius the radius (length) of the line
     */
    public void setRadius( double radius )
    {
        this.r = radius;
    }

    /**
     * @return the angle of the line (in radians)
     */
    public double getTheta()
    {
        return theta;
    }

    /**
     * Set the angle of the line
     * 
     * @param theta the angle of the line, in radians
     */
    public void setTheta( double theta )
    {
        this.theta = theta;
    }
    
    public void setX1(double x1) {
    	this.x = x1;
    }

    public double getX1()
    {
        return x;
    }

    public void setY1(double y1) {
    	this.y = y1;
    }

    public double getY1()
    {
        return y;
    }

    public Point2D getP1()
    {
        return new Point2D.Double( x, y );
    }

    public double getX2()
    {
        return x + r * Math.cos( theta );
    }

    public double getY2()
    {
        return y + r * Math.sin( theta );
    }

    public Point2D getP2()
    {
        return new Point2D.Double( getX2(), getY2() );
    }

    public void setLine( double x1, double y1, double x2, double y2 )
    {
        x = x1;
        y = y1;
        double xdiff = x2 - x1;
        double ydiff = y2 - y1;
        r     = Math.sqrt( xdiff * xdiff
                           + ydiff * ydiff );
        theta = Math.atan2( ydiff, xdiff );
    }

    public Rectangle2D getBounds2D()
    {
        double xt;
        double yt;
        double wt;
        double ht;
        double x2 = getX2();
        double y2 = getY2();
        if ( x < x2 )
        {
            xt = x;
            wt = x2 - x;
        }
        else
        {
            xt = x2;
            wt = x - x2;
        }
        if ( y < y2 )
        {
            yt = y;
            ht = y2 - y;
        }
        else
        {
            yt = y2;
            ht = y - y2;
        }

        return new Rectangle2D.Double( xt, yt, wt, ht );
    }
}