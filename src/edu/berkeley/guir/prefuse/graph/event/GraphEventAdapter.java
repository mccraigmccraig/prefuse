package edu.berkeley.guir.prefuse.graph.event;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * 
 * Aug 14, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GraphEventAdapter implements GraphEventListener {

	/**
	 * @see edu.berkeley.guir.prefuse.graph.event.GraphEventListener#nodeAdded(edu.berkeley.guir.prefuse.graph.Node)
	 */
	public void nodeAdded(Node n) {} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.event.GraphEventListener#nodeRemoved(edu.berkeley.guir.prefuse.graph.Node)
	 */
	public void nodeRemoved(Node n) {} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.event.GraphEventListener#nodeSwapped(edu.berkeley.guir.prefuse.graph.Node, edu.berkeley.guir.prefuse.graph.Node)
	 */
	public void nodeReplaced(Node o, Node n) {} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.event.GraphEventListener#edgeAdded(edu.berkeley.guir.prefuse.graph.Edge)
	 */
	public void edgeAdded(Edge e) {} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.event.GraphEventListener#edgeRemoved(edu.berkeley.guir.prefuse.graph.Edge)
	 */
	public void edgeRemoved(Edge e) {} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.event.GraphEventListener#edgeSwapped(edu.berkeley.guir.prefuse.graph.Edge)
	 */
	public void edgeReplaced(Edge o, Edge n) {} //

} // end of class GraphEventAdapter
