package edu.berkeley.guir.prefuse.graph.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.event.EventMulticaster;
import edu.berkeley.guir.prefuse.graph.Entity;

/**
 * Manages listeners for graph modification events.
 * 
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GraphLoaderMulticaster extends EventMulticaster 
    implements GraphLoaderListener
{

	public void entityLoaded(Entity e) {
	    ((GraphLoaderListener)a).entityLoaded(e);
        ((GraphLoaderListener)b).entityLoaded(e);
    } //
    
    public void entityUnloaded(Entity e) {
        ((GraphLoaderListener)a).entityUnloaded(e);
        ((GraphLoaderListener)b).entityUnloaded(e);
    } //

	public static GraphLoaderListener add(GraphLoaderListener a, GraphLoaderListener b) {
		return (GraphLoaderListener) addInternal(a, b);
	} //

	public static GraphLoaderListener remove(GraphLoaderListener a, GraphLoaderListener b) {
		return (GraphLoaderListener) removeInternal(a, b);
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
		return new GraphLoaderMulticaster(a, b);
	} //

	protected GraphLoaderMulticaster(EventListener a, EventListener b) {
		super(a,b);
	} //

} // end of class PrefuseEventMulticaster
