package edu.berkeley.guir.prefuse.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a node in a graph.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class Node extends AbstractEntity {
	
	// The type of list instance used to store children.
	protected static final Class LIST_TYPE = ArrayList.class;
	
	protected List m_neighbors;
	protected List m_edges;
	
	/**
	 * Default constructor. Creates a new node.
	 */
	public Node() {
		try {
			m_neighbors = (List)LIST_TYPE.newInstance();
			m_edges     = (List)LIST_TYPE.newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //
	
	/**
	 * Returns an iterator over all neighbor nodes of this node.
	 * @return an iterator over this node's neighbors.
	 */
	public Iterator getNeighbors() {
		return m_neighbors.iterator();
	} //
	
	/**
	 * Returns the i'th neighbor of this node.
	 * @param i the index of the neighbor in the neighbor list.
	 * @return Node the Node at the specified position in the list of
	 *  neighbors
	 */
	public Node getNeighbor(int i) {
		return (Node)m_neighbors.get(i);
	} //

	/**
	 * Indicates if a given node is a neighbor of this one.
	 * @param n the node to check as a neighbor
	 * @return true if the node is a neighbor, false otherwise
	 */
	public boolean isNeighbor(Node n) {
		return ( m_neighbors.indexOf(n) > -1 );
	} //

	/**
	 * Returns the index, or position, of a neighbor node. Returns -1 if the
	 * input node is not a neighbor of this node.
	 * @param n the node to find the index of
	 * @return the node index, or -1 if this node is not a neighbor
	 */
	public int getNeighborIndex(Node n) {
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
	 * Add a new neighbor to this node.
	 * @param n the node to add
	 */
	public void addNeighbor(Node n) {
		if ( isNeighbor(n) )
			throw new IllegalStateException("Node is already a neighbor!");
		m_neighbors.add(n);
		m_edges.add(new Edge(this,n));
	} //
	
	/**
	 * Add a new neighbor at the specified position.
	 * @param i the index at which to insert the new neighbor
	 * @param n the node to add as a neighbor
	 */
	public void addNeighbor(int i, Node n) {
		if ( isNeighbor(n) )
			throw new IllegalStateException("Node is already a neighbor!");
		m_neighbors.add(i,n);
		m_edges.add(i,new Edge(this,n));
	} //
	
	/**
	 * Remove the given node as a child of this node.
	 * @param n the node to remove
	 */
	public boolean removeNeighbor(Node n) {
		return ( removeNeighbor(getNeighborIndex(n)) != null );
	} //

	/**
	 * Remove the neighbor node at the specified index.
	 * @param i the index at which to remove a node
	 */
	public Node removeNeighbor(int i) {
		Edge e = (Edge)m_edges.remove(i);
		return (Node)m_neighbors.remove(i);
	} //
	
	public Iterator getEdges() {
		return m_edges.iterator();
	} //
	
	public Edge getEdge(Node n) {
		return (Edge)m_edges.get(m_neighbors.indexOf(n));
	} //
	
	public Edge getEdge(int i) {
		return (Edge)m_edges.get(i);
	} //
	
	public boolean isIncidentEdge(Edge e) {
		return ( m_edges.indexOf(e) > -1 );
	} //
	
	public int getEdgeIndex(Edge e) {
		return m_edges.indexOf(e);
	} //
	
	public int getNumEdges() {
		return m_edges.size();
	} //
	
	public void addEdge(Edge e) {		
		addEdge(m_edges.size(), e);
	} //
	
	public void addEdge(int i, Edge e) {
		Node n1 = (Node)e.getFirstNode();
		Node n2 = (Node)e.getSecondNode();
		if ( !e.isDirected() && n2 == this ) {
			Node tmp = n1; n1 = n2; n2 = tmp;
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
	
	public boolean removeEdge(Edge e) {
		return ( removeEdge(m_edges.indexOf(e)) != null );
	} //
	
	public Edge removeEdge(int i) {
		m_neighbors.remove(i);
		return (Edge)m_edges.remove(i);
	} //
	
} // end of class Node
