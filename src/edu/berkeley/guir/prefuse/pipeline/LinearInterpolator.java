package edu.berkeley.guir.prefuse.pipeline;

import java.awt.geom.Point2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.GraphItem;

/**
 * This class linearly interpolates a node position between two positions. This
 * is useful for performing animated transitions.
 * 
 * Apr 27, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class LinearInterpolator extends AbstractPipelineComponent 
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
				
		double sx, sy, ex, ey, x, y;
		double st, et, t;
		
		Iterator itemIter = m_registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			Point2D startLoc = item.getStartLocation();
			Point2D endLoc   = item.getEndLocation();
						
			sx = startLoc.getX();
			sy = startLoc.getY();
			ex = endLoc.getX();
			ey = endLoc.getY();
			
			x = sx + frac * (ex - sx);
			y = sy + frac * (ey - sy);
			
			item.setLocation(x,y);
			
			if ( item instanceof AggregateItem ) {
				AggregateItem aggr = (AggregateItem)item;
				st = aggr.getStartOrientation();
				et = aggr.getEndOrientation();
				t  = st + frac * (et - st);
			}
		}		
	} //

} // end of class LinearInterpolator
