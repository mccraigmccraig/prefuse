/**
 *
 */
package prefuse.util.ui;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PolarLine2D extends Line2D
{
    private double x;
    private double y;
    private double theta;
    private double r;

    public PolarLine2D( Point2D start, Point2D end )
    {
        setLine( start.getX(), start.getY(), end.getX(), end.getY() );
    }

    public PolarLine2D( Line2D line )
    {
        setLine( line.getX1(), line.getY1(), line.getX2(), line.getY2() );
    }

    public PolarLine2D( double x, double y, double r, double theta )
    {
        this.x     = x;
        this.y     = y;
        this.r     = r;
        this.theta = theta;
    }

    public double getRadius()
    {
        return r;
    }

    public void setRadius( double radius )
    {
        this.r = radius;
    }

    public double getTheta()
    {
        return theta;
    }

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