package edu.berkeley.guir.prefuse;

import java.util.ArrayList;
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
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.event.ItemRegistryListener;
import edu.berkeley.guir.prefuse.event.RegistryEventMulticaster;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.RendererFactory;
import edu.berkeley.guir.prefuse.util.FocusSet;

/**
 * The ItemRegistry is the central data structure for a prefuse visualization.
 * The registry maintains mappings between abstract graph data (e.g., 
 * <tt>Node</tt>s and <tt>Edges</tt>) and their visual representations (e.g.,
 * <tt>NodeItem</tt>s and <tt>EdgeItem</tt>s). The ItemRegistry maintains
 * rendering queues of all visualized GraphItems, a comparator for ordering
 * these queues (and thus controlling rendering order), references to all
 * displays that render the contents of this registry, and a focus manager
 * keeping track of focus sets of Entity instances. In addition, the
 * ItemRegistry supports garbage collection of GraphItems across interaction
 * cycles of a visualization, allowing visual representations of graph
 * elements to pass in and out of existence as necessary.
 * <br/><br/>
 * GraphItems are not instantiated directly, instead they are created by
 * the ItemRegistry as visual representations for abstract graph data. To
 * create a new GraphItem or retrieve an existing one, use the provided
 * ItemRegistry methods (e.g., getItem(), getNodeItem, etc). These are the
 * methods used by the various filters in the edu.berkeley.guir.prefuse.actions
 * package to determine which graph elements are visualized and which are not.
 * <br/><br/>
 * For convenience, the ItemRegistry creates entries for three types of
 * GraphItems: NodeItems, EdgeItems, and AggregateItems. The mappings and
 * rendering queues for these entries can be accessed through convenience
 * methods such as getNodeItem(), getEdgeItems, etc. More generally, separate
 * entries with their own mappings and rendering queue can be made for any 
 * type of GraphItem by using the addItemClass() methods. For example, if
 * there are more than two different types of aggregates used (e.g., subtree
 * aggregates and aggregates of other nodes) it may facilitate design to
 * separate these into their own item classes.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ItemRegistry {

	public static final String DEFAULT_NODE_CLASS = "node";
	public static final String DEFAULT_EDGE_CLASS = "edge";
	public static final String DEFAULT_AGGR_CLASS = "aggregate";
	public static final int    DEFAULT_MAX_ITEMS  = 10000;
	public static final int    DEFAULT_MAX_DIRTY  = 1;
	
	/**
	 * Wrapper class that holds all the data structures for managing
	 * a class of GraphItems.
	 */
	public class ItemEntry {
		ItemEntry(String itemClass, Class classType, int dirty) {
			try {
				name     = itemClass;
				type     = classType;
				itemList = new LinkedList();
				itemMap  = new HashMap();
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
	
    private List            m_displays;
    private Graph           m_graph;
    private FocusManager    m_fmanager;
	private ItemFactory     m_ifactory;
	private RendererFactory m_rfactory;
	
	private List m_entryList; // list of ItemEntry instances
	private Map  m_entryMap;  // maps from item class names to ItemEntry instances
	private Map  m_entityMap; // maps from items back to their entities
	
	private Comparator m_comparator;
  
  	private ItemRegistryListener m_registryListener;
  	private FocusListener        m_focusListener;
	
	/**
	 * Constructor. Creates an empty ItemRegistry and corresponding ItemFactory.
	 * By default, creates queues and <code>ItemFactory</code> entries for
	 * handling NodeItems, EdgeItems, and AggregateItems, respectively. All
	 * are given default settings, including a maxDirty value of 1.
	 */
	public ItemRegistry(Graph g) {
		this(g, true);
	} //
	
	public ItemRegistry(Graph g, boolean initDefault) {
        m_graph = g;
        m_displays = new ArrayList();
        m_fmanager = new FocusManager();
		try {
			m_ifactory  = new ItemFactory();
			m_rfactory  = new DefaultRendererFactory();
			m_entryList = new LinkedList();
			m_entryMap  = new HashMap();
			m_entityMap = new HashMap();
			m_comparator = new DefaultItemComparator();
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
	
    public synchronized Graph getGraph() {
        return m_graph;
    } //
    
    public synchronized void setGraph(Graph g) {
        // TODO: invalidate all current entries?
        m_graph = g;
    } //
    
    public synchronized void addDisplay(Display d) {
        if ( !m_displays.contains(d) )
            m_displays.add(d);
    } //
    
    public synchronized boolean removeDisplay(Display d) {
        return m_displays.remove(d);
    } //
    
    public synchronized Display getDisplay(int i) {
        return (Display)m_displays.get(i);
    } //
    
    public synchronized List getDisplaysRef() {
        return m_displays;
    } //
    
    public synchronized FocusManager getFocusManager() {
        return m_fmanager;
    } //
    
    public synchronized FocusSet getDefaultFocusSet() {
        return m_fmanager.getDefaultFocusSet();
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
        Iterator iter = entry.itemList.iterator();
        while ( iter.hasNext() ) {
            GraphItem item = (GraphItem)iter.next();
            int dirty = item.getDirty()+1;
            item.setDirty(dirty);
            if ( entry.maxDirty > -1 && dirty > entry.maxDirty ) {
                iter.remove();
                removeItem(entry, item, false);
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
			this.removeItem(entry, item, true);
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
	 * @param entity the Entity to look up
	 * @return the AggregateItem associated with the entity, if any
	 */
	public synchronized AggregateItem getAggregateItem(Entity entity) {
		return (AggregateItem)getItem(DEFAULT_AGGR_CLASS, entity, false);
	} //
	
	/**
	 * Returns the visualized AggregateItem associated with the given Entity, if
	 * any. If create is true, creates the desired AggregateItem and adds it to
	 * the registry, and removes any previous bindings associated with the
	 * Entity.
	 * @param entity the Entity to look up
	 * @param create if true, a new AggregateItem will be allocated if 
	 *  necessary
	 * @return AggregateItem the AggregateItem associated with the entity, if any
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
     * @param lr indicates whether or not to remove the item from it's
     *  rendering queue. This option is available to avoid errors that
     *  arise when removing items coming from a currently active Iterator.
	 */
	private synchronized void removeItem(ItemEntry entry, GraphItem item, boolean lr) {
		removeMappings(entry, item);
		if (lr) entry.itemList.remove(item);
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
			removeItem(entry, item, true);
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
    	m_registryListener = RegistryEventMulticaster.add(m_registryListener, irl);
  	} //

	/**
	 * Remove an item registry listener.
	 * @param irl the listener to remove.
	 */
  	public synchronized void removeItemRegistryListener(ItemRegistryListener irl) {
    	m_registryListener = RegistryEventMulticaster.remove(m_registryListener, irl);
  	} //

} // end of class ItemRegistry
