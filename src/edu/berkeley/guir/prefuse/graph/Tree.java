package edu.berkeley.guir.prefuse.graph;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import edu.berkeley.guir.prefuse.collections.BreadthFirstTreeIterator;
import edu.berkeley.guir.prefuse.collections.EdgeIterator;

/**
 * Class for representing a tree structure. A tree is an undirected graph
 * without any cycles. Furthermore, our tree implementation assumes some level
 * of orientation, distinguishing between parent and children nodes.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class Tree extends AbstractGraph {
	
	protected TreeNode m_root; 
	
	/**
	 * Constructor.
	 * @param root
	 */
	public Tree(TreeNode root) {
		m_root = root;
	} //
	
	public Tree() {
		m_root = null;
	} //

	/**
	 * Indicates if this graph is directed or undirected. Currently all
	 * trees are assumed to be undirected graphs.
	 */
	public boolean isDirected() {
		return false;
	} //

	/**
	 * Set a new root for the tree. This root should be a node <i>already</i>
	 * contained in the tree. This method allows the parent-child 
	 * relationships to be changed as necessary without changing the actual
	 * topology of the graph.
	 * @param root the new tree root. Should be contained in this tree.
	 */
	public void switchRoot(TreeNode root) {
		if ( !this.contains(root) ) {
			throw new IllegalArgumentException(
				"The new root must already be in the tree");
		}
		TreeNode n = root;
		LinkedList queue = new LinkedList();
		for ( TreeNode p = n; p != null; p = p.getParent() ) {
			queue.addFirst(p);
		}
		Iterator iter = queue.iterator();
		TreeNode p = (TreeNode)iter.next();
		while ( iter.hasNext() ) {
			TreeNode c = (TreeNode)iter.next();
			p.swapParent(c);			
			p = c;
		}
		m_root = root;
	} //
	
	public void setRoot(TreeNode root) {
		m_root = root;
	} //
	
	/**
	 * Returns the number of nodes in the tree.
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNumNodes()
	 */
	public int getNumNodes() {
		return ( m_root == null ? 0 : m_root.getNumDescendants() + 1 );
	} //
	
	/**
	 * Returns the number of edges in the tree. This is always the number of
	 * nodes minus one.
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNumEdges()
	 */
	public int getNumEdges() {
		return Math.max(0, getNumNodes()-1);
	} //

	/**
	 * Returns a breadth-first iteration of the tree nodes.
	 * @return Iterator
	 */
	public Iterator getNodes() {
		if ( m_root == null ) {
			return Collections.EMPTY_LIST.iterator();
		} else {
			return new BreadthFirstTreeIterator(m_root);
		}
	} //
	
	/**
	 * Returns the edges of the tree in breadth-first-order.
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getEdges()
	 */
	public Iterator getEdges() {
		return new EdgeIterator(this.getNodes());
	} //

	/**
	 * Returns the root node of the tree.
	 * @return TreeNode
	 */
	public TreeNode getRoot() {
		return m_root;
	} //
	
	/**
	 * Returns the depth of the given node in this tree
	 * Returns -1 if the node is not in this tree
	 * @return int
	 */
	public int getDepth(TreeNode n) {
		int depth = 0;
		TreeNode p = n;
		while ( p != m_root && p != null ) {
			depth++;
			p = p.getParent();
		}
		return ( p == null ? -1 : depth );
	}

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#contains(edu.berkeley.guir.prefuse.graph.Node)
	 */
	public boolean contains(Node n) {
		if ( n instanceof TreeNode ) {
			for ( TreeNode p = (TreeNode)n; p != null; p = p.getParent() )
				if ( p != null && p == m_root ) return true;
		}
		return false;
	} //

} // end of class Tree
