package edu.berkeley.guir.prefuse.graph.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.event.EventMulticaster;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Manages listeners for graph modification events.
 * 
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GraphEventMulticaster extends EventMulticaster 
    implements GraphEventListener
{

	public void nodeAdded(Node n) {
		((GraphEventListener) a).nodeAdded(n);
		((GraphEventListener) b).nodeAdded(n);
	} //

	public void nodeRemoved(Node n) {
		((GraphEventListener) a).nodeRemoved(n);
		((GraphEventListener) b).nodeRemoved(n);
	} //

	public void nodeReplaced(Node o, Node n) {
		((GraphEventListener) a).nodeReplaced(o,n);
		((GraphEventListener) b).nodeReplaced(o,n);		
	} //

	public void edgeAdded(Edge e) {
		((GraphEventListener) a).edgeAdded(e);
		((GraphEventListener) b).edgeAdded(e);
	} //

	public void edgeRemoved(Edge e) {
		((GraphEventListener) a).edgeRemoved(e);
		((GraphEventListener) b).edgeRemoved(e);
	} //

	public void edgeReplaced(Edge o, Edge n) {
		((GraphEventListener) a).edgeReplaced(o,n);
		((GraphEventListener) b).edgeReplaced(o,n);		
	} //

	public static GraphEventListener add(GraphEventListener a, GraphEventListener b) {
		return (GraphEventListener) addInternal(a, b);
	} //

	public static GraphEventListener remove(GraphEventListener a, GraphEventListener b) {
		return (GraphEventListener) removeInternal(a, b);
	} //

	/** 
	 * Returns the resulting multicast listener from adding listener-a
	 * and listener-b together.  
	 * If listener-a is null, it returns listener-b;  
	 * If listener-b is null, it returns listener-a
	 * If neither are null, then it creates and returns
	 * a new AWTEventMulticaster instance which chains a with b.
	 * @param a event listener-a
	 * @param b event listener-b
	 */
	protected static EventListener addInternal(
		EventListener a,
		EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new GraphEventMulticaster(a, b);
	} //

	protected GraphEventMulticaster(EventListener a, EventListener b) {
		super(a,b);
	} //

} // end of class PrefuseEventMulticaster
