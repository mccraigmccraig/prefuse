package edu.berkeley.guir.prefuse.pipeline;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Filters nodes on a tree.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class TreeNodeFilter extends AbstractPipelineComponent implements Filter {

	public void process() {
		Tree t = (Tree)m_graph;
		Iterator iter = t.getNodes();
		while ( iter.hasNext() ) {
			TreeNode cnode = (TreeNode)iter.next();
			NodeItem citem = m_registry.getNodeItem(cnode, true);
		}		
	} //

} // end of class TreeNodeFilter
