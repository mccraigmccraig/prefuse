package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * 
 * Feb 10, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ZoomHandler extends ControlAdapter {

    private int yLast;
    private Point2D down = new Point2D.Float();
    private boolean repaint = true;
    
    public ZoomHandler() {
        this(true);
    } //
    
    public ZoomHandler(boolean repaint) {
        this.repaint = repaint;
    } //
    
    public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            display.setCursor(
                Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            display.getAbsoluteCoordinate(e.getPoint(), down);
            yLast = e.getY();
        }
    } //
    
    public void mouseDragged(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            int x = e.getX(), y = e.getY();
            int dy = y-yLast;
            double zoom = 1 + ((double)dy) / 100;
            display.zoomAbs(down, zoom);
            yLast = y;
            if ( repaint )
                display.repaint();
        }
    } //
    
    public void mouseReleased(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    } //

} // end of class ZoomHandler
