package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.NodeItem;

/**
 * Factory from which to retrieve GraphItem renderers. Assumes only one type
 * of renderer each for NodeItems, EdgeItems, and AggregateItems.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class DefaultRendererFactory implements RendererFactory {

	private Renderer m_nodeRenderer;
	private Renderer m_edgeRenderer;
	private Renderer m_aggrRenderer;

	/**
	 * Default constructor. Assumes default renderers for each GraphItem type.
	 */
	public DefaultRendererFactory() {
		this(new DefaultNodeRenderer(),
		     new DefaultEdgeRenderer(),
		     new DefaultAggregateRenderer());
	} //
	
	/**
	 * Constructor.
	 * @param nodeRenderer the Renderer to use for NodeItems
	 * @param edgeRenderer the Renderer to use for EdgeItems
	 * @param aggrRenderer the Renderer to use for AggregateItems
	 */
	public DefaultRendererFactory(Renderer nodeRenderer, 
								  Renderer edgeRenderer, 
								  Renderer aggrRenderer)
	{
		m_nodeRenderer = nodeRenderer;
		m_edgeRenderer = edgeRenderer;
		m_aggrRenderer = aggrRenderer;
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.RendererFactory#getRenderer(edu.berkeley.guir.prefuse.GraphItem)
	 */
	public Renderer getRenderer(GraphItem item) {
		if ( item instanceof NodeItem ) {
			return m_nodeRenderer;
		} else if ( item instanceof EdgeItem ) {			
			return m_edgeRenderer;
		} else if ( item instanceof AggregateItem ) {
			return m_aggrRenderer;
		} else {
			return null;
		}
	} //
	
	/**
	 * @return
	 */
	public Renderer getAggregateRenderer() {
		return m_aggrRenderer;
	} //

	/**
	 * @return
	 */
	public Renderer getEdgeRenderer() {
		return m_edgeRenderer;
	} //

	/**
	 * @return
	 */
	public Renderer getNodeRenderer() {
		return m_nodeRenderer;
	} //

	/**
	 * @param renderer
	 */
	public void setAggregateRenderer(Renderer renderer) {
		m_aggrRenderer = renderer;
	} //

	/**
	 * @param renderer
	 */
	public void setEdgeRenderer(Renderer renderer) {
		m_edgeRenderer = renderer;
	} //

	/**
	 * @param renderer
	 */
	public void setNodeRenderer(Renderer renderer) {
		m_nodeRenderer = renderer;
	} //

} // end of class DefaultRendererFactory
