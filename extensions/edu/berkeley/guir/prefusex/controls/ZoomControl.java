package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.VisualItem;
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
    private boolean zoomOverItem = true;
    private double minScale = 1E-3;
    private double maxScale = 75;
    
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
    
    private void start(MouseEvent e) {
    	if ( SwingUtilities.isRightMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            if (display.isTranformInProgress()) {
                yLast = -1;
                return;
            }
            display.setCursor(
                Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            display.getAbsoluteCoordinate(e.getPoint(), down);
            yLast = e.getY();
        }
    }
    
    private void drag(MouseEvent e) {
    	if ( SwingUtilities.isRightMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            if (display.isTranformInProgress() || yLast == -1) {
                yLast = -1;
                return;
            }
            
            double scale = display.getScale();
            
            int y = e.getY();
            int dy = y-yLast;
            double zoom = 1 + ((double)dy) / 100;
            double result = scale*zoom;
            if ( result < minScale ) {
                zoom = minScale/scale;
                display.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            } else if ( result > maxScale ){
                zoom = maxScale/scale;
                display.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            } else {
                display.setCursor(
                        Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            }
            
            display.zoomAbs(down, zoom);
            
            yLast = y;
            if ( repaint )
                display.repaint();
        }
    }
    
    private void end(MouseEvent e) {
    	if ( SwingUtilities.isRightMouseButton(e) ) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }
    
    public void mousePressed(MouseEvent e) {
        start(e);
    } //
    
    public void mouseDragged(MouseEvent e) {
        drag(e);
    } //
    
    public void mouseReleased(MouseEvent e) {
        end(e);
    } //
    
    public void itemPressed(VisualItem item, MouseEvent e) {
    	if ( zoomOverItem )
    		start(e);
    }

    public void itemDragged(VisualItem item, MouseEvent e) {
    	if ( zoomOverItem )
    		drag(e);
    }
    
    public void itemReleased(VisualItem item, MouseEvent e) {
    	if ( zoomOverItem )
    		end(e);
    }
    
    /**
     * Gets the maximum scale value allowed by this zoom control
     * @return the maximum scale value 
     */
    public double getMaxScale() {
        return maxScale;
    } //
    
    /**
     * Sets the maximum scale value allowed by this zoom control
     * @return the maximum scale value 
     */
    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
    } //
    
    /**
     * Gets the minimum scale value allowed by this zoom control
     * @return the minimum scale value 
     */
    public double getMinScale() {
        return minScale;
    } //
    
    /**
     * Sets the minimum scale value allowed by this zoom control
     * @return the minimum scale value 
     */
    public void setMinScale(double minScale) {
        this.minScale = minScale;
    } //
    
    /**
	 * Indicates if the zoom control will work while the mouse is
	 * over a VisualItem.
	 * @return true if the control still operates over a VisualItem
	 */
	public boolean isZoomOverItem() {
		return zoomOverItem;
	} //

	/**
	 * Determines if the zoom control will work while the mouse is
	 * over a VisualItem
	 * @param zoomOverItem true to indicate the control operates
	 * over VisualItems, false otherwise
	 */
	public void setZoomOverItem(boolean zoomOverItem) {
		this.zoomOverItem = zoomOverItem;
	} //
    
} // end of class ZoomControl
