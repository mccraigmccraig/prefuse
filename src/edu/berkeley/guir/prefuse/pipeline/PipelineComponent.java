package edu.berkeley.guir.prefuse.pipeline;

import edu.berkeley.guir.prefuse.Pipeline;

/**
 * Interface which all PipelineComponents must implement.
 * 
 * Apr 30, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface PipelineComponent {
	
	/**
	 * Initialize this component.
	 * @param pipeline this component's parent pipeline
	 */
	public void init(Pipeline pipeline);
	
	/**
	 * Reset this component.
	 */
	public void reset();
	
	/**
	 * Run this component, performing any requisite processing.
	 */
	public void runComponent();
	
	/**
	 * Perform component processing. Called only by runComponent().
	 */
	public void process();
	
	/**
	 * Indicates whether or not this component is currently enabled.
	 * @return true if enabled, false otherwise
	 */
	public boolean isEnabled();
	
	/**
	 * Sets whether this component is enabled.
	 * @param s true to enable component, false to disable it
	 */
	public void setEnabled(boolean s);
	
} // end of interface PipelineComponent