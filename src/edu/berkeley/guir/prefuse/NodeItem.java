package edu.berkeley.guir.prefuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Represents a node in the graph to be displayed.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class NodeItem extends GraphItem {
		
	/**
	 * Initialize this NodeItem, binding it to the given
	 * ItemRegistry and Entity.
	 * @param registry the ItemRegistry monitoring this GraphItem
	 * @param entity the Entity represented by this GraphItem
	 */	
	public void init(ItemRegistry registry, String itemClass, Entity entity) {
		if ( entity != null && !(entity instanceof Node) ) {
			throw new IllegalArgumentException("NodeItem can only represent an Entity of type Node.");				
		}
		super.init(registry, itemClass, entity);
			
		/// XXX TODO: this is ugly. fix this later.
		if ( entity instanceof TreeNode ) 
			setLocationToParent((TreeNode)entity, this);
	} //
	
	/**
	 * XXX TODO: find somewhere to put this such that things are clean!
	 * @param node
	 * @param item
	 */
	private void setLocationToParent(TreeNode node, GraphItem item) {
		TreeNode parent = node.getParent();
		if ( parent == null ) { return; }
		NodeItem pitem  = m_registry.getNodeItem(parent);
		if ( pitem == null ) { return; }
		item.setLocation(pitem.getX(), pitem.getY());
	} //
	
	public void clear() {
		super.clear();
		if ( m_children != null )
			m_children.clear();
	}
	
	// ========================================================================
	
	private List m_edges = new ArrayList();
	private List m_neighbors = new ArrayList();
	private List m_children;
	private NodeItem m_parent;
	
	public void removeAllNeighbors() {
		if ( m_children != null )  m_children.clear();
		m_neighbors.clear();
		m_edges.clear();
		m_parent = null;
	} //
	
	public int getDepth() {
		int d = 0;
		NodeItem item = this;
		while ( (item=item.getParent()) != null )
			d++;
		return d;
	} //
	
	public boolean isSibling(NodeItem n) {
		return ( this != n && this.getParent() == n.getParent() );
	} //
	
	/**
	 * Returns the i'th neighbor of this node.
	 * @param i the index of the neighbor in the neighbor list.
	 * @return Node the Node at the specified position in the list of
	 *  neighbors
	 */
	public NodeItem getNeighbor(int i) {
		return (NodeItem)m_neighbors.get(i);
	} //

	/**
	 * Indicates if a given node is a neighbor of this one.
	 * @param n the node to check as a neighbor
	 * @return true if the node is a neighbor, false otherwise
	 */
	public boolean isNeighbor(NodeItem n) {
		return ( getNeighborIndex(n) > -1 );
	} //

	/**
	 * Returns the index, or position, of a neighbor node. Returns -1 if the
	 * input node is not a neighbor of this node.
	 * @param n the node to find the index of
	 * @return the node index, or -1 if this node is not a neighbor
	 */
	public int getNeighborIndex(NodeItem n) {
		return m_neighbors.indexOf(n);
	} //

	/**
	 * Return the total number of neighbors of this node.
	 * @return the number of neighbors
	 */
	public int getNumNeighbors() {
		return m_neighbors.size();
	} //

	/**
	 * Remove the neighbor node at the specified index.
	 * @param i the index at which to remove a node
	 */
	public NodeItem removeNeighbor(int i) {
		EdgeItem e = (EdgeItem)m_edges.remove(i);
		return (NodeItem)m_neighbors.remove(i);
	} //

    public Iterator getNeighbors() {
        return m_neighbors.iterator();
    } //
    
	public Iterator getEdges() {
		return m_edges.iterator();
	} //
	
	public EdgeItem getEdge(NodeItem n) {
		return (EdgeItem)m_edges.get(m_neighbors.indexOf(n));
	} //
	
	public EdgeItem getEdge(int i) {
		return (EdgeItem)m_edges.get(i);
	} //
	
	public boolean isIncidentEdge(EdgeItem e) {
		return ( m_edges.indexOf(e) > -1 );
	} //
	
	public int getEdgeIndex(EdgeItem e) {
		return m_edges.indexOf(e);
	} //
	
	public int getNumEdges() {
		return m_edges.size();
	} //
	
	public void addEdge(EdgeItem e) {		
		addEdge(m_edges.size(), e);
	} //
	
	public void addEdge(int i, EdgeItem e) {
		NodeItem n1 = e.getFirstNode();
		NodeItem n2 = e.getSecondNode();
		if ( !e.isDirected() && n2 == this ) {
			NodeItem tmp = n1; n1 = n2; n2 = tmp;
		}
		if ( n1 != this ) {
			throw new IllegalArgumentException(
			"Edge must be incident on this Node!");
		}
		if ( isIncidentEdge(e) || isNeighbor(n2) )
			throw new IllegalStateException("Node is already a neighbor!");		
		m_edges.add(i,e);
		m_neighbors.add(i,n2);		
	} //
	
	public boolean removeEdge(EdgeItem e) {
		return ( removeEdge(m_edges.indexOf(e)) != null );
	} //
	
	public EdgeItem removeEdge(int i) {
		m_neighbors.remove(i);
		return (EdgeItem)m_edges.remove(i);
	} //
	
	
	// -- tree routines -------------------------------------------------------
	
	public int getNumChildren() {
		return ( m_children == null ? 0 : m_children.size() );
	} //
	
	public Iterator getChildren() {
		if ( m_children != null )
			return m_children.iterator();
		else
			return Collections.EMPTY_LIST.iterator();
	} //
	
	public NodeItem getChild(int idx) {
		if ( m_children == null )
			throw new IndexOutOfBoundsException();
		return (NodeItem)m_children.get(idx);
	} //
	
	public int getChildIndex(NodeItem child) {
		return m_children==null ? -1 : m_children.indexOf(child);
	} //

	public void addChild(EdgeItem e) {
		int i = ( m_children == null ? 0 : m_children.size() );
		addChild(i,e);
	} //	

	/**
	 * Inserts a new child at the specified location in this node's child list.
	 * @param i
	 * @param c
	 */
	public void addChild(int i, EdgeItem e) {
		NodeItem n1 = e.getFirstNode();
		NodeItem n2 = e.getSecondNode();
		if ( e.isDirected() || !(n1 != this ^ n2 != this) )
			throw new IllegalArgumentException("Not a valid Edge!");
		NodeItem c = ( n1 == this ? n2 : n1 );
		if ( getChildIndex(c) > -1 || getNeighborIndex(c) > -1 )
			throw new IllegalStateException("Node is already a child!");
		if ( m_children == null )
			m_children = new ArrayList();
		
		int idx = ( i > 0 ? getNeighborIndex(getChild(i-1))+1 : 0 );
		addEdge(idx,e);
		m_children.add(i, c);
		
		c.addEdge(e);
		c.setParent(this);
	} //
	
	public void removeChild(int idx) {
		GraphItem item = (NodeItem)m_children.remove(idx);
		if ( item instanceof NodeItem )
			((NodeItem)item).setParent(null);
	} //
	
	public void removeAllChildren() {
		if ( m_children == null ) return;
		while ( m_children.size() > 0 ) {
			NodeItem item = (NodeItem)m_children.remove(m_children.size()-1);
			item.setParent(null);
		}
	} //
	
	public NodeItem getParent() {
		return m_parent;
	} //
	
	public void setParent(NodeItem item) {
		m_parent = item;
	} //
	
} // end of class NodeItem
