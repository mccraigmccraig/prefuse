package edu.berkeley.guir.prefuse.pipeline;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.Pipeline;
import edu.berkeley.guir.prefuse.graph.Graph;

/**
 * Represents an item in the graph processing pipeline. Most, if not all,
 * pipeline components will want to subclass this abstract class.
 * 
 * Apr 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class AbstractPipelineComponent implements PipelineComponent {
	
	protected Pipeline     m_pipeline;
	protected ItemRegistry m_registry;
	protected Display      m_display;
	protected Graph        m_graph;
	
	protected boolean     m_enabled;
	
	/**
	 * Default constructor.
	 */
	public AbstractPipelineComponent() {
		m_enabled = true;
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#init(edu.berkeley.guir.prefuse.Pipeline)
	 */
	public void init(Pipeline pipeline) {
		m_pipeline = pipeline;
		m_registry = pipeline.getItemRegistry();
		m_display  = pipeline.getDisplay();
		m_graph    = pipeline.getGraph();
		m_enabled  = true;
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#reset()
	 */
	public void reset() {
		m_pipeline = null;
		m_registry = null;
		m_display  = null;
		m_graph    = null;
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#runComponent()
	 */
	public void runComponent() {
		if ( m_enabled ) {			
			process();		
		}		
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#process()
	 */
	public abstract void process();

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#isEnabled()
	 */
	public boolean isEnabled() {
		return m_enabled;
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean s) {
		m_enabled = s;
	} //

	/**
	 * Get a pipline attribute. Pipeline attributes are variables that are
	 * accessible to all pipeline components.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	protected Object getAttribute(String name) {
		return m_pipeline.getAttribute(name);
	} //

	/**
	 * Set a pipline attribute. Pipeline attributes are variables that are
	 * accessible to all pipeline components.
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	protected void setAttribute(String name, Object value) {
		m_pipeline.setAttribute(name, value);
	} //

	/**
	 * Indicates if the specified pipeline attribute has been set.
	 * @param name the name of the attribute
	 * @return true if it has been set, false otherwise
	 */
	protected boolean hasAttribute(String name) {
		return ( getAttribute(name) != null );
	} //

	/**
	 * Get an integer pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	protected int getIntegerAttribute(String name) {
		return m_pipeline.getIntegerAttribute(name);		
	} //
	
	/**
	 * Get a boolean pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	protected boolean getBooleanAttribute(String name) {
		return m_pipeline.getBooleanAttribute(name);
	} //
	
	/**
	 * Get a single-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	protected float getFloatAttribute(String name) {
		return m_pipeline.getFloatAttribute(name);	
	} //
	
	/**
	 * Get a double-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @return the value of the attribute
	 */
	protected double getDoubleAttribute(String name) {
		return m_pipeline.getDoubleAttribute(name);	
	} //
	
	/**
	 * Set an integer pipeline attribute.
	 * @param name the name of the attribute
	 * @param i the value of the attribute
	 */
	protected void setIntegerAttribute(String name, int i) {
		m_pipeline.setIntegerAttribute(name, i);
	} //
	
	/**
	 * Set a boolean pipeline attribute.
	 * @param name the name of the attribute
	 * @param b the value of the attribute
	 */
	protected void setBooleanAttribute(String name, boolean b) {
		m_pipeline.setBooleanAttribute(name, b);
	} //
	
	/**
	 * Set a single-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @param f the value of the attribute
	 */
	protected void setFloatAttribute(String name, float f) {
		m_pipeline.setFloatAttribute(name, f);
	} //
	
	/**
	 * Set a double-precision floating point pipeline attribute.
	 * @param name the name of the attribute
	 * @param d the value of the attribute
	 */
	protected void setDoubleAttribute(String name, double d) {
		m_pipeline.setDoubleAttribute(name, d);
	} //
	
} // end of abstract class PipelineComponent
