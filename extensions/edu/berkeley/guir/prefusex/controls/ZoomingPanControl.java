package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * <p>
 * Allows users to pan over a display such that the display zooms in and
 * out proportionally to how fast the pan is performed.
 * </p>
 * 
 * <p>
 * <b>TODO</b>: this is a broken, work in progress. <b>don't use this yet.</b>
 * </p>
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ZoomingPanControl extends ControlAdapter {

    private int xDown, yDown;
    private double sDown;
    private boolean repaint = true;
    
    private double v0 = 1.0, d0 = 100, d1 = 600, s0 = .1;
    
    public ZoomingPanControl() {
        this(true);
    } //
    
    public ZoomingPanControl(boolean repaint) {
        this.repaint = repaint;
    } //
    
    public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            display.setCursor(
                    Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            xDown = e.getX(); yDown = e.getY();
            sDown = display.getTransform().getScaleX();
        }        
    } //
    
    public void mouseDragged(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            int x = e.getX(), y = e.getY();
            int dx = x-xDown, dy = y-yDown;
            double d = Math.sqrt(dx*dx+dy*dy);
            double s, v;
            if ( d <= d0 ) {
                s = 1.0;
                v = v0*(d/d0);
            } else {
                s = ( d >= d1 ? s0 : Math.pow(s0, (d-d0)/(d1-d0)) );
                v = v0 / s;
            }
            double sx = display.getTransform().getScaleX();
            s = s/sx;
            
            display.pan(v*dx/d,v*dy/d);
            display.zoom(e.getPoint(), s);
            
            if ( repaint )
                display.repaint();
        }
    } //
    
    public void mouseReleased(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            double sx = display.getTransform().getScaleX();
            double s = sDown/sx;
            display.zoom(e.getPoint(), s);
            if ( repaint )
                display.repaint();
            display.setCursor(Cursor.getDefaultCursor());
        }
    } //
    
} // end of class ZoomingPanControl
