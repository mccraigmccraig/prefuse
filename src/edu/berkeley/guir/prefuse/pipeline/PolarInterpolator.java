package edu.berkeley.guir.prefuse.pipeline;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.Pipeline;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Interpolates between starting and ending display locations by linearly
 * interpolating between polar co-ordinates.
 * 
 * Apr 28, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class PolarInterpolator extends AbstractPipelineComponent 
	implements Interpolator, FocusListener
{

	public static final String ATTR_ANIM_FRAC = "animationFrac";
	public static final String ATTR_ANCHOR    = "anchor";

	private static final double TWO_PI = 2*Math.PI;
	
	protected Set m_linear = new HashSet();

	public void init(Pipeline pipeline) {
		super.init(pipeline);
		pipeline.getItemRegistry().addFocusListener(this);
	} //
	
	public void reset() {
		m_pipeline.getItemRegistry().removeFocusListener(this);
		m_linear.clear();
		super.reset();
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		double frac = getDoubleAttribute(ATTR_ANIM_FRAC);
		if ( frac == Double.NaN ) {
			throw new IllegalStateException("Animation fraction has not been set!");
		}

		Point2D anchor = (Point2D)getAttribute(ATTR_ANCHOR);
		if ( anchor == null ) {
			anchor = new Point2D.Float();
			setAttribute(ATTR_ANCHOR, anchor);
			//throw new IllegalStateException("Layout anchor has not been set!");
		}

		double ax, ay, sx, sy, ex, ey, x, y;
		double dt1, dt2, sr, st, er, et, r, t, stt, ett;

		ax = anchor.getX();
		ay = anchor.getY();
		
		Iterator itemIter = m_registry.getItems();
		while ( itemIter.hasNext() ) {
			GraphItem item = (GraphItem)itemIter.next();
			Entity entity = m_registry.getEntity(item);
			Point2D startLoc = item.getStartLocation();
			Point2D endLoc   = item.getEndLocation();

			sx = startLoc.getX() - ax;
			sy = startLoc.getY() - ay;
			ex = endLoc.getX() - ax;
			ey = endLoc.getY() - ay;

			// linearly interpolate to and from focus
			if ( m_linear.contains(entity) ) {
				x = startLoc.getX() + frac * (endLoc.getX()-startLoc.getX());
				y = startLoc.getY() + frac * (endLoc.getY()-startLoc.getY());
				item.setLocation(x,y);
			} else {	
				sr = Math.sqrt(sx*sx + sy*sy);
				st = Math.atan2(sy,sx);			
				er = Math.sqrt(ex*ex + ey*ey);
				et = Math.atan2(ey,ex);
				stt = translate(st);
				ett = translate(et);
				
				dt1 = et - st;
				dt2 = ett - stt;
				
				if ( Math.abs(dt1) < Math.abs(dt2) ) {
					t = st + frac * dt1;
				} else {
					t = stt + frac * dt2;
				}
				r = sr + frac * (er - sr);
							
				x = Math.round(ax + r*Math.cos(t));
				y = Math.round(ay + r*Math.sin(t));
	
				item.setLocation(x,y);
			}
			
			if ( item instanceof AggregateItem ) {
				AggregateItem aggr = (AggregateItem)item;
				st = aggr.getStartOrientation();
				et = aggr.getEndOrientation();
				stt = translate(st);
				ett = translate(et);
				
				dt1 = et - st;
				dt2 = ett - stt;
				
				if ( Math.abs(dt1) < Math.abs(dt2) ) {
					t = st + frac * dt1;
				} else {
					t = stt + frac * dt2;
				}
				aggr.setOrientation(t);
			}
		}
	} //

	private double translate(double t) {
		return ( t < 0 ? t+TWO_PI : t );
	}

	/**
	 * @see edu.berkeley.guir.prefuse.event.FocusListener#focusChanged(edu.berkeley.guir.prefuse.event.FocusEvent)
	 */
	public void focusChanged(FocusEvent e) {
		m_linear.clear();
		if ( e.getType() == FocusEvent.FOCUS_SET ) {
			TreeNode f = (TreeNode)e.getFocus();
			TreeNode p  = (TreeNode)e.getPreviousFocus();		
			for ( ; p != null; p = p.getParent() ) {		
				m_linear.add(p);
			}
		}	
	} //

} // end of class PolarInterpolator
