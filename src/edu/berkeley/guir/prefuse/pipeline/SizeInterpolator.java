package edu.berkeley.guir.prefuse.pipeline;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Linearly interpolates the size of a GraphItem.
 * 
 * Apr 27, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class SizeInterpolator extends AbstractPipelineComponent 
	implements Interpolator
{

	public static final String ATTR_ANIM_FRAC = "animationFrac";

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		double frac = getDoubleAttribute(ATTR_ANIM_FRAC);
		if ( frac == Double.NaN ) {
			throw new IllegalStateException("Animation fraction has not been set!");
		}
				
		double ss, es, s;		
		
		Iterator itemIter = m_registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			ss = item.getStartSize();
			es = item.getEndSize();						
			s = ss + frac * (es - ss);						
			item.setSize(s);
		}		
	} //

} // end of class SizeInterpolator
