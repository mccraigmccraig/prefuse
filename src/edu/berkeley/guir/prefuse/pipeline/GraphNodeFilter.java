package edu.berkeley.guir.prefuse.pipeline;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Filters graph nodes, allowing all nodes in the graph to be visualized
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GraphNodeFilter extends AbstractPipelineComponent implements Filter {

	public void process() {
		Iterator nodeIter = m_graph.getNodes();
		while ( nodeIter.hasNext() ) {
			Node node = (Node)nodeIter.next();
			m_registry.getNodeItem(node, true);
		}
	} //

} // end of class GraphNodeFilter
