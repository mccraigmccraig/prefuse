package edu.berkeley.guir.prefuse.event;

import java.util.EventObject;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.Entity;

/**
 * Represents a change in which nodes have been placed in "focus" by
 * user selection.
 * 
 * Apr 26, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class FocusEvent extends EventObject {
	
	public static final int FOCUS_ADDED   = 0;
	public static final int FOCUS_REMOVED = 1;
	public static final int FOCUS_SET     = 2;
	
	private Entity m_focus;
	private Entity m_prevFocus;
	private int    m_type;
	
	/**
	 * Constructor.
	 * @param registry the registry upon which the focus change occurs.
	 * @param type the type of the FocusEvent. Is one of FOCUS_ADDED, 
	 *  FOCUS_REMOVED, and FOCUS_SET.
	 * @param focus the focus entity added, removed, or set.
	 * @param prevFocus the previous focus (if it exists), in case of 
	 *  a FOCUS_SET
	 */
	public FocusEvent(ItemRegistry registry, int type, Entity focus, Entity prevFocus) {
		super(registry);
		m_type = type;
		m_focus = focus;
		m_prevFocus = prevFocus;		
	} //
	
	/**
	 * Returns the ItemRegistry where the focus change occurred.
	 * @return the ItemRegistry
	 */
	public ItemRegistry getSourceRegistry() {
		return (ItemRegistry)getSource();
	} //
	
	/**
	 * Returns the type of focus change.
	 * @return the type of focus change
	 */
	public int getType() {
		return m_type;
	} //
	
	/**
	 * Returns the affected focus entity.
	 * @return the affected focus entity
	 */
	public Entity getFocus() {
		return m_focus;
	} //
	
	/**
	 * Returns the previous focus, if appropriate.
	 * @return the previous focus
	 */
	public Entity getPreviousFocus() {
		return m_prevFocus;
	} //
		
} // end of class FocusEvent
