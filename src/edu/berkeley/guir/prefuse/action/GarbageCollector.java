package edu.berkeley.guir.prefuse.action;

import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * Signals the <code>ItemRegistry</code> to perform a garbage collection 
 * operation. The class type of the <code>GraphItem</code> to garbage
 * collect must be specified through the constructor or 
 * <code>setType()</code> method.
 * 
 * Jul 15, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GarbageCollector extends AbstractAction {
	private String m_itemClass;

	public GarbageCollector() {
		super();
	} //
	
	public GarbageCollector(String itemClass) {
		m_itemClass = itemClass;
	} //

	public String getItemClass() {
		return m_itemClass;
	} //
	
	public void setItemClass(String itemClass) {
		m_itemClass = itemClass;
	} //

	/**
	 * Make the proper garbage collecting call.
	 */
	public void run(ItemRegistry registry, double frac) {
		registry.garbageCollect(m_itemClass);
	} //

} // end of class GarbageCollector
