package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Changes the location of a whole subtree when dragged on screen.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class SubtreeDragControl extends ControlAdapter {

    private GraphItem activeItem;
    private Point2D down = new Point2D.Double();
    private Point2D tmp = new Point2D.Double();
    private boolean wasFixed;
    private boolean repaint = true;
    
    public SubtreeDragControl() {
    } //
    
    public SubtreeDragControl(boolean repaint) {
        this.repaint = repaint;
    } //
    
    public void itemEntered(GraphItem item, MouseEvent e) {
        if ( !(item instanceof NodeItem) ) return;
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    } //
    
    public void itemExited(GraphItem item, MouseEvent e) {
        if ( !(item instanceof NodeItem) ) return;
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getDefaultCursor());
    } //
    
    public void itemPressed(GraphItem item, MouseEvent e) {
        if ( !(item instanceof NodeItem) ) return;
        Display d = (Display)e.getComponent();
        down = d.getAbsoluteCoordinate(e.getPoint(), down);
        activeItem = item;
        wasFixed = item.isFixed();
        item.setFixed(true);
    } //
    
    public void itemReleased(GraphItem item, MouseEvent e) {
        if ( !(item instanceof NodeItem) ) return;
        activeItem = null;
        item.setFixed(wasFixed);
    } //
    
    public void itemDragged(GraphItem item, MouseEvent e) {
        if ( !(item instanceof NodeItem) ) return;
        Display d = (Display)e.getComponent();
        tmp = d.getAbsoluteCoordinate(e.getPoint(), tmp);
        double dx = tmp.getX()-down.getX();
        double dy = tmp.getY()-down.getY();
        updateLocations((NodeItem)item, dx, dy);
        down.setLocation(tmp);
        if ( repaint ) {
            ItemRegistry registry = item.getItemRegistry();
            Iterator iter = registry.getDisplaysRef().iterator();
            while ( iter.hasNext() ) {
                ((Display)iter.next()).repaint();
            }
        }
    } //
    
    private void updateLocations(NodeItem n, double dx, double dy) {
        Point2D p = n.getLocation();
        n.updateLocation(p.getX()+dx,p.getY()+dy);
        n.setLocation(p.getX()+dx,p.getY()+dy);
        for ( int i=0; i<n.getNumChildren(); i++ )
            updateLocations(n.getChild(i),dx,dy);
    } //
    
} // end of class SubtreeDragControl
