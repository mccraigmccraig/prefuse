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
    private final Object focusSetKey = FocusManager.SELECTION_KEY;
	private final ItemRegistry registry;
    
	
	// (( CONSTRUCTORS )) \\
    public MultiSelectFocusControl(final ItemRegistry registry) {
    	this.registry = registry;
    	registry.getFocusManager().putFocusSet(focusSetKey, new DefaultFocusSet());
    }
    
    
    // (( METHODS )) \\
    /**
     * Shift click adds the item to the focus set if not added;
     * else it removes the item
     */
    public void itemClicked(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem && 
             SwingUtilities.isLeftMouseButton(e) &&
			 e.isShiftDown())
        {
            final FocusSet focusSet = registry.getFocusManager().getFocusSet(focusSetKey);
            final Entity node = item.getEntity();
			if (focusSet.contains(node)) {
            	focusSet.remove(node);
            } else {
            	focusSet.add(node);
            }
            registry.touch(item.getItemClass());
        }
    } //

    /**
     * Clear the focus
     */
	public void mouseClicked(MouseEvent e) {
		registry.getFocusManager().getFocusSet(focusSetKey).clear();
	}
}
