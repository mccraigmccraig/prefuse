package edu.berkeley.guir.prefuse.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import edu.berkeley.guir.prefuse.collections.BreadthFirstGraphIterator;

/**
 * Library of useful operations on trees.
 * 
 * May 21, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class TreeLib {

	/**
	 * Returns a Tree object, representing a breadth-first-search tree
	 * rooted at the specified node. This is one useful way of imposing
	 * a tree structure on a general graph. The only requirement is that
	 * the graph nodes are instances of the TreeNode class.
	 * @param r the root of the breadth-first-search.
	 * @return the breadth-first-search tree.
	 */
	public static Tree breadthFirstTree(TreeNode r) {
		if ( r == null ) { return null; }
		
		Iterator iter = new BreadthFirstGraphIterator(r);
		while ( iter.hasNext() ) {
			TreeNode n = (TreeNode)iter.next();
			n.removeAllAsChildren();
		}
		
		HashSet    visited = new HashSet();
		LinkedList queue   = new LinkedList();
		queue.add(r);
		visited.add(r);
		
		r.setParent(null);
		
		while ( !queue.isEmpty() ) {
			TreeNode n = (TreeNode)queue.removeFirst();		
			iter = n.getNeighbors();
			while ( iter.hasNext() ) {
				TreeNode c = (TreeNode)iter.next();
				if ( !visited.contains(c) ) {
					n.setAsChild(c);
					queue.add(c);
					visited.add(c);
				}
			}
		}
		return new Tree(r);
	} //
	
	/**
	 * Removes a node from a tree and replaces it with a new node. Requires
	 * that the replacement node does not have existing parent nodes.
	 * @param tree the <code>Tree</code> containing the old node
	 * @param old the <code>TreeNode</code> to remove from its tree
	 * @param n the <code>TreeNode</code> to replace it with.
	 * @throws IllegalStateException if <code>n</code> has a tree parent.
	 */
	public static void replaceNode(Tree tree, TreeNode old, TreeNode n) {
		if ( n.getParent() != null ) {
			throw new IllegalStateException
				("Argument node n must not have any existing parents!");	
		}
		TreeNode p = old.getParent();
		int idx = ( p != null ? p.getChildIndex(old) : -1 );
		while ( old.getNumChildren() > 0 ) {
			TreeNode c = old.removeChild(0);
			n.addChild(c);
		}
		if ( p != null ) {
			p.removeChild(old);
			p.addChild(idx,n);
		}
		if ( old == tree.getRoot() )
			tree.switchRoot(n);
	} //
	
	private static LinkedList tmpList1 = new LinkedList();
	private static LinkedList tmpList2 = new LinkedList();
	
	/**
	 * Swaps two nodes in a tree. Assumes both nodes are already
	 * in the tree.
	 * @param tree the <code>Tree</code> containing both nodes
	 * @param o a node in the tree
	 * @param n another node in the tree
	 * @throws IllegalStateException if either of the nodes are not in the tree
	 */
	public static synchronized void swapNodes(Tree tree, TreeNode o, TreeNode n) {
		// don't need to do anything if the two are the same
		if ( o == n ) return;
		
		// make sure both nodes are in the tree
		if ( !tree.contains(o) || !tree.contains(n) )
			throw new IllegalStateException(
				"Input nodes must be in the tree!");
		
		// first ensure ordering
		if ( n.isDescendant(o) ) { TreeNode t = o; o = n; n = t; }
		
		TreeNode op = o.getParent();
		TreeNode np = n.getParent();
		int oidx = ( op != null ? op.getChildIndex(o) : -1 );
		int nidx = ( np != null ? np.getChildIndex(n) : -1 );
		if ( op != null ) { op.removeChild(o); }
		if ( np != null ) { np.removeChild(n); }

		while ( o.getNumChildren() > 0 ) {
			TreeNode c = (TreeNode)o.removeChild(0);
			if ( c != n ) tmpList1.add(c);
		}
		while ( n.getNumChildren() > 0 ) {
			TreeNode c = (TreeNode)n.removeChild(0);
			tmpList2.add(c);
		}
		Iterator iter = tmpList1.iterator();
		while ( iter.hasNext() ) { n.addChild((TreeNode)iter.next()); }
		iter = tmpList2.iterator();
		while ( iter.hasNext() ) { o.addChild((TreeNode)iter.next()); }
		
		if ( oidx != -1 ) {
			op.addChild(oidx, n);
		}
		if ( np != o && nidx != -1 ) { 
			np.addChild(nidx, o);
		} else if ( nidx != -1 ) {
			n.addChild(nidx, o);
		}
		tmpList1.clear(); tmpList2.clear();
		
		if ( tree.getRoot() == o )
			tree.switchRoot(n);
	} //
	
	public static void sortTree(Tree tree, Comparator comp) {
		TreeNode root = tree.getRoot();
		sortHelper(root, comp);
	} //

	private static void sortHelper(TreeNode node, Comparator c) {
		ArrayList list = new ArrayList();
		Iterator enum = node.getChildren();
		while ( enum.hasNext() ) {
			list.add(enum.next());
		}
		Collections.sort(list, c);
		node.removeAllChildren();
		Iterator iter = list.iterator();
		while ( iter.hasNext() ) {
			TreeNode cnode = (TreeNode)iter.next();
			node.addChild(cnode);
			sortHelper(cnode, c);
		}
	} //

} // end of class TreeLib
