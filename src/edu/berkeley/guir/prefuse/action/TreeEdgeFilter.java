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
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class TreeEdgeFilter extends AbstractAction {

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
		}
	} //

} // end of class TreeEdgeFilter
