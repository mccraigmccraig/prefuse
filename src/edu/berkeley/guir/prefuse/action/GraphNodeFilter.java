package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Filters graph nodes, allowing all nodes in the graph to be visualized
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GraphNodeFilter extends AbstractAction {

	public void run(ItemRegistry registry, double frac) {
		Iterator nodeIter = registry.getGraph().getNodes();
		while ( nodeIter.hasNext() ) {
			Node node = (Node)nodeIter.next();
			NodeItem item = registry.getNodeItem(node, true);
		}
	} //

} // end of class GraphNodeFilter
