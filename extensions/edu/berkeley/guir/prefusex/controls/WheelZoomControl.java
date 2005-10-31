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
	
	public void itemWheelMoved(VisualItem item, MouseWheelEvent e) {
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
	
} // end of class WheelZoomControl