package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * The TreeEdgeFilter determines which edges to visualize based on the nodes
 *  selected for visualization and the underlying tree structure.
 * 
 * Apr 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class TreeEdgeFilter extends AbstractAction {
    
    private boolean m_edgesVisible;
    
    /**
     * Filters tree edges, connecting filtered graph nodes into a
     * tree structure. Filtered edges are visible by default.
     */
    public TreeEdgeFilter() {
        this(true);
    } //
    
    /**
     * Filters tree edges, connecting filtered graph nodes into a
     * tree structure. Edge visibility can be controlled.
     * @param edgesVisible determines whether or not the filtered
     *  edges are visible in the display.
     */
    public TreeEdgeFilter(boolean edgesVisible) {
        m_edgesVisible = edgesVisible;
    } //
    
	public void run(ItemRegistry registry, double frac) {
		Iterator nodeIter = registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			NodeItem nitem  = (NodeItem)nodeIter.next();
			TreeNode node   = (TreeNode)registry.getEntity(nitem);
			TreeNode parent = node.getParent();
			
			while ( parent != null && !registry.isVisible(parent) )
				parent = parent.getParent();
			if ( parent == null ) continue;

			Edge edge = ( parent==node.getParent() ? 
					      parent.getEdge(node) : new Edge(parent, node) );
			EdgeItem e = registry.getEdgeItem(edge, true);
            NodeItem p = registry.getNodeItem(parent);
            p.addChild(e);
            if ( !m_edgesVisible ) e.setVisible(false);
		}
	} //

} // end of class TreeEdgeFilter
