package edu.berkeley.guir.prefuse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.pipeline.DefaultPipelineManager;
import edu.berkeley.guir.prefuse.pipeline.PipelineComponent;
import edu.berkeley.guir.prefuse.pipeline.PipelineManager;

/**
 * The pipeline oversees the processing of the graph data into
 * individual components for visualization.
 * 
 * Apr 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class Pipeline {

	private static final Class LIST_TYPE    = LinkedList.class;
	private static final Class MAP_TYPE     = HashMap.class;
	private static final Class MANAGER_TYPE = DefaultPipelineManager.class; 

	private PipelineManager m_manager;
	private List            m_pipeline;
	private Map             m_attributes;
	private ItemRegistry    m_registry;
	private Graph           m_graph;
	private Display         m_display;
	
	/**
	 * Pipeline constructor. Creates a default ItemRegistry.
	 * @param g
	 */
	public Pipeline(Graph g, Display display) {
		this(g, display, new ItemRegistry());
	} //
	
	public Pipeline(Graph g, Display display, ItemRegistry registry) {
		try {
			m_manager    = (PipelineManager)MANAGER_TYPE.newInstance();
			m_pipeline   = (List)LIST_TYPE.newInstance();
			m_attributes = (Map)MAP_TYPE.newInstance();
		} catch ( Exception e ) {
			e.printStackTrace();
		}		
		m_registry = registry;
		m_graph    = g;
		m_display  = display;
		m_manager.init(this);		
		m_display.setPipeline(this);
	} //
	
	/**
	 * Sets the pipeline manager used to oversee pipeline execution.
	 * @param manager the pipeline manager to use
	 */
	public void setPipelineManager(PipelineManager manager) {
		m_manager.reset();
		m_manager = manager;
		m_manager.init(this);
	} //
	
	/**
	 * Runs the processing pipeline
	 */
	public void runPipeline() {
		synchronized ( m_registry ) {
			m_manager.runPipeline();			
		}		
	} //
	
	/**
	 * Private helper to reinitialize the pipeline components when
	 * pipeline state is changed (e.g. when associated with a new graph).
	 */
	public void reinit() {
		m_manager.init(this);
		Iterator iter = m_pipeline.iterator();
		while ( iter.hasNext() ) {
			PipelineComponent pc = (PipelineComponent)iter.next();
			pc.reset();
			pc.init(this);
		}
	} //
	
	// ========================================================================
	// == ACCESSOR / MUTATOR METHODS ==========================================
	
	/**
	 * Returns the graph processed by this pipeline.
	 * @return Graph
	 */
	public Graph getGraph() {
		return m_graph;
	} //
	
	/**
	 * Sets the graph processed by this pipeline.
	 * @param g the Graph to associate with the pipeline.
	 */
	public void setGraph(Graph g) {
		m_graph = g;
		reinit();
	} //
	
	/**
	 * Returns the display driven by this pipeline.
	 * @return Display the display associated with this pipeline
	 */
	public Display getDisplay() {
		return m_display;
	} //
	
	/**
	 * Sets the display driven by this pipeline.
	 * @param display the display associated with this pipeline
	 */
	public void setDisplay(Display display) {
		m_display = display;
		reinit();
	} //
	
	/**
	 * Returns the item registry used to store visualized graph items.
	 * @return ItemRegistry this pipeline's item registry
	 */
	public ItemRegistry getItemRegistry() {
		return m_registry;
	} //
	
	/**
	 * Add a PipelineComponent to the end of the pipeline.
	 * @param c the PipelineComponent to add
	 */
	public void addComponent(PipelineComponent c) {		
		c.init(this);
		m_pipeline.add(c);
	} //
	
	/**
	 * Insert a PipelineComponent into the pipeline at the specified position.
	 * @param i the index into the pipeline list
	 * @param c the PipelineComponent to add
	 */
	public void addComponent(int i, PipelineComponent c) {		
		c.init(this);
		m_pipeline.add(i, c);
	} //
	
	/**
	 * Return the PipelineComponent at the specified position.
	 * @param i the index into the pipeline list
	 * @return PipelineComponent the component at the specified position
	 */
	public PipelineComponent getComponent(int i) {
		return (PipelineComponent)m_pipeline.get(i);
	} //
	
	/**
	 * Returns an in-order iterator of the components of this pipeline.
	 * @return Iterator
	 */
	public Iterator getComponents() {
		return m_pipeline.iterator();
	} //

	/**
	 * Get a pipline attribute. Pipeline attributes are variables that are
	 * accessible to all pipeline components.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	public Object getAttribute(String name) {
		return m_attributes.get(name);
	} //

	/**
	 * Set a pipline attribute. Pipeline attributes are variables that are
	 * accessible to all pipeline components.
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setAttribute(String name, Object value) {
		m_attributes.put(name, value);
	} //

	/**
	 * Get an integer pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	public int getIntegerAttribute(String name) {
		Integer i = (Integer)getAttribute(name);
		if ( i != null ) {
			return i.intValue();
		} else {
			return Integer.MIN_VALUE;
		}
	} //

	/**
	 * Get a boolean pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	public boolean getBooleanAttribute(String name) {
		Boolean b = (Boolean)getAttribute(name);
		if ( b != null ) {
			return b.booleanValue();
		} else {
			return false;
		}
	} //

	/**
	 * Get a single-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	public float getFloatAttribute(String name) {
		Float f = (Float)getAttribute(name);
		if ( f != null ) {
			return f.floatValue();
		} else {
			return Float.NaN;
		}
	} //

	/**
	 * Get a double-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	public double getDoubleAttribute(String name) {
		Double d = (Double)getAttribute(name);
		if ( d != null ) {
			return d.doubleValue();
		} else {
			return Double.NaN;
		}
	} //

	/**
	 * Set an integer pipeline attribute.
	 * @param name the name of the attribute
	 * @param i the value of the attribute
	 */
	public void setIntegerAttribute(String name, int i) {
		setAttribute(name, new Integer(i));
	} //

	/**
	 * Set a boolean pipeline attribute.
	 * @param name the name of the attribute
	 * @param b the value of the attribute
	 */
	public void setBooleanAttribute(String name, boolean b) {
		setAttribute(name, new Boolean(b));
	} //

	/**
	 * Set a single-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @param f the value of the attribute
	 */
	public void setFloatAttribute(String name, float f) {
		setAttribute(name, new Float(f));
	} //

	/**
	 * Set a double-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @param d the value of the attribute
	 */
	public void setDoubleAttribute(String name, double d) {
		setAttribute(name, new Double(d));
	} //

} // end of class Pipeline
