package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.util.display.DisplayLib;

/**
 * Zooms a display such that all nodes will fit.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ZoomToFitControl extends ControlAdapter {

    private int margin;
    private int mouseButton = MouseEvent.BUTTON3;
    
    public ZoomToFitControl() {
        this(50);
    } //
    
    public ZoomToFitControl(int margin) {
        this.margin = margin;
    } //
    
    public void itemClicked(VisualItem item, MouseEvent e) {
        mouseClicked(e);
    } //
    
    public void mouseClicked(MouseEvent e) {
        if ( e.getButton() == mouseButton ) {
            Display display = (Display)e.getComponent();
	        ItemRegistry registry = display.getRegistry();
	        Rectangle2D b = DisplayLib.getNodeBounds(registry,margin);
	        DisplayLib.fitViewToBounds(display, b);
        }
    } //
    
} // end of class ZoomToFitControl
