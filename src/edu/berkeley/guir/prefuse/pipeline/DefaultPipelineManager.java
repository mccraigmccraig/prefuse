package edu.berkeley.guir.prefuse.pipeline;

import java.util.Iterator;

/**
 * This manager simply runs all the pipeline components in order.
 * 
 * Apr 28, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class DefaultPipelineManager extends PipelineManager {

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineManager#runPipeline(java.util.List)
	 */
	public void runPipeline()
	{
		processComponents(m_pipeline.getComponents());
		m_pipeline.getDisplay().repaint();
	} //

	/**
	 * Helper method that runs each pipeline component in order.
	 * @param components
	 */
	protected void processComponents(Iterator components)
	{	
		synchronized ( this.getPipeline().getItemRegistry() ) {			
			while ( components.hasNext() ) {
				try {
					((PipelineComponent)components.next()).runComponent();
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		}
	} //

} // end of class DefaultPipelineManager
