package edu.berkeley.guir.prefuse;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.berkeley.guir.prefuse.collections.CompositeItemIterator;
import edu.berkeley.guir.prefuse.collections.DefaultItemComparator;
import edu.berkeley.guir.prefuse.collections.VisibleItemIterator;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.event.ItemRegistryListener;
import edu.berkeley.guir.prefuse.event.PrefuseRegistryEventMulticaster;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.RendererFactory;

/**
 * Registry containing all items to be visualized.
 *
 * Apr 22, 2003 - jheer - Created class
 * Jul 16, 2003 - jheer - Generalized to handle arbitrary item classes
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class ItemRegistry {

	public static final String DEFAULT_NODE_CLASS = "node";
	public static final String DEFAULT_EDGE_CLASS = "edge";
	public static final String DEFAULT_AGGR_CLASS = "aggregate";
	public static final int    DEFAULT_MAX_ITEMS  = 10000;
	public static final int    DEFAULT_MAX_DIRTY  = 1;

	private static final Class LIST_TYPE = LinkedList.class;
	private static final Class MAP_TYPE  = HashMap.class;
	
	/**
	 * Wrapper class that holds all the data structures for managing
	 * a class of GraphItems.
	 */
	public class ItemEntry {
		ItemEntry(String itemClass, Class classType, int dirty) {
			try {
				name     = itemClass;
				type     = classType;
				itemList = (List)LIST_TYPE.newInstance();
				itemMap  = (Map)MAP_TYPE.newInstance();
				modified = false;
				maxDirty = dirty;
			} catch ( Exception e) {
				e.printStackTrace();
			}
		} //
		public List getItemList() {	return itemList; } //
		
		public boolean modified;
		public int     maxDirty;
		public Class   type;
		public String  name;
		public List    itemList;
		public Map     itemMap;
	} // end of inner class ItemEntry
	
	private ItemFactory     m_ifactory;
	private RendererFactory m_rfactory;
	
	private List m_entryList; // list of ItemEntry instances
	private Map  m_entryMap;  // maps from item class names to ItemEntry instances
	private Map  m_entityMap; // maps from items back to their entities
	
	private Comparator m_comparator;
	
	private List m_focusList; // list of visualization's focal nodes
  
  	private ItemRegistryListener m_registryListener;
  	private FocusListener        m_focusListener;
	
	/**
	 * Constructor. Creates an empty ItemRegistry and corresponding ItemFactory.
	 * By default, creates queues and <code>ItemFactory</code> entries for
	 * handling NodeItems, EdgeItems, and AggregateItems, respectively. All
	 * are given default settings, including a maxDirty value of 1.
	 */
	public ItemRegistry() {
		this(true);
	} //
	
	public ItemRegistry(boolean initDefault) {
		try {
			m_ifactory  = new ItemFactory();
			m_rfactory  = new DefaultRendererFactory();
			m_entryList = (List)LIST_TYPE.newInstance();
			m_entryMap  = (Map)MAP_TYPE.newInstance();
			m_entityMap = (Map)MAP_TYPE.newInstance();
			m_comparator = new DefaultItemComparator();
			
			m_focusList = (List)LIST_TYPE.newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		if ( initDefault ) {
			defaultInitialization();		
		}
	} //
	
	private synchronized void defaultInitialization() {
		addItemClass(DEFAULT_NODE_CLASS, NodeItem.class);
		addItemClass(DEFAULT_EDGE_CLASS, EdgeItem.class);
		addItemClass(DEFAULT_AGGR_CLASS, AggregateItem.class);
	} //
	
	public synchronized void addItemClass(String itemClass, Class itemType) {
		addItemClass(itemClass, itemType, DEFAULT_MAX_DIRTY, DEFAULT_MAX_ITEMS);
	} //
	
	public synchronized void addItemClass(String itemClass, Class itemType, int maxDirty) {
		addItemClass(itemClass, itemType, maxDirty, DEFAULT_MAX_ITEMS);
	} //
	
	public synchronized void addItemClass(String itemClass, Class itemType, 
								int maxDirty, int maxItems) {
		ItemEntry entry = new ItemEntry(itemClass, itemType, maxDirty);
		m_entryList.add(entry);
		m_entryMap.put(itemClass, entry);
		m_ifactory.addItemClass(itemClass, itemType, maxItems);
	} //
	
	/**
	 * Return the renderer factory for this registry's items. The
	 * renderer factory determines which renderer components should be
	 * used to draw GraphItems in the visualization.
	 * @return the current renderer factory
	 */
	public synchronized RendererFactory getRendererFactory() {
		return m_rfactory;
	} //
	
	/**
	 * Set the renderer factory for this registry's items. The
	 * renderer factory determines which renderer components should be
	 * used to draw GraphItems in the visualization. By using this method,
	 * one can set custom renderer factories to control the rendering 
	 * behavior of all visualized items.
	 * @param factory the renderer factory to use
	 */
	public synchronized void setRendererFactory(RendererFactory factory) {
		m_rfactory = factory;
	} //

	/**
	 * Return the item comparator used to determine rendering order.
	 * @return the item comparator
	 */
	public synchronized Comparator getItemComparator() {
		return m_comparator;
	} //
	
	/**
	 * Sets the item comparator used to determine rendering order. This
	 * method can be used to install custom comparators for GraphItems,
	 * allowing fine grained control over the order items are processed
	 * in the rendering loop.
	 * 
	 * Items drawn later will appear on top of earlier-drawn items, and the
	 * registry sorts items in <i>increasing</i> order, so the the greater
	 * the item is according to the comparator, the later it will be drawn
	 * in the rendering cycle.
	 * 
	 * @return the item comparator
	 */
	public synchronized void setItemComparator(Comparator comparator) {
		m_comparator = comparator;
	} //

	// ========================================================================
	// == REGISTRY METHODS ====================================================

	public synchronized void garbageCollect(String itemClass) {
		ItemEntry entry = (ItemEntry)m_entryMap.get(itemClass);
		if ( entry != null ) {
			garbageCollect(entry);
		} else {
			throw new IllegalArgumentException("The input string must be a" 
				+ " recognized item class!");
		}
	} //
	
	public synchronized void garbageCollect(ItemEntry entry) {
		entry.modified = true;
		for ( int i = 0; i < entry.itemList.size(); i++ ) {
			GraphItem item = (GraphItem)entry.itemList.get(i);			
			int dirty = item.getDirty()+1;
			item.setDirty(dirty);
			if ( entry.maxDirty > -1 && dirty > entry.maxDirty ) {
				removeItem(entry, item);
				i--;
			} else if ( dirty > 1 ) {
				item.setVisible(false);
			}
		}		
	} //
		
	/**
	 * Perform garbage collection of NodeItems. Use carefully.
	 */
	public synchronized void garbageCollectNodes() {
		garbageCollect(DEFAULT_NODE_CLASS);
	} //
	
	/**
	 * Perform garbage collection of EdgeItems. Use carefully.
	 */
	public synchronized void garbageCollectEdges() {
		garbageCollect(DEFAULT_EDGE_CLASS);
	} //
	
	/**
	 * Perform garbage collection of AggregateItems. Use carefully.
	 */
	public synchronized void garbageCollectAggregates() {
		garbageCollect(DEFAULT_AGGR_CLASS);
	} //

	public synchronized void clear() {
		Iterator iter = m_entryList.iterator();
		while ( iter.hasNext() ) {
			clear((ItemEntry)iter.next());
		}
	} //

	private synchronized void clear(ItemEntry entry) {
		entry.modified = true;
		while ( entry.itemList.size() > 0 ) {
			GraphItem item = (GraphItem)entry.itemList.get(0);
			this.removeItem(entry, item);
		}
	} //

	/**
	 * Returns all the visible GraphItems in the registry. The order items 
	 * are returned will determine their rendering order. This order is 
	 * determined by the item comparator. The setItemComparator() method can
	 * be used to control this ordering.
	 * @return iterator over all visible GraphItems, in rendering order
	 */
	public synchronized Iterator getItems() {
		Iterator entryIter = m_entryList.iterator();
		while ( entryIter.hasNext() ) {
			ItemEntry entry = (ItemEntry)entryIter.next();
			if ( entry.modified ) {
				Collections.sort(entry.itemList, m_comparator);
				entry.modified = false;
			}
		}
		return new CompositeItemIterator(m_entryList,m_comparator,false);
	} //

	/**
	 * Returns all the visible GraphItems in the registry in <i>reversed</i>
	 * rendering order.
	 * @return iterator over all visible GraphItems, in reverse rendering order
	 */
	public synchronized Iterator getItemsReversed() {
		Iterator entryIter = m_entryList.iterator();
		while ( entryIter.hasNext() ) {
			ItemEntry entry = (ItemEntry)entryIter.next();
			if ( entry.modified ) {
				Collections.sort(entry.itemList, m_comparator);
				entry.modified = false;
			}
		}
		return new CompositeItemIterator(m_entryList,m_comparator,true);
	} //

	public synchronized Iterator getItems(String itemClass, boolean visibleOnly) {
		ItemEntry entry = (ItemEntry)m_entryMap.get(itemClass);
		if ( entry != null ) {
			if ( entry.modified ) {
				Collections.sort(entry.itemList, m_comparator);
				entry.modified = false;
			}
			if ( visibleOnly ) {
				return new VisibleItemIterator(entry.itemList, false);
			} else {
				return entry.itemList.iterator();
			}
		} else {
			throw new IllegalArgumentException("The input string must be a"
						+ " recognized item class!");
		}
	} //

	public synchronized void touch(String itemClass) {
		ItemEntry entry = (ItemEntry)m_entryMap.get(itemClass);
		if ( entry != null ) {
			entry.modified = true;
		} else {
			throw new IllegalArgumentException("The input string must be a"
						+ " recognized item class!");		
		}
	} //
	
	public synchronized void touchNodeItems() {
		touch(DEFAULT_NODE_CLASS);
	} //
	
	public synchronized void touchEdgeItems() {
		touch(DEFAULT_EDGE_CLASS);
	} //
	
	public synchronized void touchAggregateItems() {
		touch(DEFAULT_AGGR_CLASS);
	} //

	/**
	 * Returns an iterator over all visible NodeItems, in rendering order.
	 * @return iterator over NodeItems in rendering order
	 */
	public synchronized Iterator getNodeItems() {
		return getItems(DEFAULT_NODE_CLASS, true);
	} //
	
	/**
	 * Returns an iterator over NodeItems, in rendering order. If 
	 * <code>visibleOnly</code> is true, only currently visible items will be
	 * returned. If it is false, all NodeItems currently in the queue will be
	 * returned. 
	 * @param visibleOnly true to show only visible items, false for all items
	 * @return an <code>Iterator</code> over items in rendering order.
	 */
	public synchronized Iterator getNodeItems(boolean visibleOnly) {
		return getItems(DEFAULT_NODE_CLASS, visibleOnly);
	} //
	
	/**
	 * Returns an iterator over all visible EdgeItems, in rendering order.
	 * @return iterator over EdgeItems in rendering order
	 */
	public synchronized Iterator getEdgeItems() {
		return getItems(DEFAULT_EDGE_CLASS, true);
	} //
	
	/**
	 * Returns an iterator over EdgeItems, in rendering order. If 
	 * <code>visibleOnly</code> is true, only currently visible items will be
	 * returned. If it is false, all EdgeItems currently in the queue will be
	 * returned. 
	 * @param visibleOnly true to show only visible items, false for all items
	 * @return an <code>Iterator</code> over items in rendering order.
	 */
	public synchronized Iterator getEdgeItems(boolean visibleOnly) {
		return getItems(DEFAULT_EDGE_CLASS, visibleOnly);
	} //
	
	/**
	 * Returns an iterator over all visible AggregateItems, in rendering order.
	 * @return iterator over AggregateItems in rendering order
	 */
	public synchronized Iterator getAggregateItems() {
		return getItems(DEFAULT_AGGR_CLASS, true);
	} //
	
	/**
	 * Returns an iterator over AggregateItems, in rendering order. If 
	 * <code>visibleOnly</code> is true, only currently visible items will be
	 * returned. If it is false, all AggregateItems currently in the queue will
	 * be returned. 
	 * @param visibleOnly true to show only visible items, false for all items
	 * @return an <code>Iterator</code> over items in rendering order.
	 */
	public synchronized Iterator getAggregateItems(boolean visibleOnly) {
		return getItems(DEFAULT_AGGR_CLASS, visibleOnly);
	} //
	
	/**
	 * Returns the entity associated with the given GraphItem, if any.
	 * If multiple entities are associated with an input GraphItem of
	 * type AggregateItem, the first one is returned. To get all entities
	 * in such cases use the getEntities() method instead.
	 * @param item
	 * @return Entity
	 */
	public synchronized Entity getEntity(GraphItem item) {
		Object o = m_entityMap.get(item);
		if ( o == null ) {
			return null;
		} else if ( o instanceof Entity ) {
			return (Entity)o;
		} else {
			return (Entity)((List)o).get(0);
		}
	} //
	
	/**
	 * Returns the entities associated with the given GraphItem, if any.
	 * @param item
	 * @return Entity
	 */
	public synchronized List getEntities(GraphItem item) {
		Object o = m_entityMap.get(item);
		List list;
		if ( o instanceof Entity ) {
			(list = new LinkedList()).add(o);
		} else {
			list = (List)o;
		}
		return list;
	} //
	
	/**
	 * Determines if a node is visible (i.e. directly displayed by the
	 * visualization, not as part of an aggregate).
	 */
	public synchronized boolean isVisible(Node n) {
		NodeItem item;
		return ( (item=getNodeItem(n)) != null && item.isVisible() );		 
	} //
	
	public synchronized GraphItem getItem(String itemClass, Entity entity, boolean create) {
		ItemEntry entry = (ItemEntry)m_entryMap.get(itemClass);
		if ( entry != null ) {
			GraphItem item = (GraphItem)entry.itemMap.get(entity);
			if ( !create ) {
				return item;
			} else if ( item == null ) {
				item = m_ifactory.getItem(itemClass);
				item.init(this, itemClass, entity);
				addItem(entry, entity, item);
			}
            if ( item instanceof NodeItem )
                ((NodeItem)item).removeAllNeighbors();
            item.setDirty(0);
            item.setVisible(true);
			return item;
		} else {
			throw new IllegalArgumentException("The input string must be a"
						+ " recognized item class!");
		}		
	} //
	
	/**
	 * Returns the visualized NodeItem associated with the given Node, if any.
	 * @param node the Node to look up
	 * @return NodeItem the NodeItem associated with the node, if any.
	 */
	public synchronized NodeItem getNodeItem(Node node) {
		return (NodeItem)getItem(DEFAULT_NODE_CLASS, node, false);			
	} //
	
	/**
	 * Returns the visualized NodeItem associated with the given Node, if any.
	 * If create is true, creates the desired NodeItem and adds it to the
	 * registry, and removes any previous bindings associated with the Node.
	 * @param node the Node to look up
	 * @param create if true, a new NodeItem will be allocated if necessary
	 * @return NodeItem the NodeItem associated with the node, if any
	 */
	public synchronized NodeItem getNodeItem(Node node, boolean create) {
		return (NodeItem)getItem(DEFAULT_NODE_CLASS, node, create);		
	} //

	/**
	 * Returns the visualized EdgeItem associated with the given Edge, if any.
	 * @param edge the Edge to look up
	 * @return EdgeItem the EdgeItem associated with the edge, if any
	 */
	public synchronized EdgeItem getEdgeItem(Edge edge) {
		return (EdgeItem)getItem(DEFAULT_EDGE_CLASS, edge, false);
	} //
	
	/**
	 * Returns the visualized EdgeItem associated with the given Edge, if any.
	 * If create is true, creates the desired EdgeItem and adds it to the
	 * registry, and removes any previous bindings associated with the Edge.
	 * @param edge the Edge to look up
	 * @param create if true, a new EdgeItem will be allocated if necessary
	 * @return EdgeItem the EdgeItem associated with the edge, if any
	 */
	public synchronized EdgeItem getEdgeItem(Edge edge, boolean create) {
		return (EdgeItem)getItem(DEFAULT_EDGE_CLASS, edge, create);		
	} //
	
	/**
	 * Returns the visualized AggregateItem associated with the given Node,
	 * if any.
	 * @param node the Node to look up
	 * @return AggregateItem the AggregateItem associated with the node, if any
	 */
	public synchronized AggregateItem getAggregateItem(Entity entity) {
		return (AggregateItem)getItem(DEFAULT_AGGR_CLASS, entity, false);
	} //
	
	/**
	 * Returns the visualized AggregateItem associated with the given Entity, if
	 * any. If create is true, creates the desired AggregateItem and adds it to
	 * the registry, and removes any previous bindings associated with the
	 * Entity.
	 * @param node the Node to look up
	 * @param create if true, a new AggregateItem will be allocated if 
	 *  necessary
	 * @return AggregateItem the AggregateItem associated with the node, if any
	 */
	public synchronized AggregateItem getAggregateItem(Entity entity, boolean create) {
		return (AggregateItem)getItem(DEFAULT_AGGR_CLASS, entity, create);
	} //

	/**
	 * Add a mapping between the given entity and item, this means that
	 * the entity is part of the aggregation represented by the item.
	 * @param entity the Entity (e.g. Node or Edge) to add
	 * @param item the GraphItem
	 */	
	public synchronized void addMapping(Entity entity, GraphItem item) {
		String itemClass = item.getItemClass();
		ItemEntry entry = (ItemEntry)m_entryMap.get(itemClass);
		if ( entry != null ) {
			addMapping(entry, entity, item);
		} else {
			throw new IllegalArgumentException("The input string must be a"
						+ " recognized item class!");
		}
	} //
	
	/**
	 * Add a mapping between the given entity and the item within the
	 *  given item class
	 * @param entity the graph Entity to add
	 * @param item the GraphItem corresponding to the entity
	 */
	private synchronized void addMapping(ItemEntry entry, Entity entity, GraphItem item) {
		entry.itemMap.put(entity, item);
		if ( m_entityMap.containsKey(item) ) {
			Object o = m_entityMap.get(item);
			List list;
			if ( o instanceof List ) {
				list = (List)o;
			} else {
				(list = new LinkedList()).add(o);
			}
			list.add(entity);
			m_entityMap.put(item, list);
		} else {
			m_entityMap.put(item, entity);
		}
	} //
	
	/**
	 * Removes all extraneous mappings from an item 
	 * @param item the item to strip of all mappings
	 */
	public synchronized void removeMappings(GraphItem item) {
		ItemEntry entry = (ItemEntry)m_entryMap.get(item.getItemClass());
		if ( entry != null ) {
			removeMappings(entry, item);
		} else {
			throw new IllegalArgumentException("Didn't recognize the item's"
						+ " item class.");				
		}
	} //
	
	private synchronized void removeMappings(ItemEntry entry, GraphItem item) {
		if ( m_entityMap.containsKey(item) ) {
			Object o = m_entityMap.get(item);
			m_entityMap.remove(item);
			if ( o instanceof Entity ) {
				entry.itemMap.remove(o);
			} else {
				Iterator iter = ((List)o).iterator();
				while ( iter.hasNext() ) {
					entry.itemMap.remove(iter.next());
				}
			}
		}		
	} //

	/**
	 * Add a graph item to the visualization queue, and add a mapping
	 * between the given entity and the item.
	 * @param entity the graph Entity to add
	 * @param item the GraphItem corresponding to the entity
	 */
	private synchronized void addItem(ItemEntry entry, Entity entity, GraphItem item) {
		addItem(entry, item);
		addMapping(entry, entity, item);		
	} //

	/**
	 * Add a graph item to the visualization queue, but do not add any new
	 * mappings.
	 * @param item the item to add the the visualization queue
	 */
	private synchronized void addItem(ItemEntry entry, GraphItem item) {
		entry.itemList.add(item);
		entry.modified = true;
		if ( m_registryListener != null ) {
    		m_registryListener.registryItemAdded(item);
		}
	} //
	
	/**
	 * Remove an item from the visualization queue.
	 * @param entry the <code>ItemEntry</code> for this item's item class.
	 * @param item the item to remove from the visualization queue
	 */
	private synchronized void removeItem(ItemEntry entry, GraphItem item) {
		removeMappings(entry, item);
		entry.itemList.remove(item);
		if ( m_registryListener != null ) {
			m_registryListener.registryItemRemoved(item);
		}
		m_ifactory.reclaim(item);
	} //

	/**
	 * Remove an item from the visualization queue.
	 * @param item the item to remove from the visualization queue
	 */
	public synchronized void removeItem(GraphItem item) {
		ItemEntry entry = (ItemEntry)m_entryMap.get(item.getItemClass());
		if ( entry != null ) {
			removeItem(entry, item);
		} else {
			throw new IllegalArgumentException("Didn't recognize the item's"
						+ " item class.");				
		}
	} //

  	// ========================================================================
  	// == LISTENER METHODS ====================================================

	/**
	 * Add an item registry listener.
	 * @param irl the listener to add.
	 */
  	public synchronized void addItemRegistryListener(ItemRegistryListener irl) {
    	m_registryListener = PrefuseRegistryEventMulticaster.add(m_registryListener, irl);
  	} //

	/**
	 * Remove an item registry listener.
	 * @param irl the listener to remove.
	 */
  	public synchronized void removeItemRegistryListener(ItemRegistryListener irl) {
    	m_registryListener = PrefuseRegistryEventMulticaster.remove(m_registryListener, irl);
  	} //
  	
	/**
	 * Add a focus listener.
	 * @param fl the listener to add.
	 */
  	public synchronized void addFocusListener(FocusListener fl) {
  		m_focusListener = PrefuseRegistryEventMulticaster.add(m_focusListener, fl);
  	} //
  	
	/**
	 * Remove a focus listener.
	 * @param fl the listener to remove.
	 */
  	public synchronized void removeFocusListener(FocusListener fl) {
  		m_focusListener = PrefuseRegistryEventMulticaster.remove(m_focusListener, fl);
  	} //


	// ========================================================================
	// == FOCUS METHODS =======================================================

	/**
	 * Return an iterator over the (usually user-selected) focus nodes. Other
	 * threads of execution may attempt to make changes to the registry while
	 * one is using this iterator, so it is strongly recommended that the
	 * resulting iterator be accessed only within an enclosing synchronized
	 * block on this registry instance. For example: 
	 * <tt>synchronized ( registry ) {
	 *        // iterator accesses...
	 * }</tt>
	 * @return an iterator over the entities currently treated 
	 *  as visualization foci
	 */
	public synchronized Iterator focusIterator() {
		return m_focusList.iterator();
	} //
	
	/**
	 * Indicates if the given entity is in the focus set.
	 * @param entity the entity to check
	 * @return true if the entity is a focus, false otherwise
	 */
	public synchronized boolean isFocus(Entity entity) {
		Iterator iter = m_focusList.iterator();
		while ( iter.hasNext() ) {
			Entity e = (Entity)iter.next();
			if ( e == entity ) {
				return true;
			}
		}
		return false;
	} //
	
	/**
	 * Indicates if the given item is associated with an entity is in 
	 *  the focus set.
	 * @param item the item to check
	 * @return true if the item's entity is a focus, false otherwise
	 */
	public synchronized boolean isFocus(GraphItem item) {
		Entity e = getEntity(item);
		return isFocus(e);
	} //
	
	/**
	 * Add a focus entity.
	 * @param entity the new focus
	 */
	public synchronized void addFocus(Entity entity) {
		m_focusList.add(entity);
		if ( m_focusListener != null ) {
			FocusEvent e = new FocusEvent(this, FocusEvent.FOCUS_ADDED, entity, null);
			m_focusListener.focusChanged(e);
		}
	} //
	
	/**
	 * Remove a focus entity.
	 * @param entity the entity to remove as a focus.
	 */
	public synchronized void removeFocus(Entity entity) {
		if ( m_focusList.remove(entity) && m_focusListener != null ) {
			FocusEvent e = new FocusEvent(this, FocusEvent.FOCUS_REMOVED, null, entity);
			m_focusListener.focusChanged(e);
		}
	} //
	
	/**
	 * Removes all foci.
	 */
	public synchronized void clearFocus() {
		for ( int i = 0; i < m_focusList.size(); ) {
			Entity entity = (Entity)m_focusList.get(i);
			removeFocus(entity);
		}
	} //
	
	/**
	 * Set the current entity as a focus, removing all other foci.
	 * @param entity the new focus
	 */
	public synchronized void setFocus(Entity entity) {
		Entity prevFocus = null;
		if ( m_focusList.size() > 0 )
			prevFocus = (Entity)m_focusList.get(0); // TODO: hacky, refactor this later
		clearFocusList();
		m_focusList.add(entity);
		if ( m_focusListener != null ) {
			FocusEvent e = new FocusEvent(this, FocusEvent.FOCUS_SET, entity, prevFocus);
			m_focusListener.focusChanged(e);
		}
	} //
	
	/**
	 * Helper method to clear the focus list.
	 */
	private synchronized void clearFocusList() {
		m_focusList.clear();
	} //

} // end of class ItemRegistry
