package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Zooms the display, changing the scale of the viewable region. Zooming
 * is achieved by pressing the right mouse button on the background of the
 * visualization and dragging the mouse up or down. Moving the mouse up
 * zooms out the display around the spot the mouse was originally pressed.
 * Moving the mouse down similarly zooms in the display, making items
 * larger.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ZoomControl extends ControlAdapter {

    private int yLast;
    private Point2D down = new Point2D.Float();
    private boolean repaint = true;
    
    /**
     * Creates a new zooming control that issues repaint requests as an item
     * is dragged.
     */
    public ZoomControl() {
        this(true);
    } //
    
    /**
     * Creates a new zooming control that optionally issues repaint requests
     * as an item is dragged.
     * @param repaint indicates whether or not repaint requests are issued
     *  as zooming events occur. This can be set to false if other activities
     *  (for example, a continuously running force simulation) are already
     *  issuing repaint events.
     */
    public ZoomControl(boolean repaint) {
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

} // end of class ZoomControl
