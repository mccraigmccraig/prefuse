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
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class GraphEdgeFilter extends AbstractAction {

    private boolean m_edgesVisible;
    
    /**
     * Filters graph edges, connecting filtered graph nodes into a
     * graph structure. Filtered edges are visible by default.
     */
    public GraphEdgeFilter() {
        this(true);
    } //
    
    /**
     * Filters graph edges, connecting filtered graph nodes into a
     * graph structure. Edge visibility can be controlled.
     * @param edgesVisible determines whether or not the filtered
     *  edges are visible in the display.
     */
    public GraphEdgeFilter(boolean edgesVisible) {
        m_edgesVisible = edgesVisible;
    } //
    
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
                    } catch ( IllegalStateException e ) { 
                        // already a neighbor
                    }
                    if ( !m_edgesVisible ) eitem.setVisible(false);
                }
			}
		}
	} //

} // end of class GraphEdgeFilter
