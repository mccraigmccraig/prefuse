package edu.berkeley.guir.prefuse.graph;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.event.GraphEventListener;
import edu.berkeley.guir.prefuse.graph.event.GraphEventMulticaster;

/**
 * 
 * Aug 14, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class AbstractGraph implements Graph {

	protected GraphEventListener m_graphListener = null;
	
	/**
	 * Add a graph event listener.
	 * @param fl the listener to add.
	 */
	public void addGraphEventListener(GraphEventListener gl) {
		m_graphListener = GraphEventMulticaster.add(m_graphListener, gl);
	} //
  	
	/**
	 * Remove a focus listener.
	 * @param fl the listener to remove.
	 */
	public void removeFocusListener(GraphEventListener gl) {
		m_graphListener = GraphEventMulticaster.remove(m_graphListener, gl);
	} //
	
	public void fireNodeAdded(Node n) {
		if ( m_graphListener != null )
			m_graphListener.nodeAdded(n);
	} //

	public void fireNodeRemoved(Node n) {
		if ( m_graphListener != null )
			m_graphListener.nodeRemoved(n);
	} //
	
	public void fireNodeReplaced(Node o, Node n) {
		if ( m_graphListener != null )
			m_graphListener.nodeReplaced(o,n);
	} //
	
	public void fireEdgeAdded(Edge e) {
		if ( m_graphListener != null )
			m_graphListener.edgeAdded(e);
	} //
	
	public void fireEdgeRemoved(Edge e) {
		if ( m_graphListener != null )
			m_graphListener.edgeRemoved(e);
	} //
	
	public void fireEdgeReplaced(Edge o, Edge n) {
		if ( m_graphListener != null )
			m_graphListener.edgeReplaced(o,n);
	} //
		
	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNumNodes()
	 */
	public abstract int getNumNodes();

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNumEdges()
	 */
	public abstract int getNumEdges();

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getNodes()
	 */
	public abstract Iterator getNodes();

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#getEdges()
	 */
	public abstract Iterator getEdges();

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#isDirected()
	 */
	public abstract boolean isDirected();

	/**
	 * @see edu.berkeley.guir.prefuse.graph.Graph#contains(edu.berkeley.guir.prefuse.graph.Node)
	 */
	public abstract boolean contains(Node n);

} // end of class AbstractGraph
