package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * Issues repaint requests to all displays tied to the given item registry.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class RepaintAction extends AbstractAction {

    /**
     * Calls repaint on all displays associated with the given ItemRegistry.
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
    public void run(ItemRegistry registry, double frac) {
        Iterator iter = registry.getDisplaysRef().iterator();
        while ( iter.hasNext() ) {
            ((Display)iter.next()).repaint();
        }
    } //

} // end of class RepaintAction
