/*
 * Created on Aug 19, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.event.ControlListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.util.FocusSet;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class MultiItemClickControl extends ControlAdapter implements
		ControlListener {
	// CONSTANTS------
    private static final Object FOCUS_KEY = FocusManager.SELECTION_KEY;
	
    
	// METHODS--------
	/**
	 * Clicking on a selected item brings up comparison pane; for a non-selected
	 * item, focus changes to that item
	 */
	public void itemClicked(VisualItem item, MouseEvent e) {
		if (item instanceof NodeItem && SwingUtilities.isLeftMouseButton(e)) {
			final ItemRegistry registry = item.getItemRegistry();
			final FocusManager focusManager = registry.getFocusManager();
			final FocusSet focusSet = focusManager.getFocusSet(FOCUS_KEY);
            final Entity node = item.getEntity(); // need dataitem, not visitem
			if (focusManager.isFocus(FOCUS_KEY, node)) {
				System.out.println("a selected item has been clicked"+item);
				// bring up comparison pane
				//addComparisonPane(focusSet);
			} else {
				focusSet.set(node);
			}
			registry.touch(item.getItemClass());
		}
	}
}
