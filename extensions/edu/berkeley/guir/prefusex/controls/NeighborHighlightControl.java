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
 * A ControlListener that sets the visualization attribute "highlight" to
 * true for nodes neighboring the node currently under the mouse pointer. The
 * "highlight" flag can then be used by a color function to change node
 * appearance as desired.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class NeighborHighlightControl extends ControlAdapter {

    private Activity update = null;
    
    public NeighborHighlightControl() {
    } //
    
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
