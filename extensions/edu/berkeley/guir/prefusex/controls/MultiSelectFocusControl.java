/*
 * Created on Aug 11, 2004
 */
package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.util.DefaultFocusSet;
import edu.berkeley.guir.prefuse.util.FocusSet;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class MultiSelectFocusControl extends ControlAdapter {
	// (( CONSTANTS )) \\
    private static final Object FOCUS_KEY = FocusManager.SELECTION_KEY;
	private final ItemRegistry registry; // needed for clearing focusSet on mouseClicked
    
	
	// (( CONSTRUCTORS )) \\
    public MultiSelectFocusControl(final ItemRegistry registry) {
    	this.registry = registry;
    	registry.getFocusManager().putFocusSet(FOCUS_KEY, new DefaultFocusSet());
    }
    
    
    // (( METHODS )) \\
    /**
     * Shift click adds the item to the focus set if not added;
     * else it removes the item
     */
    public void itemClicked(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem && 
             SwingUtilities.isLeftMouseButton(e))
        {
        	final FocusManager focusManager = registry.getFocusManager();
            final FocusSet focusSet = focusManager.getFocusSet(FOCUS_KEY);
            final Entity node = item.getEntity();
            
            if (e.isShiftDown()) { // mode: adding to/removing from focus set
				if (focusSet.contains(node)) {
					focusSet.remove(node);
				} else {
					focusSet.add(node);
				}
			} else { // mode: doing something cool/resetting focus
				if (focusManager.isFocus(FOCUS_KEY, node)) {
					System.out.println("a selected item has been clicked"+item);
					// bring up comparison pane
					//addComparisonPane(focusSet);
				} else {
					focusSet.set(node);
				}
			}
            registry.touch(item.getItemClass());
        }
    } //

    /**
     * Clear the focus
     */
	public void mouseClicked(MouseEvent e) {
		registry.getFocusManager().getFocusSet(FOCUS_KEY).clear();
	}
}
