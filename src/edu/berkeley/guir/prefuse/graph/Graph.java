package edu.berkeley.guir.prefuse.graph;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.event.GraphEventListener;

/**
 * Interface for representing a graph
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface Graph {

	/**
	 * Return the number of nodes in the graph.
	 * @return the number of nodes.
	 */
	public int getNumNodes();
	
	/**
	 * Return the number of edges in the graph.
	 * @return the number of edges.
	 */
	public int getNumEdges();

	/**
	 * Returns an iterator over all the nodes in the graph.
	 * @return an iterator over all nodes.
	 */
	public Iterator getNodes();
	
	/**
	 * Returns an iterator over all the edges in the graph.
	 * @return an iterator over all edges.
	 */
	public Iterator getEdges();
	
	/**
	 * Indicates if the graph contains directed of undirected edges.
	 * @return true if directed, false if undirected.
	 */
	public boolean isDirected();
	
	/**
	 * Indicates if the graph contains the specified node.
	 * @param n the node to check for graph membership.
	 * @return true if the node is in the graph, false otherwise.
	 */
	public boolean contains(Node n);

	public void addGraphEventListener(GraphEventListener gl);
	public void removeFocusListener(GraphEventListener gl);

} // end of interface Graph
