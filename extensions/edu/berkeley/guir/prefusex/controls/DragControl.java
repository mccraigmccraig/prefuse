package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Changes an item's location when dragged on screen.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DragControl extends ControlAdapter {

    private GraphItem activeItem;
    private Point2D down = new Point2D.Double();
    private Point2D tmp = new Point2D.Double();
    private boolean repaint = true;
    
    public DragControl() {
    } //
    
    public DragControl(boolean repaint) {
        this.repaint = repaint;
    } //
    
    public void itemPressed(GraphItem item, MouseEvent e) {
        Display d = (Display)e.getComponent();
        down = d.getAbsoluteCoordinate(e.getPoint(), down);
        activeItem = item;
    } //
    
    public void itemReleased(GraphItem item, MouseEvent e) {
        activeItem = null;
    } //
    
    public void itemDragged(GraphItem item, MouseEvent e) {
        Display d = (Display)e.getComponent();
        tmp = d.getAbsoluteCoordinate(e.getPoint(), tmp);
        double dx = tmp.getX()-down.getX();
        double dy = tmp.getY()-down.getY();
        Point2D p = item.getLocation();
        item.setLocation(p.getX()+dx,p.getY()+dy);
        down.setLocation(tmp);
        if ( repaint ) {
            ItemRegistry registry = item.getItemRegistry();
            Iterator iter = registry.getDisplaysRef().iterator();
            while ( iter.hasNext() ) {
                ((Display)iter.next()).repaint();
            }
        }
    } //
    
} // end of class DragControl
