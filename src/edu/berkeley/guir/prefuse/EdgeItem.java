package edu.berkeley.guir.prefuse;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Visual representation of an edge in a graph.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class EdgeItem extends GraphItem {

	protected NodeItem m_node1;
	protected NodeItem m_node2;

	/**
	 * Initialize this EdgeItem, binding it to the
     *  given ItemRegistry and Entity.
	 * @param registry the ItemRegistry monitoring this GraphItem
	 * @param entity the Entity represented by this GraphItem
	 */
	public void init(ItemRegistry registry, String itemClass, Entity entity) {
		if ( entity != null && !(entity instanceof Edge) ) {
			throw new IllegalArgumentException("EdgeItem can only represent an Entity of type Edge.");
		}
		super.init(registry, itemClass, entity);
		
		Edge edge = (Edge)entity;
		Entity n1 = edge.getFirstNode();
		Entity n2 = edge.getSecondNode();
		
		NodeItem item1 = getItem(n1);
		setFirstNode(item1);
		NodeItem item2 = getItem(n2);
		setSecondNode(item2);
	} //
	
	protected NodeItem getItem(Entity n) {
		NodeItem item = null;
		if ( n instanceof Node ) {
			item = m_registry.getNodeItem((Node)n);
		}
		return item;
	} //

    /**
     * Indicates whether or not this edge is a directed edge.
     * @return true if this edge is directed, false otherwise
     */
	public boolean isDirected() {
		return ((Edge)m_entity).isDirected();
	} //
	
    /**
     * Given a node item incident on this edge, returns the other node item
     * incident on this edge.
     * @param nitem a NodeItem incident on this edge
     * @return the other NodeItem incident on this edge
     * @throws IllegalArgumentException if the provided NodeItem is not
     *  incident on this edge.
     */
    public NodeItem getOtherNode(NodeItem nitem) {
        if ( m_node1 == nitem )
            return m_node2;
        else if ( m_node2 == nitem )
            return m_node1;
        else
            throw new IllegalArgumentException("Input NodeItem is incident on this Edge.");
    } //
    
	/**
	 * Return the GraphItem representing the first (source) node in the edge.
	 * @return the first (source) GraphItem
	 */
	public NodeItem getFirstNode() {
		return m_node1;
	} //
	
	/**
	 * Set the GraphItem representing the first (source) node in the edge.
	 * @param item the first (source) GraphItem
	 */
	public void setFirstNode(NodeItem item) {
		m_node1 = item;
	} //
	
	/**
	 * Return the GraphItem representing the second (target) node in the edge.
	 * @return the second (target) GraphItem
	 */
	public NodeItem getSecondNode() {
		return m_node2;
	} //
	
	/**
	 * Set the NodeItem representing the second (target) node in the edge.
	 * @param item the second (target) NodeItem
	 */
	public void setSecondNode(NodeItem item) {
		m_node2 = item;
	} //

} // end of class EdgeItem
