package edu.berkeley.guir.prefuse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Factory class for GraphItem instances. This allows object
 * initialization to be consolidated in a single location and allocated objects
 * to be re-used.
 * 
 * This class works closely with the ItemRegistry, but is
 * implemented separately to provide encapsulation and simplify design.
 * 
 * Apr 24, 2003 - jheer - Created class
 * Jul 16, 2003 - jheer - Generalized to handle arbitrary item classes
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class ItemFactory {
	
	private static final Class LIST_TYPE = LinkedList.class;
	private static final Class MAP_TYPE  = HashMap.class;
	
	private class FactoryEntry {
		FactoryEntry(String itemClass, Class classType, int maxSize) {
			try {
				maxItems = maxSize;
				name     = itemClass;
				type     = classType;
				itemList = (List)LIST_TYPE.newInstance();
			} catch ( Exception e) {
				e.printStackTrace();
			}
		} //
		int     maxItems;
		Class   type;
		String  name;
		List    itemList;
	} // end of inner class ItemEntry
	
	private Map m_entryMap;
	
	/**
	 * Constructor. Creates a new ItemFactory instance.
	 */
	public ItemFactory() {
		try {
			m_entryMap = (Map)MAP_TYPE.newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //
	
	public void addItemClass(String itemClass, Class classType, int maxItems) {
		FactoryEntry fentry = new FactoryEntry(itemClass, classType, maxItems);
		m_entryMap.put(itemClass, fentry);
	} //
	
	// ========================================================================
	// == FACTORY METHODS =====================================================
	
	public GraphItem getItem(String itemClass) {
		FactoryEntry fentry = (FactoryEntry)m_entryMap.get(itemClass);
		if ( fentry != null ) {
			GraphItem item = null;
			if ( fentry.itemList.isEmpty() ) {
				try {
					item = (GraphItem)fentry.type.newInstance();
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			} else {
				item = (GraphItem)fentry.itemList.remove(0);
			}
			return item;
		} else {
			throw new IllegalArgumentException("The input string must be a"
						+ " recognized item class!");
		}
	} //
	
	/**
	 * Reclaim an item into an item pool. Used to avoid object initialization
	 * costs. If maximum pool sizes are reached, this item will not be
	 * reclaimed. In this case it should have NO remaining references, allowing
	 * it to be garbage collected.
	 * @param item the GraphItem to reclaim
	 */
	public void reclaim(GraphItem item) {
		String itemClass    = item.getItemClass();
		FactoryEntry fentry = (FactoryEntry)m_entryMap.get(itemClass);
		
		// clear any references within the item
		item.clear();
		
		// Determine which "bin" the item belongs in, then add it
		// if the maximum has not yet been reached.
		if ( fentry.itemList.size() <= fentry.maxItems ) {
			fentry.itemList.add(item);
		}
	} //

} // end of class ItemFactory
