package edu.berkeley.guir.prefuse.util;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 * 
 * Feb 21, 2004 - jheer - Created class
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
    
    public void setClip(Rectangle r) {
        clip[0] = r.x; clip[1] = r.y;
        clip[2] = r.x+r.width;
        clip[3] = r.y+r.height;
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
    
    public boolean intersects(Rectangle r) {
        int tw = (int)Math.ceil(clip[2]-clip[0]);
        int th = (int)Math.ceil(clip[3]-clip[1]);
        int rw = r.width;
        int rh = r.height;
        if (rw < 0 || rh < 0 || tw < 0 || th < 0) {
            return false;
        }
        int tx = (int)Math.floor(clip[0]);
        int ty = (int)Math.floor(clip[1]);
        int rx = r.x;
        int ry = r.y;
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
    
    public void union(Rectangle r) {
        clip[0] = Math.min(clip[0], r.x-1);
        clip[1] = Math.min(clip[1], r.y-1);
        clip[2] = Math.max(clip[2], r.x+r.width+1);
        clip[3] = Math.max(clip[3], r.y+r.height+1);
    } //
    
    public int getX() {
        return (int)Math.floor(clip[0]);
    } //
    
    public int getY() {
        return (int)Math.floor(clip[1]);
    } //
    
    public int getWidth() {
        return (int)Math.ceil(clip[2]-clip[0]);
    } //
    
    public int getHeight() {
        return (int)Math.ceil(clip[3]-clip[1]);
    } //
    
} // end of inner class Clip
