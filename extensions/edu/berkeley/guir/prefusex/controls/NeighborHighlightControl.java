package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * <p>
 * A ControlListener that sets the visualization attribute "highlight" to
 * true for nodes neighboring the node currently under the mouse pointer. The
 * "highlight" flag can then be used by a color function to change node
 * appearance as desired.
 * </p>
 * 
 * <p>
 * To test whether not an item has been highlighted, use the method call
 * {@link edu.berkeley.guir.prefuse.GraphItem#getVizAttribute(String)
 * item.getVizAttribute("highlight")}. The result will either be 
 * <code>null</code> or an object of type <code>Boolean</code> indicating
 * whether or not the item is highlighted (a value of <code>null</code>
 * indicates no highlighting).
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
    } //
    
    /**
     * Creates a new highlight control that runs the given activity
     * whenever the neighbor highlight changes.
     * @param update the update Activity to run
     */
    public NeighborHighlightControl(Activity update) {
        this.update = update;
    } //
    
    public void itemEntered(GraphItem item, MouseEvent e) {
        setNeighborHighlight((NodeItem)item, true);
    } //
    
    public void itemExited(GraphItem item, MouseEvent e) {
        setNeighborHighlight((NodeItem)item, false);
    } //
    
    public void itemReleased(GraphItem item, MouseEvent e) {
        setNeighborHighlight((NodeItem)item, false);
    } //
    
    public void setNeighborHighlight(NodeItem n, boolean state) {
        ItemRegistry registry = n.getItemRegistry();
        Boolean val = state ? Boolean.TRUE : null;
        synchronized ( registry ) {
            Iterator iter = n.getEdges();
            while ( iter.hasNext() ) {
                EdgeItem eitem = (EdgeItem)iter.next();
                NodeItem nitem = eitem.getAdjacentNode(n);
                eitem.setVizAttribute("highlight", val);
                nitem.setVizAttribute("highlight", val);
            }
        }
        if ( update != null )
            ActivityManager.scheduleNow(update);
    } //
    
} // end of class NeighborHighlightControl
