/**
 * 
 */
package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseWheelEvent;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Zooms the display using the mouse scroll wheel, changing the scale of the
 * viewable region.
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class WheelZoomControl extends ControlAdapter {
	
    private double minScale = 1E-3;
    private double maxScale = 75;
	private boolean zoomOverItem = true;
	
	public void itemWheelMoved(VisualItem item, MouseWheelEvent e) {
		if ( zoomOverItem )
			zoom(e);
	} //
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoom(e);
	} //

	private void zoom(MouseWheelEvent e) {
		Display display = (Display) e.getComponent();
		
		double zoom = 1 + 0.1f * e.getWheelRotation();
		double scale = display.getScale();
		double result = scale * zoom;

		if ( result < minScale ) {
			zoom = minScale/scale;
        } else if ( result > maxScale ){
        	zoom = maxScale/scale;
        }		
		
		display.zoom(e.getPoint(),zoom);
		display.repaint();
	} //
	
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
    
} // end of class WheelZoomControl