package edu.berkeley.guir.prefuse.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.berkeley.guir.prefuse.collections.EdgeIterator;

/**
 * A straight-forward representation of a general graph.
 * TODO: Fire events for edge changes
 * 
 * May 21, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class SimpleGraph extends AbstractGraph {

	private static final Class LIST_TYPE = ArrayList.class;

	protected List m_nodes;
	protected boolean m_directed = false;

	/**
	 * Constructor. Takes in a collection of nodes contained in
	 * the graph.
	 * @param nodes a collection of the nodes of the graph.
	 */
	public SimpleGraph(Collection nodes, boolean directed) {
		this(directed);
		m_nodes.addAll(nodes);
	} //
	
	/**
	 * Constructor. Takes in a collection of nodes contained in
	 * the graph.
	 * @param nodes a collection of the nodes of the graph.
	 */
	public SimpleGraph(Collection nodes) {
		this();
		m_nodes.addAll(nodes);
	} //	
	
	/**
	 * Constructor. Creates an empty graph.
	 */
	public SimpleGraph() {
		m_directed = false;
		try {
			m_nodes = (List)LIST_TYPE.newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}		
	} //

	/**
	 * Constructor. Creates an empty graph.
	 */
	public SimpleGraph(boolean directed) {
		m_directed = directed;
		try {
			m_nodes = (List)LIST_TYPE.newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}		
	} //

	/**
	 * Add a node to this graph.
	 * @param n the node to add
	 */
	public void addNode(Node n) {
		m_nodes.add(n);
		fireNodeAdded(n);
	} //
	
	/**
	 * Remove a node from the graph.
	 * @param n
	 */
	public void removeNode(Node n) {
		int idx = m_nodes.indexOf(n);
		if ( idx < 0 ) throw new IllegalArgumentException();
		
		int nN = n.getNumNeighbors();
		for ( int i = 0; i < nN; i++ ) {
			Edge e = n.removeEdge(0);
			if ( !e.isDirected() ) {
				Node n2 = (Node)e.getFirstNode();
				if ( n2 == n )
					n2 = (Node)e.getSecondNode();				
				n2.removeNeighbor(n);
			}
			fireEdgeRemoved(e);
		}
		m_nodes.remove(n);
		
		// remove any 'inlinks' to the removed node
		// TODO? could be made more efficient by explicitly
		// representing node inlinks... 
		if ( m_directed ) {
			Iterator iter = m_nodes.iterator();
			while ( iter.hasNext() ) {
				Node n2 = (Node)iter.next();
				int nidx = n2.getNeighborIndex(n);
				if ( nidx > -1 ) {
					Edge e = n2.removeEdge(nidx);
					fireEdgeRemoved(e);
				}
			}
		}
		fireNodeRemoved(n);
	} //

	/**
	 * Add an edge to this graph.
	 * @param u the first node in the edge
	 * @param v the second node in the edge
	 */
	public void addEdge(Node u, Node v) {
		addEdge(new Edge(u,v,m_directed));
	} //
	
	/**
	 * Add an edge to this graph.
	 * @param e the <code>Edge</code> to add
	 */
	public boolean addEdge(Edge e) {
		Node n1 = (Node)e.getFirstNode();
		Node n2 = (Node)e.getSecondNode();
		if ( n1.isNeighbor(n2) || n2.isNeighbor(n1) ) {
			return false;
		}
		if ( m_directed ^ e.isDirected() ) {
			throw new IllegalStateException(
				"Directedness of edge and graph differ");
		}
		n1.addEdge(e);
		if ( !m_directed ) {
			n2.addEdge(e);
		}
		fireEdgeAdded(e);
        return true;
	} //

	/**
	 * Remove an edge from the graph
	 * @param e the <code>Edge</code> to remove
	 */
	public void removeEdge(Edge e) {
		if ( m_directed ^ e.isDirected() ) {
			throw new IllegalStateException(
				"Directedness of edge and graph differ");
		}
		Node n1 = (Node)e.getFirstNode();
		Node n2 = (Node)e.getSecondNode();
		if ( !n1.isNeighbor(n2) || (!m_directed && !n2.isNeighbor(n1)) ) {
			throw new IllegalStateException(
				"No edge exists between these nodes");
		}
		n1.removeNeighbor(n2);
		if ( !m_directed ) {
			n2.removeNeighbor(n1);
		}
		fireEdgeRemoved(e);
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNumNodes()
	 */
	public int getNumNodes() {
		return m_nodes.size();
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNumEdges()
	 */
	public int getNumEdges() {
		int numEdges = 0;
		for ( Iterator i = getEdges(); i.hasNext(); i.next(), numEdges++ );
		return numEdges;
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNodes()
	 */
	public Iterator getNodes() {
		return m_nodes.iterator();
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getEdges()
	 */
	public Iterator getEdges() {
		return new EdgeIterator(this.getNodes());
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#isDirected()
	 */
	public boolean isDirected() {
		return m_directed;
	}

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#contains(edu.berkeley.guir.prefuse.graph.Node)
	 */
	public boolean contains(Node n) {
		return m_nodes.contains(n);
	} //

} // end of class SimpleGraph
