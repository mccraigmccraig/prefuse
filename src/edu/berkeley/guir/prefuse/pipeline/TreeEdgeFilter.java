package edu.berkeley.guir.prefuse.pipeline;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
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
public class TreeEdgeFilter extends AbstractPipelineComponent implements Filter {

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		Iterator nodeIter = m_registry.getNodeItems();
		while ( nodeIter.hasNext() ) {
			NodeItem nitem  = (NodeItem)nodeIter.next();
			TreeNode node   = (TreeNode)m_registry.getEntity(nitem);
			TreeNode parent = node.getParent();
			
			while ( parent != null && !m_registry.isVisible(parent) )
				parent = parent.getParent();
			if ( parent == null ) continue;

			Edge edge = ( parent==node.getParent() ? 
					      parent.getEdge(node) : new Edge(parent, node) );
			EdgeItem e = m_registry.getEdgeItem(edge, true);
            NodeItem p = m_registry.getNodeItem(parent);
            p.addChild(e);
		}
	} //

} // end of class TreeEdgeFilter
