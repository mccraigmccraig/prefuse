package edu.berkeley.guir.prefuse.pipeline;

/**
 * Signals the <code>ItemRegistry</code> to perform a garbage collection 
 * operation. The class type of the <code>GraphItem</code> to garbage
 * collect must be specified through the <code>setType()</code> method.
 * 
 * Jul 15, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GarbageCollector extends AbstractPipelineComponent 
	implements ProcessingComponent
{
	private String m_itemClass;

	public GarbageCollector() {
		super();
	} //
	
	public GarbageCollector(String itemClass) {
		m_itemClass = itemClass;
	} //

	public String getItemClass() {
		return m_itemClass;
	} //
	
	public void setItemClass(String itemClass) {
		m_itemClass = itemClass;
	} //

	/**
	 * Make the proper garbage collecting call.
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#process()
	 */
	public void process() {
		m_registry.garbageCollect(m_itemClass);
	} //

} // end of class GarbageCollector
