package edu.berkeley.guir.prefuse.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Represents a clipping rectangle in a prefuse <code>Display</code>.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class Clip {
    
    private double[] clip = new double[4];
    private double[] tmp  = new double[4];
    
    public void setClip(double x1, double y1, double x2, double y2) {
        clip[0] = x1; clip[1] = y1;
        clip[2] = x2; clip[3] = y2;
    } //
    
    public void setClip(Clip c) {
        System.arraycopy(c.clip, 0, clip, 0, 4);
    } //
    
    public void setClip(Rectangle2D r) {
        clip[0] = r.getX(); clip[1] = r.getY();
        clip[2] = clip[0]+r.getWidth();
        clip[3] = clip[1]+r.getHeight();
    } //
    
    public void transform(AffineTransform at) {
        at.transform(clip,0,tmp,0,2);
        double[] s = tmp;
        tmp = clip;
        clip = s;
    } //
    
    public void limit(double x, double y, double w, double h) {
        clip[0] = Math.max(clip[0],x);
        clip[1] = Math.max(clip[1],y);
        clip[2] = Math.min(clip[2],w);
        clip[3] = Math.min(clip[3],h);
    } //
    
    public boolean intersects(Rectangle2D r) {
        double tw = clip[2]-clip[0];
        double th = clip[3]-clip[1];
        double rw = r.getWidth();
        double rh = r.getHeight();
        if (rw < 0 || rh < 0 || tw < 0 || th < 0) {
            return false;
        }
        double tx = clip[0];
        double ty = clip[1];
        double rx = r.getX();
        double ry = r.getY();
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    } //
    
    public void union(Clip c) {
        clip[0] = Math.min(clip[0], c.clip[0]);
        clip[1] = Math.min(clip[1], c.clip[1]);
        clip[2] = Math.max(clip[2], c.clip[2]);
        clip[3] = Math.max(clip[3], c.clip[3]);
    } //
    
    public void union(Rectangle2D r) {
        clip[0] = Math.min(clip[0], r.getX()-1);
        clip[1] = Math.min(clip[1], r.getY()-1);
        clip[2] = Math.max(clip[2], r.getX()+r.getWidth()+1);
        clip[3] = Math.max(clip[3], r.getX()+r.getHeight()+1);
    } //
    
    public double getX() {
        return clip[0];
    } //
    
    public double getY() {
        return clip[1];
    } //
    
    public double getWidth() {
        return clip[2]-clip[0];
    } //
    
    public double getHeight() {
        return clip[3]-clip[1];
    } //
    
} // end of inner class Clip
