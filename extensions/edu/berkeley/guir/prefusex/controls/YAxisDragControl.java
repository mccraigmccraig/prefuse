/*
 * Created on Aug 7, 2004
 */
package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class YAxisDragControl extends DragControl {
	
	/* (non-Javadoc)
	 * @see edu.berkeley.guir.prefuse.event.ControlListener#itemDragged(edu.berkeley.guir.prefuse.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemDragged(final VisualItem item, final MouseEvent e) {
        if (!(item instanceof NodeItem)) return;
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        dragged = true;
        final Display d = (Display)e.getComponent();
        tmp = d.getAbsoluteCoordinate(e.getPoint(), tmp);
        final double dy = tmp.getY()-down.getY();
        final Point2D p = item.getLocation();
        item.updateLocation(p.getX(),p.getY()+dy);
        item.setLocation(p.getX(),p.getY()+dy);
        down.setLocation(tmp);
        if ( repaint )
            item.getItemRegistry().repaint();
        if ( update != null )
            update.runNow();
	}
}
