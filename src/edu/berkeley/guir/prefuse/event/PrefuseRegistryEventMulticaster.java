package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Manages a list of listeners for prefuse registry events.
 * 
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class PrefuseRegistryEventMulticaster
	implements FocusListener, ItemRegistryListener {

	protected final EventListener a, b;

	public static FocusListener add(FocusListener a, FocusListener b) {
		return (FocusListener) addInternal(a, b);
	} //

	public static ItemRegistryListener add(
		ItemRegistryListener a,
		ItemRegistryListener b) {
		return (ItemRegistryListener) addInternal(a, b);
	} //

	public static FocusListener remove(FocusListener a, FocusListener b) {
		return (FocusListener) removeInternal(a, b);
	} //

	public static ItemRegistryListener remove(
		ItemRegistryListener l,
		ItemRegistryListener oldl) {
		return (ItemRegistryListener) removeInternal(l, oldl);
	} //

	public void focusChanged(FocusEvent e) {
		((FocusListener) a).focusChanged(e);
		((FocusListener) b).focusChanged(e);
	} //

	public void registryItemAdded(GraphItem item) {
		((ItemRegistryListener) a).registryItemAdded(item);
		((ItemRegistryListener) b).registryItemAdded(item);
	} //

	public void registryItemRemoved(GraphItem item) {
		((ItemRegistryListener) a).registryItemRemoved(item);
		((ItemRegistryListener) b).registryItemRemoved(item);
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
		return new PrefuseRegistryEventMulticaster(a, b);
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
		} else if (l instanceof PrefuseRegistryEventMulticaster) {
			return ((PrefuseRegistryEventMulticaster) l).remove(oldl);
		} else {
			return l; // it's not here
		}
	} //

	protected PrefuseRegistryEventMulticaster(EventListener a, EventListener b) {
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
		if (l instanceof PrefuseRegistryEventMulticaster) {
			PrefuseRegistryEventMulticaster mc = (PrefuseRegistryEventMulticaster) l;
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
		if (l instanceof PrefuseRegistryEventMulticaster) {
			PrefuseRegistryEventMulticaster mc = (PrefuseRegistryEventMulticaster) l;
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
