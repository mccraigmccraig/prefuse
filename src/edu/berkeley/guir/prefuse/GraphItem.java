package edu.berkeley.guir.prefuse;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.render.Renderer;

/**
 * Abstract class for representing an entity to be visualized. Subclasses
 * include Node, Edge, and Aggregate.
 * 
 * TODO: abstract the various properties into the attributes map?
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class GraphItem {
	
	public static final Class POINT_TYPE = Point2D.Float.class;
	public static final Class MAP_TYPE   = HashMap.class; 
	
	protected ItemRegistry m_registry;  // the item registry this item is assoicated with
	protected String       m_itemClass; // the item class this item belongs to
	protected Entity       m_entity;    // the graph entity this item maps to
	protected int          m_dirty;     // used to keep track of dirty status

	protected boolean m_visible;
	protected boolean m_newlyVisible;
	
	protected Map m_attrs;
	protected double  m_doi;
	protected Point2D m_location;
	protected Point2D m_startLocation;
	protected Point2D m_endLocation;
	protected Paint   m_color;
	protected Paint   m_startColor;
	protected Paint   m_endColor;
	protected Paint   m_fillColor;
	protected Paint   m_startFillColor;
	protected Paint   m_endFillColor;
	protected double m_size;
	protected double m_startSize;
	protected double m_endSize;
	protected Font   m_font;
	
	/**
	 * Default constructor.
	 */
	public GraphItem() {
		try {
			m_attrs         = (Map)MAP_TYPE.newInstance();
			m_location      = (Point2D)POINT_TYPE.newInstance();
			m_startLocation = (Point2D)POINT_TYPE.newInstance();
			m_endLocation   = (Point2D)POINT_TYPE.newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //
	
	public String toString() {
		return m_entity.toString();
	} //
	
	/**
	 * Initialize this GraphItem, binding it to the given
	 * ItemRegistry and Entity.
	 * @param registry the ItemRegistry monitoring this GraphItem
	 * @param entity the Entity represented by this GraphItem
	 */
	public void init(ItemRegistry registry, String itemClass, Entity entity) {
		m_itemClass = itemClass;
		m_registry = registry;
		m_entity   = entity;
		m_dirty    = 0;
		m_visible  = false;
		m_newlyVisible = false;
		m_doi = Integer.MIN_VALUE;
		
		/// XXX DEBUG
		//System.out.println("Initializing Item: " + this.getClass().getName()
		//	 + " - " + this.getAttribute("Text"));
	} //
	
	/**
	 * Clear the state of this GraphItem.
	 */
	public void clear() {
		m_registry = null;
		m_entity   = null;
		m_attrs.clear();
		
		m_location.setLocation(0,0);
		m_startLocation.setLocation(0,0);
		m_endLocation.setLocation(0,0);
		m_color = null;
		m_startColor = null;
		m_endColor = null;
		m_fillColor = null;
		m_startFillColor = null;
		m_endFillColor = null;
		m_size = 0;
		m_startSize = 0;
		m_endSize = 0;
	} //

	/**
	 * Return the ItemRegistry associated with this GraphItem.
	 * @return the ItemRegistry
	 */
	public ItemRegistry getItemRegistry() {
		return m_registry;
	} //

	/**
	 * Return the item class this item belongs to.
	 * @return String label of this item's item class
	 */
	public String getItemClass() {
		return m_itemClass;
	} //

	/**
	 * Return the Entity this GraphItem represents.
	 * @return the Entity
	 */
	public Entity getEntity() {
		return m_entity;
	} //

	/**
	 * Get an entity attribute (a value associated with the actual graph data structure).
	 * @param name the name of the attribute
	 * @return String
	 */
	public String getAttribute(String name) {
		if ( m_entity == null ) {
			throw new IllegalStateException("This item has no assigned entity.");
		} else {
			return m_entity.getAttribute(name);
		}		
	} //

	/**
	 * Set an entity attribute (a value associated with the actual graph data structure).
	 * @param name the name of the attribute
	 * @param value
	 */
	public void setAttribute(String name, String value) {
		if ( m_entity == null ) {
			throw new IllegalStateException("This item has no assigned entity.");
		} else {
			m_entity.setAttribute(name, value);
		}		
	} //
	
	/**
	 * Get a visualization attribute for this item.
	 * @param name the name of the attribute
	 * @return String
	 */
	public Object getVizAttribute(String name) {
		return m_attrs.get(name);
	} //
	
	/**
	 * Set a visualization attribute for this item.
	 * @param name the name of the attribute
	 * @param value
	 */
	public void setVizAttribute(String name, Object value) {
		m_attrs.put(name, value);
	} //
	
	/**
	 * Remove a visualization attribute for this item.
	 * @param name the name of the attribute
	 */
	public void removeVizAttribute(String name) {
		m_attrs.remove(name);
	} //
	
	/**
	 * Updates an interpolated attribute, setting the current value of the attribute as the
	 * start value, and setting the specified value as the ending value. The current value is
	 * left unchanged.
	 * @param name - the name of the attribute (e.g. color)
	 * @param startName - the name of the start value for the attribute (e.g. startColor)
	 * @param endName - the name of the end value for the attribute (e.g. endColor)
	 * @param value - the new ending value of the attribute.
	 */
	public void updateVizAttribute(String name, String startName, String endName, Object value) {
		Object curVal = getVizAttribute(name);
		setVizAttribute(startName, curVal);
		setVizAttribute(endName, value);
	} //

	/**
	 * "Touching" an item tells the system that the item is to be used
	 * in the visualization, and so resets any garbage collecting data
	 * to it's freshest state. If an item has been retrieved using
	 * the <code>ItemRegistry.getNodeItem(node, true)</code> method it is
	 * touched automatically.
	 */
	public void touch() {
		m_dirty = 0;
	} //

	/**
	 * Gets the dirty counter for this item. This counter is used
	 * by the ItemRegistry to control garbage collection. 
	 * @return the dirty counter
	 */
	public int getDirty() {
		return m_dirty;
	} //
	
	/**
	 * Sets the dirty counter for this item. This counter is used
	 * by the ItemRegistry to control garbage collection. 
	 * @param dirty the new value of the dirty counter
	 */
	public void setDirty(int dirty) {
		m_dirty = dirty;
	} //

	/**
	 * Returns true if this item became visible within the last
	 * processing cycle.
	 * @return true if newly visible, false otherwise
	 */
	public boolean isNewlyVisible() {
		return m_newlyVisible;
	} //

	/**
	 * Indicates if this GraphItem is currently visible in the visualization.
	 * @return true if visible, false otherwise
	 */
	public boolean isVisible() {
		return m_visible;
	} //

	/**
	 * Sets the visible status of this GraphItem. If set false, this item will
	 * not be visited by the rendering loop.
	 * @param s the new visibility status of this item.
	 */
	public void setVisible(boolean s) {
		m_newlyVisible = ( !m_visible && s );
		m_visible = s;
	} //

    public boolean isFocus() {
        FocusManager fmanager = m_registry.getFocusManager();
        return fmanager.isFocus(m_entity);
    } //
    
	/**
	 * Get the renderer for drawing this GraphItem.
	 * @return this item's Renderer
	 */
	public Renderer getRenderer() {
		try {
			return m_registry.getRendererFactory().getRenderer(this);
		} catch ( Exception e ) {
			System.out.println("processing reclaimed item!!! -- " + this.getClass().getName());
			//e.printStackTrace();
		}
		return null;
	} //
	
	/**
	 * Returns true if the given point is contained within this GraphItem.
	 * @param point the point to test for containment
	 * @return true if the point is within this GraphItem
	 */
	public boolean locatePoint(Point2D point) {
		return getRenderer().locatePoint(point, this);
	} //
	
	/**
	 * Returns the bounding box of this GraphItem, determined by it's renderer.
	 * @return a Rectangle representing the bounding box for this GraphItem
	 */
	public Rectangle getBounds() {
		return getRenderer().getBoundsRef(this);
	} //


	// == convenience methods for viz attributes ==============================

	public double getDOI() { return m_doi; } //
	
	public void setDOI(double doi) { m_doi = doi; } //

	public Point2D getStartLocation() {
		return m_startLocation;
	} //

	public Point2D getEndLocation() {
		return m_endLocation;
	} //
	
	public Point2D getLocation() {
		return m_location;
	} //

	public void setLocation(Point2D loc) {
		m_location.setLocation(loc);
	} //

	public void setLocation(double x, double y) {
		m_location.setLocation(x,y);
	} //

	public void setStartLocation(Point2D loc) {
		m_startLocation.setLocation(loc);
	} //

	public void setStartLocation(double x, double y) {
		m_startLocation.setLocation(x,y);
	} //

	public void setEndLocation(Point2D loc) {
		m_endLocation.setLocation(loc);
	} //

	public void setEndLocation(double x, double y) {
		m_endLocation.setLocation(x,y);
	} //

	public void updateLocation(Point2D loc) {
		m_startLocation.setLocation(m_location);
		m_endLocation.setLocation(loc);
	} //

	public void updateLocation(double x, double y) {
		m_startLocation.setLocation(m_location);
		m_endLocation.setLocation(x,y);
	} //
	
	public double getX() {
		return m_location.getX();
	} //
	
	public double getY() {
		return m_location.getY();
	} //
	
	public Paint getStartColor() {
		return m_startColor;
	} //
	
	public Paint getEndColor() {
		return m_endColor;
	} //
	
	public Paint getColor() {
		return m_color;
	} //	
	
	public void setColor(Paint color) {
		m_color = color;
	} //
	
	public void updateColor(Paint color) {
		m_startColor = m_color;
		m_endColor = color;
	} //
	
	public Paint getStartFillColor() {
		return m_startFillColor;
	} //
	
	public Paint getEndFillColor() {
		return m_endFillColor;
	} //
	
	public Paint getFillColor() {
		return m_fillColor;
	} //	
	
	public void setFillColor(Paint color) {
		m_fillColor = color;
	} //
	
	public void updateFillColor(Paint color) {
		m_startFillColor = m_fillColor;
		m_endFillColor = color;
	} //
	
	public double getStartSize() {
		return m_startSize;
	} //
	
	public double getEndSize() {
		return m_endSize;
	} //
	
	public double getSize() {
		return m_size;
	} //
	
	public void setSize(double size) {
		m_size = size;
	} //
	
	public void setStartSize(double size) {
		m_startSize = size;
	} //

	public void setEndSize(double size) {
		m_endSize = size;
	} //
			
	public void updateSize(double size) {
		m_startSize = m_size;
		m_endSize = size;
	} //
	
	public Font getFont() {
		return m_font;
	} //
	
	public void setFont(Font f) {
		m_font = f;
	} //

} // end of abstract class GraphItem
