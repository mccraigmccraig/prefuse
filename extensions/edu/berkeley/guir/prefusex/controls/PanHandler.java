package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

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
public class PanHandler extends ControlAdapter {

    private int xDown, yDown;
    private boolean repaint = true;
    
    public PanHandler() {
        this(true);
    } //
    
    public PanHandler(boolean repaint) {
        this.repaint = repaint;
    } //    
    
    public void mousePressed(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            e.getComponent().setCursor(
                Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            xDown = e.getX();
            yDown = e.getY();
        }
    } //
    
    public void mouseDragged(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            Display display = (Display)e.getComponent();
            int x = e.getX(), y = e.getY();
            int dx = x-xDown, dy = y-yDown;
            display.pan(dx,dy);
            xDown = x;
            yDown = y;
            if ( repaint )
                display.repaint();
        }
    } //
    
    public void mouseReleased(MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) ) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
            xDown = -1;
            yDown = -1;
        }
    } //
    
} // end of class PanHandler
