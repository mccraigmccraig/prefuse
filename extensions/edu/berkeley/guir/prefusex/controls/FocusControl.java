package edu.berkeley.guir.prefusex.controls;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.util.FocusSet;

/**
 * Sets the current focus (according to the ItemRegistry's default focus
 * set) in response to mouse actions. This does not necessarily cause the
 * display to change. For this functionality, use a 
 * {@link edu.berkeley.guir.prefuse.event.FocusListener FocusListener} 
 * to drive display updates when the focus changes.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FocusControl extends ControlAdapter {

    private Object focusSetKey = FocusManager.DEFAULT_KEY;
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
     * clicked the specified number of times. A click value of zero indicates
     * that the focus shoudl be changed in response to mouse-over events.
     * @param clicks the number of clicks needed to switch the focus.
     */
    public FocusControl(int clicks) {
        ccount = clicks;
    } //
    
    /**
     * Creates a new FocusControl that changes the focus when an item is 
     * clicked the specified number of times. A click value of zero indicates
     * that the focus shoudl be changed in response to mouse-over events.
     * @param clicks the number of clicks needed to switch the focus.
     * @param focusSetKey the key corresponding to the focus set to use
     */
    public FocusControl(int clicks, Object focusSetKey) {
        ccount = clicks;
        this.focusSetKey = focusSetKey;
    } //
    
    public void itemEntered(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem ) {
            Display d = (Display)e.getSource();
            d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if ( ccount == 0 ) {
                FocusManager fm = item.getItemRegistry().getFocusManager();
                FocusSet fs = fm.getFocusSet(focusSetKey);
                fs.set(item.getEntity());
            }
        }
    } //
    
    public void itemExited(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem ) {
            Display d = (Display)e.getSource();
            d.setCursor(Cursor.getDefaultCursor());
            if ( ccount == 0 ) {
                FocusManager fm = item.getItemRegistry().getFocusManager();
                FocusSet fs = fm.getFocusSet(focusSetKey);
                fs.remove(item.getEntity());
            }
        }
    } //
    
    public void itemClicked(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem && ccount > 0 && 
             SwingUtilities.isLeftMouseButton(e)    && 
             e.getClickCount() == ccount )
        {
            FocusManager fm = item.getItemRegistry().getFocusManager();
            FocusSet fs = fm.getFocusSet(focusSetKey);
            fs.set(item.getEntity());
        }
    } //
    
} // end of class FocusControl
