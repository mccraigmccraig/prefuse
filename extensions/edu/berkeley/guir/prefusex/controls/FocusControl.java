package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Sets the current focus (according to the ItemRegistry's default focus
 * set) when an item is clicked. This does not necessarily cause the
 * display to change. For this functionality, use a 
 * {@link edu.berkeley.guir.prefuse.event.FocusListener FocusListener} 
 * to drive display updates when the focus changes.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FocusControl extends ControlAdapter {

    private int ccount;
    
    /**
     * Creates a new FocusControl that changes the focus to another item
     * when that item is clicked once.
     */
    public FocusControl() {
        this(1);
    } //
    
    /**
     * Creates a new FocusControl that changes the focus when an item is 
     * clicked the specified number of times.
     * @param clicks the number of clicks needed to switch the focus.
     */
    public FocusControl(int clicks) {
        ccount = clicks;
    } //
    
    public void itemEntered(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem ) {
            Display d = (Display)e.getSource();
            d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    } //
    
    public void itemExited(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem ) {
            Display d = (Display)e.getSource();
            d.setCursor(Cursor.getDefaultCursor());
        }
    } //
    
    public void itemClicked(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem && SwingUtilities.isLeftMouseButton(e) 
                && e.getClickCount() == ccount )
        {
            ItemRegistry registry = item.getItemRegistry();
            registry.getDefaultFocusSet().set(item.getEntity());
        }
    } //
    
} // end of class FocusControl
