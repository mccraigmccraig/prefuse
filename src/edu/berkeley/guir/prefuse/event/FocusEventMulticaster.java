package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

/**
 * Manages a list of listeners for focus events.
 * 
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class FocusEventMulticaster extends EventMulticaster
    implements FocusListener
{

	public static FocusListener add(FocusListener a, FocusListener b) {
		return (FocusListener) addInternal(a, b);
	} //

	public static FocusListener remove(FocusListener a, FocusListener b) {
		return (FocusListener) removeInternal(a, b);
	} //

    protected static EventListener addInternal(
            EventListener a, EventListener b)
    {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new FocusEventMulticaster(a, b);
    } //      

	protected FocusEventMulticaster(EventListener a, EventListener b) {
        super(a, b);
	} //

    public void focusChanged(FocusEvent e) {
        ((FocusListener) a).focusChanged(e);
        ((FocusListener) b).focusChanged(e);
    } //
	
} // end of class FocusEventMulticaster
