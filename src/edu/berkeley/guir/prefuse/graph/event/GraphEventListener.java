package edu.berkeley.guir.prefuse.graph.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * 
 * Aug 14, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface GraphEventListener extends EventListener {

	public void nodeAdded(Node n);
	public void nodeRemoved(Node n);
	public void nodeReplaced(Node o, Node n);
	
	public void edgeAdded(Edge e);
	public void edgeRemoved(Edge e);
	public void edgeReplaced(Edge o, Edge n);
	
} // end of interface GraphEventListener
