/*
 * Created on Jul 14, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Filters out all nodes except the notch nodes.
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineGraphFilter extends GraphFilter implements TimelineConstants {
	// (( CONSTRUCTORS )) \\
	public TimelineGraphFilter() {
		super();
	}

	/**
	 * @param edgesVisible
	 */
	public TimelineGraphFilter(boolean edgesVisible) {
		super(edgesVisible);
	}

	
	// (( METHODS )) \\
	public void run(ItemRegistry registry, double frac) {
        Graph graph = registry.getGraph();
        // initialize filtered graph
        Graph fgraph = registry.getFilteredGraph();
        if ( fgraph instanceof DefaultGraph )
            ((DefaultGraph)fgraph).reinit(graph.isDirected());
        else
            fgraph = new DefaultGraph(graph.isDirected());
        
        // filter the nodes
        Iterator nodeIter = graph.getNodes();
        int i = 0;
        while ( nodeIter.hasNext() ) {
            final Node node = (Node)nodeIter.next();
            final NodeItem item;
            // XXX Gotta switch back later
/*            if (node.getAttribute(NODE_TYPE).equals(NOTCH_TYPE)) {
                item = (NodeItem) registry.getItem(NOTCH_NODE_TYPE, node, true);
            } else {
                item = (NodeItem) registry.getItem(NOTNOTCH_NODE_TYPE, node, true);
            }*/
            item = registry.getNodeItem(node, true);
            if (item.getAttribute(NODE_TYPE).equals(NOTCH_TYPE)) {
                fgraph.addNode(item);
                //System.out.println(i++);
            }
        }
        
        // process each node's edges
        nodeIter = fgraph.getNodes();
        while ( nodeIter.hasNext() ) {
            NodeItem item = (NodeItem)nodeIter.next();
            Node     node = (Node)item.getEntity();
            Iterator edgeIter = node.getEdges();
            while ( edgeIter.hasNext() ) {
                Edge edge = (Edge)edgeIter.next();
                Node n = edge.getAdjacentNode(node);
                // filter the edge
                EdgeItem eitem = registry.getEdgeItem(edge, true);
                fgraph.addEdge(eitem);
                if ( !m_edgesVisible ) eitem.setVisible(false);
            }
        }
        
        // update the registry's filtered graph
        registry.setFilteredGraph(fgraph);
        
        // optional garbage collection
        //super.run(registry, frac);
	}
	
	public static void main(String[] args) {
	}
}
