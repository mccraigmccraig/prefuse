package edu.berkeley.guir.prefuse;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.util.FontLib;

/**
 * Abstract class for a visual representation of a graph element. Subclasses
 * include NodeItem, EdgeItem, and AggregateItem.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class VisualItem implements Entity {
    
    // TODO: abstract the various properties into the attributes map?
	
	protected ItemRegistry m_registry;  // the item registry this item is associated with
	protected String       m_itemClass; // the item class this item belongs to
	protected Entity       m_entity;    // the graph entity this item maps to
	protected int          m_dirty;     // used to keep track of dirty status

	protected boolean m_visible;
	protected boolean m_newlyVisible;
	
    // all non-standard viz attributes go here
	protected Map m_attrs;
    // degree-of-interest of ths item
	protected double  m_doi;
    // location attributes
	protected Point2D m_location;
	protected Point2D m_startLocation;
	protected Point2D m_endLocation;
    // color attributes
	protected Paint m_color;
	protected Paint m_startColor;
	protected Paint m_endColor;
	protected Paint m_fillColor;
	protected Paint m_startFillColor;
	protected Paint m_endFillColor;
    // size attributes
    protected double m_size;
	protected double m_startSize;
	protected double m_endSize;
    // font attributes
    protected Font m_startFont;
	protected Font m_font;
    protected Font m_endFont;
    // fix the position of this item?
    protected boolean m_fixed = false;
    
	/**
	 * Default constructor.
	 */
	public VisualItem() {
	    m_attrs         = new HashMap(5,0.9f);
		m_location      = new Point2D.Float();
		m_startLocation = new Point2D.Float();
		m_endLocation   = new Point2D.Float();
	} //
	
	public String toString() {
		return m_entity.toString();
	} //
	
	/**
	 * Initialize this VisualItem, binding it to the given
	 * ItemRegistry and Entity.
	 * @param registry the ItemRegistry monitoring this VisualItem
	 * @param entity the Entity represented by this VisualItem
	 */
	public void init(ItemRegistry registry, String itemClass, Entity entity) {
		m_itemClass = itemClass;
		m_registry = registry;
		m_entity   = entity;
		m_dirty    = 0;
		m_visible  = false;
		m_newlyVisible = false;
		m_doi = Integer.MIN_VALUE;
		
		initAttributes();
	} //
	
    protected void initAttributes() {
        // general viz attributes
        m_attrs.clear();
        // location
        m_location.setLocation(0,0);
        m_startLocation.setLocation(0,0);
        m_endLocation.setLocation(0,0);
        // colors
        m_color          = Color.BLACK;
        m_startColor     = Color.BLACK;
        m_endColor       = Color.BLACK;
        m_fillColor      = Color.LIGHT_GRAY;
        m_startFillColor = Color.LIGHT_GRAY;
        m_endFillColor   = Color.LIGHT_GRAY;
        // sizes
        m_size      = 1;
        m_startSize = 1;
        m_endSize   = 1;
        // fonts
        m_startFont = FontLib.getFont("SansSerif",Font.PLAIN,10);
        m_font      = m_startFont;
        m_endFont   = m_startFont;
    }
    
	/**
	 * Clear the state of this VisualItem.
	 */
	public void clear() {
		m_registry = null;
		m_entity   = null;
        initAttributes();
	} //

	/**
	 * Return the ItemRegistry associated with this VisualItem.
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
	 * Return the Entity this VisualItem represents.
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
     * @see edu.berkeley.guir.prefuse.graph.Entity#getAttributes()
     */
    public Map getAttributes() {
        return m_entity.getAttributes();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.Entity#setAttributes(java.util.Map)
     */
    public void setAttributes(Map attrMap) {
        m_entity.setAttributes(attrMap);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.Entity#clearAttributes()
     */
    public void clearAttributes() {
        m_entity.clearAttributes();
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
	 * Indicates if this VisualItem is currently visible in the visualization.
	 * @return true if visible, false otherwise
	 */
	public boolean isVisible() {
		return m_visible;
	} //

	/**
	 * Sets the visible status of this VisualItem. If set false, this item will
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
	 * Get the renderer for drawing this VisualItem.
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
	 * Returns true if the given point is contained within this VisualItem.
	 * @param point the point to test for containment
	 * @return true if the point is within this VisualItem
	 */
	public boolean locatePoint(Point2D point) {
		return getRenderer().locatePoint(point, this);
	} //
	
	/**
	 * Returns the bounding box of this VisualItem, determined by it's renderer.
	 * @return a Rectangle representing the bounding box for this VisualItem
	 */
	public Rectangle getBounds() {
		return getRenderer().getBoundsRef(this);
	} //


	// == convenience methods for viz attributes ==============================

    public boolean isFixed() {
        return m_fixed;
    } //
    
    public void setFixed(boolean s) {
        m_fixed = s;
    } //
    
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
	
	public Font getStartFont() {
		return m_startFont;
	} //
	
	public void setStartFont(Font f) {
		m_startFont = f;
	} //
    
    public Font getFont() {
        return m_font;
    } //
    
    public void setFont(Font f) {
        m_font = f;
    } //
    
    public Font getEndFont() {
        return m_endFont;
    } //
    
    public void setEndFont(Font f) {
        m_endFont = f;
    } //
    
    public void updateFont(Font f) {
        m_startFont = m_font;
        m_endFont = f;
    } //

} // end of abstract class VisualItem
