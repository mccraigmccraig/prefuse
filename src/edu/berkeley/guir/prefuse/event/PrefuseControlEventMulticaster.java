package edu.berkeley.guir.prefuse.event;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.EventListener;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Manages a list of listeners for prefuse control events.
 * 
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class PrefuseControlEventMulticaster implements ControlListener {

	protected final EventListener a, b;

	public static ControlListener add(ControlListener a, ControlListener b) {
		return (ControlListener) addInternal(a, b);
	} //

	public static ControlListener remove(
		ControlListener l,
		ControlListener oldl) {
		return (ControlListener) removeInternal(l, oldl);
	} //

	public void itemDragged(GraphItem item, MouseEvent e) {
		((ControlListener) a).itemDragged(item, e);
		((ControlListener) b).itemDragged(item, e);
	} //

	public void itemMoved(GraphItem item, MouseEvent e) {
		((ControlListener) a).itemMoved(item, e);
		((ControlListener) b).itemMoved(item, e);
	} //

	public void itemWheelMoved(GraphItem item, MouseWheelEvent e) {
		((ControlListener) a).itemWheelMoved(item, e);
		((ControlListener) b).itemWheelMoved(item, e);
	} //

	public void itemClicked(GraphItem item, MouseEvent e) {
		((ControlListener) a).itemClicked(item, e);
		((ControlListener) b).itemClicked(item, e);
	} //

	public void itemPressed(GraphItem item, MouseEvent e) {
		((ControlListener) a).itemPressed(item, e);
		((ControlListener) b).itemPressed(item, e);
	} //

	public void itemReleased(GraphItem item, MouseEvent e) {
		((ControlListener) a).itemReleased(item, e);
		((ControlListener) b).itemReleased(item, e);
	} //

	public void itemEntered(GraphItem item, MouseEvent e) {
		((ControlListener) a).itemEntered(item, e);
		((ControlListener) b).itemEntered(item, e);
	} //

	public void itemExited(GraphItem item, MouseEvent e) {
		((ControlListener) a).itemExited(item, e);
		((ControlListener) b).itemExited(item, e);
	} //

	public void itemKeyPressed(GraphItem item, KeyEvent e) {
		((ControlListener) a).itemKeyPressed(item, e);
		((ControlListener) b).itemKeyPressed(item, e);
	} //

	public void itemKeyReleased(GraphItem item, KeyEvent e) {
		((ControlListener) a).itemKeyReleased(item, e);
		((ControlListener) b).itemKeyReleased(item, e);
	} //

	public void itemKeyTyped(GraphItem item, KeyEvent e) {
		((ControlListener) a).itemKeyTyped(item, e);
		((ControlListener) b).itemKeyTyped(item, e);
	} //

	public void mouseEntered(MouseEvent e) {
		((ControlListener) a).mouseEntered(e);
		((ControlListener) b).mouseEntered(e);
	} //
	
	public void mouseExited(MouseEvent e) {
		((ControlListener) a).mouseExited(e);
		((ControlListener) b).mouseExited(e);
	} //
	
	public void mousePressed(MouseEvent e) {
		((ControlListener) a).mousePressed(e);
		((ControlListener) b).mousePressed(e);
	} //
	
	public void mouseReleased(MouseEvent e) {
		((ControlListener) a).mouseReleased(e);
		((ControlListener) b).mouseReleased(e);
	} //
	
	public void mouseClicked(MouseEvent e) {
		((ControlListener) a).mouseClicked(e);
		((ControlListener) b).mouseClicked(e);
	} //
	
	public void mouseDragged(MouseEvent e) {
		((ControlListener) a).mouseDragged(e);
		((ControlListener) b).mouseDragged(e);
	} //
	
	public void mouseMoved(MouseEvent e) {
		((ControlListener) a).mouseMoved(e);
		((ControlListener) b).mouseMoved(e);
	} //
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		((ControlListener) a).mouseWheelMoved(e);
		((ControlListener) b).mouseWheelMoved(e);
	} //
	
	public void keyPressed(KeyEvent e) {
		((ControlListener) a).keyPressed(e);
		((ControlListener) b).keyPressed(e);
	} //
	
	public void keyReleased(KeyEvent e) {
		((ControlListener) a).keyReleased(e);
		((ControlListener) b).keyReleased(e);
	} //
	
	public void keyTyped(KeyEvent e) {
		((ControlListener) a).keyTyped(e);
		((ControlListener) b).keyTyped(e);
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
		return new PrefuseControlEventMulticaster(a, b);
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
		} else if (l instanceof PrefuseControlEventMulticaster) {
			return ((PrefuseControlEventMulticaster) l).remove(oldl);
		} else {
			return l; // it's not here
		}
	} //

	protected PrefuseControlEventMulticaster(EventListener a, EventListener b) {
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
		if (l instanceof PrefuseControlEventMulticaster) {
			PrefuseControlEventMulticaster mc = (PrefuseControlEventMulticaster) l;
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
		if (l instanceof PrefuseControlEventMulticaster) {
			PrefuseControlEventMulticaster mc = (PrefuseControlEventMulticaster) l;
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
