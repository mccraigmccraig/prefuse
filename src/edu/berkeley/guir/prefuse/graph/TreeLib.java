package edu.berkeley.guir.prefuse.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import edu.berkeley.guir.prefuse.collections.BreadthFirstGraphIterator;
import edu.berkeley.guir.prefuse.collections.EdgeNodeComparator;

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
     * Helper method for index hunting.
     */
    public static int nearestIndex(TreeNode n, TreeNode p) {
        int idx = 0;
        for ( int i=0; i<n.getEdgeCount(); i++ ) {
            TreeNode c = (TreeNode)n.getNeighbor(i);
            if ( c == p )
                return idx;
            else if ( c.getParent() == n ) {
                idx++;
            }
        }
        return n.getChildCount();
        
//        int nidx = n.getIndex(p);
//        int idx  = n.getChildCount();
//        if ( n.getChildCount() > 0 ) {
//            for ( int i = 0; i<n.getChildCount(); i++ ) {
//                TreeNode c = (TreeNode)n.getChild(i);
//                if ( nidx < n.getIndex(c) ) {
//                    idx = i;
//                    break; 
//                }
//            }
//        }
//        return idx;
        
//        int nidx = n.getIndex(p);
//        int idx = 0;
//        for ( int i=0; i<n.getEdgeCount(); i++ ) {
//            TreeNode nn = (TreeNode)n.getNeighbor(i);
//            if ( nn.getParent() == n )
//                if ( nidx < i )
//                    return idx;
//                else
//                    idx++;
//        }
//        return idx;        
    } //
    
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
		
		r.setParentEdge(null);
		
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
		return new DefaultTree(r);
	} //
	
//	private static LinkedList tmpList1 = new LinkedList();
//	private static LinkedList tmpList2 = new LinkedList();
//	
//	/**
//	 * Swaps two nodes in a tree. Assumes both nodes are already
//	 * in the tree.
//	 * @param tree the <code>Tree</code> containing both nodes
//	 * @param o a node in the tree
//	 * @param n another node in the tree
//	 * @throws IllegalStateException if either of the nodes are not in the tree
//	 */
//	public static synchronized void swapNodes(Tree tree, TreeNode o, TreeNode n) {
//		// don't need to do anything if the two are the same
//		if ( o == n ) return;
//		
//		// make sure both nodes are in the tree
//		if ( !tree.contains(o) || !tree.contains(n) )
//			throw new IllegalStateException(
//				"Input nodes must be in the tree!");
//		
//		// first ensure ordering
//		if ( n.isDescendant(o) ) { TreeNode t = o; o = n; n = t; }
//		
//		TreeNode op = o.getParent();
//		TreeNode np = n.getParent();
//		int oidx = ( op != null ? op.getChildIndex(o) : -1 );
//		int nidx = ( np != null ? np.getChildIndex(n) : -1 );
//		if ( op != null ) { op.removeChild(o); }
//		if ( np != null ) { np.removeChild(n); }
//
//		while ( o.getChildCount() > 0 ) {
//			TreeNode c = (TreeNode)o.removeChild(0);
//			if ( c != n ) tmpList1.add(c);
//		}
//		while ( n.getChildCount() > 0 ) {
//			TreeNode c = (TreeNode)n.removeChild(0);
//			tmpList2.add(c);
//		}
//		Iterator iter = tmpList1.iterator();
//		while ( iter.hasNext() ) { n.addChild((TreeNode)iter.next()); }
//		iter = tmpList2.iterator();
//		while ( iter.hasNext() ) { o.addChild((TreeNode)iter.next()); }
//		
//		if ( oidx != -1 ) {
//			op.addChild(oidx, n);
//		}
//		if ( np != o && nidx != -1 ) { 
//			np.addChild(nidx, o);
//		} else if ( nidx != -1 ) {
//			n.addChild(nidx, o);
//		}
//		tmpList1.clear(); tmpList2.clear();
//		
//		if ( tree.getRoot() == o )
//			tree.changeRoot(n);
//	} //
	
	public static void sortTree(Tree tree, Comparator comp) {
		TreeNode root = tree.getRoot();
		sortHelper(root, new EdgeNodeComparator(comp));
	} //

	private static void sortHelper(TreeNode node, EdgeNodeComparator c) {
		ArrayList list = new ArrayList();
		Iterator iter = node.getChildren();
		while ( iter.hasNext() )
			list.add(iter.next());
        c.setIgnoredNode(node);
		Collections.sort(list, c);
		node.removeAllChildren();
		iter = list.iterator();
		while ( iter.hasNext() ) {
			Edge e = (Edge)iter.next();
			node.addChild(e);
			sortHelper((TreeNode)e.getAdjacentNode(node), c);
		}
	} //

} // end of class TreeLib
