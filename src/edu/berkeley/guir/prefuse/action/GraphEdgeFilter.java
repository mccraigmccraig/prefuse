package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * The GraphEdgeFilter allows all edges adjacent to visualized 
 * nodes to be visualized.
 * 
 * Apr 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GraphEdgeFilter extends AbstractAction {

	/**
	 * @see edu.berkeley.guir.prefuse.filter.AbstractPipelineComponent#process()
	 */
	public void run(ItemRegistry registry, double frac) {
		Iterator nodeIter = registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			NodeItem nitem  = (NodeItem)nodeIter.next();
			Node node = (Node)nitem.getEntity();
			Iterator edgeIter = node.getEdges();
			while ( edgeIter.hasNext() ) {
				Edge edge = (Edge)edgeIter.next();
                Node n = (Node)edge.getFirstNode();
                if ( n == node )
                    n = (Node)edge.getSecondNode();
                if ( registry.isVisible(n) ) {
                    EdgeItem eitem = registry.getEdgeItem(edge, true);
                    try {
                        nitem.addEdge(eitem);
                    } catch ( IllegalStateException e ) {}
                }
			}
		}
	} //

} // end of class GraphEdgeFilter
