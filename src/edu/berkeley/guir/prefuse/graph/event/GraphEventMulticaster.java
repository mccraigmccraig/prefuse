package edu.berkeley.guir.prefuse.graph.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Manages a list of listeners for prefuse registry events.
 * 
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GraphEventMulticaster implements GraphEventListener {

	protected final EventListener a, b;

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

	/** 
	 * Returns the resulting multicast listener after removing the
	 * old listener from listener-l.
	 * If listener-l equals the old listener OR listener-l is null, 
	 * returns null.
	 * Else if listener-l is an instance of AWTEventMulticaster, 
	 * then it removes the old listener from it.
	 * Else, returns listener l.
	 * @param l the listener being removed from
	 * @param oldl the listener being removed
	 */
	protected static EventListener removeInternal(
		EventListener l,
		EventListener oldl) {
		if (l == oldl || l == null) {
			return null;
		} else if (l instanceof GraphEventMulticaster) {
			return ((GraphEventMulticaster) l).remove(oldl);
		} else {
			return l; // it's not here
		}
	} //

	protected GraphEventMulticaster(EventListener a, EventListener b) {
		this.a = a;
		this.b = b;
	} //

	protected EventListener remove(EventListener oldl) {
		if (oldl == a)
			return b;
		if (oldl == b)
			return a;
		EventListener a2 = removeInternal(a, oldl);
		EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b) {
			return this; // it's not here
		}
		return addInternal(a2, b2);
	} //

	private static int getListenerCount(EventListener l) {
		if (l instanceof GraphEventMulticaster) {
			GraphEventMulticaster mc = (GraphEventMulticaster) l;
			return getListenerCount(mc.a) + getListenerCount(mc.b);
		}
		// Delete nulls. 
		else {
			return (l == null) ? 0 : 1;
		}
	} //

	private static int populateListenerArray(
		EventListener[] a,
		EventListener l,
		int index) {
		if (l instanceof GraphEventMulticaster) {
			GraphEventMulticaster mc = (GraphEventMulticaster) l;
			int lhs = populateListenerArray(a, mc.a, index);
			return populateListenerArray(a, mc.b, lhs);
		} else if (l != null) {
			a[index] = l;
			return index + 1;
		}
		// Delete nulls. 
		else {
			return index;
		}
	} //

	public static EventListener[] getListeners(
		EventListener l,
		Class listenerType) {
		int n = getListenerCount(l);
		EventListener[] result =
			(EventListener[]) java.lang.reflect.Array.newInstance(
				listenerType,
				n);
		populateListenerArray(result, l, 0);
		return result;
	} //
	
} // end of class PrefuseEventMulticaster
