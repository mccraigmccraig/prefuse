package edu.berkeley.guir.prefuse.graph;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.berkeley.guir.prefuse.collections.WrapAroundIterator;

/**
 * Represents a node in a tree. This implementation supports imposing
 * tree structures on arbitrary graphs, thus supports both a list
 * of neighbor nodes and a list of child nodes, where the child list must
 * be a subset of the neighbor list. Accordingly, this class supports both
 * normal methods for adding and removing children nodes as well as
 * <i>neighbor-invariant</i> methods, that allow parent-child relations to
 * be changed without changing the underlying neighbor edges. 
 * 
 * These functionalities are particularly useful for using tree layout methods
 * to visualize more complex graph structures.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class TreeNode extends Node {

	protected List     m_children;
	protected TreeNode m_parent;
	protected int      m_numDescendants;
	
	// ========================================================================
	// == CONSTRUCTOR(S) ======================================================
	
	/**
	 * Constructor. Creates a new, empty TreeNode.
	 */
	public TreeNode() {
		m_numDescendants = 0;
		m_parent = null;
	} //
	
	/**
	 * Constructor, creates a new node, and makes it the child of the
	 * specified input node.
	 * @param parent the parent TreeNode
	 */
	public TreeNode(TreeNode parent) {
		m_numDescendants = 0;
		parent.addChild(this);
	} //
	
	/**
	 * Create a new tree node that uses the same internals as the
	 * given node.
	 * @param node
	 */
	public TreeNode(Node node) {
		 this();
		 this.m_neighbors  = node.m_neighbors;
		 this.m_attributes = node.m_attributes;
	} //
	
	public static TreeNode copy(TreeNode node) {
		TreeNode copy = new TreeNode();
		copy.m_attributes = node.m_attributes;
		return copy;
	} //
	
	// ========================================================================
	// == ACCESSOR METHODS ====================================================

	/**
	 * Returns true if the argument node is a child of this one.
	 */
	public boolean isChild(TreeNode n) {
		return ( getChildIndex(n) >= 0 );
	} //

	/**
	 * Returns true if the argument node is a descendant of this one.
	 * Nodes are considered descendants of themselves (i.e.
	 * <tt>n.isDescendant(n)</tt> will always be true).
	 * @param n
	 * @return true if the givn node is a descendant, false otherwise
	 */
	public boolean isDescendant(TreeNode n) {
		while ( n != null ) {
			if ( this == n ) { 
				return true;
			} else {
				n = n.getParent();
			}
		}
		return false;
	} //

	public boolean isSibling(TreeNode n) {
		return ( this != n && this.getParent() == n.getParent() );
	} //

	public TreeNode getNextSibling() {
		int idx = m_parent.getChildIndex(this);
		if ( m_parent.getNumChildren() == idx+1 )
			return null;
		else
			return m_parent.getChild(idx+1);
	} //
	
	public TreeNode getPreviousSibling() {
		int idx = m_parent.getChildIndex(this);
		return ( idx == 0 ? null : m_parent.getChild(idx-1) );		
	} //

	/**
	 * Returns an iterator over this node's children
	 * @return Iterator
	 */
	public Iterator getChildren() {
		if ( m_children == null || m_children.size() == 0 ) {
			return Collections.EMPTY_LIST.iterator();	
		} else {
			int pidx = ( m_parent == null ? 0 : 
							nearestIndex(this, m_parent) % m_children.size() );
			return new WrapAroundIterator(m_children, pidx);
		}
	} //

	/**
	 * Returns the i'th child of this node.
	 * @param i
	 * @return TreeNode
	 */
	public TreeNode getChild(int i) {
		if ( m_children == null || i < 0 || i >= m_children.size() ) {
			throw new IndexOutOfBoundsException();
		} else {
			return (TreeNode)m_children.get(i);
		}
	} //
	
	/**
	 * Returns the index, or position, of a child node. Returns -1 if the
	 * input node is not a child of this node.
	 * @param c
	 * @return int
	 */
	public int getChildIndex(TreeNode c) {
		return ( m_children == null ? -1 : m_children.indexOf(c) );
	} //
	
	/**
	 * Returns the tree parent of this node, or null if the node has no parent
	 * @return TreeNode
	 */
	public TreeNode getParent() {
		return m_parent;
	} //

	/**
	 * Returns the number of children this node has
	 * @return int
	 */
	public int getNumChildren() {
		return ( m_children == null ? 0 : m_children.size() );
	} //
	
	/**
	 * Returns the number of descendants this node has
	 * @return int
	 */
	public int getNumDescendants() {
		return m_numDescendants;
	} //
	
	// ========================================================================
	// == MUTATOR METHODS =====================================================

	/**
	 * Helper method for index hunting.
	 */
	private static int nearestIndex(TreeNode p, TreeNode n) {
		int nidx = p.getNeighborIndex(n);
		int idx  = p.getNumChildren();
		if ( p.getNumChildren() > 0 ) {
			Iterator iter = p.m_children.iterator();
			for ( int i = 0; iter.hasNext(); i++ ) {
				TreeNode c = (TreeNode)iter.next();
				if ( nidx < p.getNeighborIndex(c) ) {
					idx = i;
					break; 
				}
			}
		}
		return idx;
	} //

	/**
	 * Sets the parent of this node. Made package visible to help prevent
	 * violations of tree invariants (e.g. proper numDescendants counts).
	 */
	void setParent(TreeNode p) {
		m_parent = p;
	} //

	/**
	 * Swaps the parent of this node. The new parent must be a child of
	 * this node. Made package visible to help prevent
	 * violations of tree invariants (e.g. proper numDescendants counts).
	 */
	void swapParent(TreeNode c) {
		removeAsChild(c);
		
		//// now add child to new parent
		setParent(c);
		
		// find the right index at which to insert the child
		int idx = nearestIndex(c, this);
		
		c.setAsChild(idx, this);
	} //

	/**
	 * Set the number of descendants of this node. Made package visible to help
	 * prevent violations of proper descendant counts.
	 * @param n
	 */
	void setNumDescendants(int n) {
		this.m_numDescendants = n;
	} //

	/**
	 * Appends a child node to the given node.
	 * @param c
	 */
	public void addChild(TreeNode c) {
		int i = ( m_children == null ? 0 : m_children.size() );
		addChild(i,c);
	} //

	/**
	 * Inserts a new child at the specified location in this node's child list.
	 * @param i
	 * @param c
	 */
	public void addChild(int i, TreeNode c) {
		if ( getChildIndex(c) > -1 || getNeighborIndex(c) > -1 ) {
			throw new IllegalStateException("Node is already a child!");
		}
		if ( m_children == null ) {
			try {
				m_children = (List)LIST_TYPE.newInstance();
			} catch ( Exception e ) { }
		}
		
		int idx = ( i > 0 ? getNeighborIndex(getChild(i-1))+1 : 0 );
		super.addNeighbor(idx,c);
		m_children.add(i, c);
		
		c.addNeighbor(this);
		c.setParent(this);
		
		int descendantDelta = 1 + c.getNumDescendants();
		for ( TreeNode p = this; p != null; p = p.getParent() ) {		
			p.m_numDescendants += descendantDelta;
		}
	} //
	
	public void addChild(Edge e) {
		int i = ( m_children == null ? 0 : m_children.size() );
		addChild(i,e);
	} //	

	/**
	 * Inserts a new child at the specified location in this node's child list.
	 * @param i the index at which to add the child
	 * @param e the Edge to the child
	 */
	public void addChild(int i, Edge e) {
		Entity n1 = (Entity)e.getFirstNode();
		Entity n2 = (Entity)e.getSecondNode();
		if ( e.isDirected() || !(n1 instanceof TreeNode) ||
			 !(n2 instanceof TreeNode) || !(n1 != this ^ n2 != this) )
		{
			throw new IllegalArgumentException("Not a valid Edge!");
		}
		TreeNode c = (TreeNode)( n1 == this ? n2 : n1 );
		if ( getChildIndex(c) > -1 || getNeighborIndex(c) > -1 ) {
			throw new IllegalStateException("Node is already a child!");
		}
		if ( m_children == null ) {
			try {
				m_children = (List)LIST_TYPE.newInstance();
			} catch ( Exception ex ) { }
		}
		
		int idx = ( i > 0 ? getNeighborIndex(getChild(i-1))+1 : 0 );
		super.addEdge(idx,e);
		m_children.add(i, c);
		
		c.addEdge(e);
		c.setParent(this);
		
		int descendantDelta = 1 + c.getNumDescendants();
		for ( TreeNode p = this; p != null; p = p.getParent() ) {		
			p.m_numDescendants += descendantDelta;
		}
	} //
	
	/**
	 * Remove the given node as a child of this node.
	 * @param n
	 */
	public boolean removeChild(TreeNode n) {
		return ( removeChild(getChildIndex(n)) != null );	
	} //
	
	/**
	 * Remove the child node at the specified index.
	 * @param i
	 */
	public TreeNode removeChild(int i) {
		if ( i < 0 || i >= m_children.size() ) {
			throw new IndexOutOfBoundsException();
		}
		
		TreeNode c = (TreeNode)m_children.remove(i);
		super.removeNeighbor(c);
		
		c.removeNeighbor(this);
		c.setParent(null);		
		
		int descendantDelta = 1 + c.getNumDescendants();
		for ( TreeNode p = this; p != null; p = p.getParent() ) {
			p.m_numDescendants -= descendantDelta;
		}
		return c;
	} //
	
	/**
	 * Remove all children nodes.
	 */
	public void removeAllChildren() {
		if ( m_children == null ) { return;	}
		Iterator iter = m_children.iterator();
		while ( iter.hasNext() ) {
			TreeNode c = (TreeNode)iter.next();
			c.setParent(null);
			removeNeighbor(c);
			c.removeNeighbor(this);
		}
		m_children.clear();
		
		int descendantDelta = this.m_numDescendants;
		for ( TreeNode p = this; p != null; p = p.getParent() ) {
			p.m_numDescendants -= descendantDelta;
		}
	} //
	
	//// -- neighbor-invariant children routines ------------------------------
	
	/**
	 * Add the given node (which should already be a neighbor!) as a
	 * child of this node in the tree structure.
	 * @param c the node to add as a child
	 * @throws IllegalStateException if the node is already a 
	 * child or is not a neighbor
	 */
	public void setAsChild(TreeNode c) {
		int i = ( m_children == null ? 0 : m_children.size() );
		setAsChild(i,c);
	} //
	
	/**
	 * Add the given node (which should already be a neighbor!) as a
	 * child of this node in the tree structure.
	 * @param i the index in the list of children at which to add the node
	 * @param c the node to add as a child
	 * @throws IllegalStateException if the node is already a 
	 * child or is not a neighbor
	 */
	public void setAsChild(int i, TreeNode c) {
		int idx;
		if ( getChildIndex(c) > -1 || (idx=getNeighborIndex(c)) < 0 ) {
			throw new IllegalStateException("Node is already a child or is not a neighbor!");
		}
		int size = ( m_children == null ? 0 : m_children.size() );
		if ( i < 0 || i > size ) {
			throw new IndexOutOfBoundsException();
		}
		if ( m_children == null ) {
			try {
				m_children = (List)LIST_TYPE.newInstance();
			} catch ( Exception e ) { }
		}
		m_children.add(i, c);
		c.setParent(this);
		
		int descendantDelta = 1 + c.getNumDescendants();
		for ( TreeNode p = this; p != null; p = p.getParent() ) {		
			p.m_numDescendants += descendantDelta;
		}
	} //
	
	/**
	 * Removes a node as a child in the tree, but leave it as a neighbor node.
	 * @param n
	 * @return true if the node was removed, false otherwise
	 */
	public boolean removeAsChild(TreeNode n) {
		return ( removeAsChild(getChildIndex(n)) != null );
	} //
	
	/**
	 * Removes a node as a child in the tree, but leave it as a neighbor node.
	 * @param i the index of the node to remove in the list of children
	 * @return the removed child TreeNode
	 */
	public TreeNode removeAsChild(int i) {
		if ( i < 0 || i >=  m_children.size() ) {
			throw new IndexOutOfBoundsException("Input out of range");
		}
		TreeNode c = (TreeNode)m_children.remove(i);
		c.setParent(null);

		int descendantDelta = 1 + c.getNumDescendants();
		for ( TreeNode p = this; p != null; p = p.getParent() ) {
			p.m_numDescendants -= descendantDelta;
		}
		return c;
	} //
	
	/**
	 * Removes all children nodes as children of this node, but the nodes will
	 * remain neighbor nodes of this one.
	 */
	public void removeAllAsChildren() {
		if ( m_children == null ) { return;	}
		Iterator iter = m_children.iterator();
		while ( iter.hasNext() ) {
			TreeNode c = (TreeNode)iter.next();
			c.setParent(null);
		}
		m_children.clear();
		int descendantDelta = m_numDescendants;
		for ( TreeNode p = this; p != null; p = p.getParent() ) {
			p.m_numDescendants -= descendantDelta;
		}
	} //
	
} // end of class TreeNode
