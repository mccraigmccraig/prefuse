package edu.berkeley.guir.prefuse.pipeline;

import edu.berkeley.guir.prefuse.Pipeline;

/**
 * Interface for pipeline managers - components that oversee the graph
 * visualization processing pipeline. These managers control the individual
 * pipeline components as necessary.
 * 
 * Apr 28, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class PipelineManager {

	protected Pipeline m_pipeline;
	
	/**
	 * Initialize this pipeline manager.
	 * @param p the pipeline this manager is overseeing
	 */
	public void init(Pipeline p) {
		m_pipeline = p;
	} //
	
	/**
	 * Reset this pipeline manager.
	 */
	public void reset() {
		m_pipeline = null;
	} //

	/**
	 * Get the pipeline this manager oversees.
	 * @return the processing pipeline
	 */
	public Pipeline getPipeline() {
		return m_pipeline;
	} //

	/**
	 * Causes the manager to execute the processing pipeline as it sees fit.
	 * Subclasses must implement this method to perform custom pipeline
	 * management.
	 */
	public abstract void runPipeline();

} // end of abstract class PipelineManager
