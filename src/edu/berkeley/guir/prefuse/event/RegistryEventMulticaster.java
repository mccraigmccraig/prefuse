package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.VisualItem;

/**
 * Manages a list of listeners for prefuse item registry events.
 * 
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class RegistryEventMulticaster extends EventMulticaster
    implements ItemRegistryListener
{
	public static ItemRegistryListener add(
		ItemRegistryListener a,
		ItemRegistryListener b) {
		return (ItemRegistryListener) addInternal(a, b);
	} //

	public static ItemRegistryListener remove(
		ItemRegistryListener l,
		ItemRegistryListener oldl) {
		return (ItemRegistryListener) removeInternal(l, oldl);
	} //

	public void registryItemAdded(VisualItem item) {
		((ItemRegistryListener) a).registryItemAdded(item);
		((ItemRegistryListener) b).registryItemAdded(item);
	} //

	public void registryItemRemoved(VisualItem item) {
		((ItemRegistryListener) a).registryItemRemoved(item);
		((ItemRegistryListener) b).registryItemRemoved(item);
	} //

    protected static EventListener addInternal(
            EventListener a, EventListener b)
    {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new RegistryEventMulticaster(a, b);
    } //
    
	protected RegistryEventMulticaster(EventListener a, EventListener b) {
		super(a,b);
	} //
    
} // end of class RegistryEventMulticaster
