package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Changes an item's location when dragged on screen. Other effects
 * include fixing a nodes position when the mouse if over it, and
 * changing the mouse cursor to a hand when the mouse passes over an
 * item.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DragControl extends ControlAdapter {

    private GraphItem activeItem;
    private Point2D down = new Point2D.Double();
    private Point2D tmp = new Point2D.Double();
    private boolean wasFixed, dragged;
    private boolean repaint = true;
    
    /**
     * Creates a new drag control that issues repaint requests as an item
     * is dragged.
     */
    public DragControl() {
    } //
    
    /**
     * Creates a new drag control that optionally issues repaint requests
     * as an item is dragged.
     * @param repaint indicates whether or not repaint requests are issued
     *  as drag events occur. This can be set to false if other activities
     *  (for example, a continuously running force simulation) are already
     *  issuing repaint events.
     */
    public DragControl(boolean repaint) {
        this.repaint = repaint;
    } //
    
    public void itemEntered(GraphItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        activeItem = item;
        wasFixed = item.isFixed();
        item.setFixed(true);
    } //
    
    public void itemExited(GraphItem item, MouseEvent e) {
        if ( activeItem == item ) {
            activeItem = null;
            item.setFixed(wasFixed);
        }
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getDefaultCursor());
    } //
    
    public void itemPressed(GraphItem item, MouseEvent e) {
        dragged = false;
        Display d = (Display)e.getComponent();
        down = d.getAbsoluteCoordinate(e.getPoint(), down);
    } //
    
    public void itemReleased(GraphItem item, MouseEvent e) {
        if ( dragged ) {
            activeItem = null;
            item.setFixed(wasFixed);
            dragged = false;
        }
    } //
    
    public void itemDragged(GraphItem item, MouseEvent e) {
        dragged = true;
        Display d = (Display)e.getComponent();
        tmp = d.getAbsoluteCoordinate(e.getPoint(), tmp);
        double dx = tmp.getX()-down.getX();
        double dy = tmp.getY()-down.getY();
        Point2D p = item.getLocation();
        item.updateLocation(p.getX()+dx,p.getY()+dy);
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
