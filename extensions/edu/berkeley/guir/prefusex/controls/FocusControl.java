package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Sets the current focus (according to the ItemRegistry's default focus
 * set) when an item is clicked.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FocusControl extends ControlAdapter {

    private int ccount;
    private Activity update = null;
    
    public FocusControl() {
        this(1);
    } //
    
    public FocusControl(int clicks) {
        ccount = clicks;
    } //
    
    public FocusControl(Activity update) {
        this.update = update;
    } //
    
    public void itemEntered(GraphItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    } //
    
    public void itemExited(GraphItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getDefaultCursor());
    } //
    
    public void itemClicked(GraphItem item, MouseEvent e) {
        if ( SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == ccount ) {
            ItemRegistry registry = item.getItemRegistry();
            registry.getDefaultFocusSet().set(item.getEntity());
            if ( update != null )
                ActivityManager.scheduleNow(update);
        }
    } //
    
} // end of class FocusControl
