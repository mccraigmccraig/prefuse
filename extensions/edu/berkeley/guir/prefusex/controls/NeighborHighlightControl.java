package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * <p>
 * A ControlListener that sets the highlighted status (using the
 * {@link edu.berkeley.guir.prefuse.VisualItem#setHighlighted(boolean)
 * VisualItem.setHighlighted} method) for nodes neighboring the node 
 * currently under the mouse pointer. The highlight flag can then be used
 * by a color function to change node appearance as desired.
 * </p>
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class NeighborHighlightControl extends ControlAdapter {

    private Activity update = null;
    
    /**
     * Creates a new highlight control.
     */
    public NeighborHighlightControl() {
        this(null);
    } //
    
    /**
     * Creates a new highlight control that runs the given activity
     * whenever the neighbor highlight changes.
     * @param update the update Activity to run
     */
    public NeighborHighlightControl(Activity update) {
        this.update = update;
    } //
    
    public void itemEntered(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem )
            setNeighborHighlight((NodeItem)item, true);
    } //
    
    public void itemExited(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem )
            setNeighborHighlight((NodeItem)item, false);
    } //
    
    public void itemReleased(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem )
            setNeighborHighlight((NodeItem)item, false);
    } //
    
    public void setNeighborHighlight(NodeItem n, boolean state) {
        ItemRegistry registry = n.getItemRegistry();
        synchronized ( registry ) {
            Iterator iter = n.getEdges();
            while ( iter.hasNext() ) {
                EdgeItem eitem = (EdgeItem)iter.next();
                NodeItem nitem = (NodeItem)eitem.getAdjacentNode(n);
                eitem.setHighlighted(state);
                nitem.setHighlighted(state);
                registry.touch(nitem.getItemClass());
                registry.touch(eitem.getItemClass());
            }
        }
        if ( update != null )
            update.runNow();
    } //
    
} // end of class NeighborHighlightControl
