package edu.berkeley.guir.prefuse.pipeline;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Linearly interpolates between starting and ending colors for GraphItems.
 * Custom color interpolators can be written by subclassing this class and
 * overriding the getInterpolatedColor() method.
 * 
 * Apr 30, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class ColorInterpolator extends AbstractPipelineComponent
	implements Interpolator
{
	public static final String ATTR_ANIM_FRAC = "animationFrac";
	private HashMap m_colorCache;
	
	public ColorInterpolator() {
		m_colorCache = new HashMap();		
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		double frac = getDoubleAttribute(ATTR_ANIM_FRAC);
		if ( frac == Double.NaN ) {
			throw new IllegalStateException("Animation fraction has not been set!");
		}
		
		Iterator iter = m_registry.getItems();
		while ( iter.hasNext() ) {
			GraphItem item = (GraphItem)iter.next();
			
			Color c1 = item.getStartColor(), c2 = item.getEndColor();
			item.setColor(getInterpolatedColor(c1,c2,frac));
			
			Color f1 = item.getStartFillColor(), f2 = item.getEndFillColor();
			item.setFillColor(getInterpolatedColor(f1,f2,frac));
		}
		m_colorCache.clear();
	} //
	
	protected Color getInterpolatedColor(Color c1, Color c2, double frac) {
		String key = c1.toString() + c2.toString(); 	
		Color ic;
		if ( c1.equals(c2) ) {
			ic = c1;
		} else {			
			ic = (Color)m_colorCache.get(key);
			if ( ic == null ) {				
				ic = getIntermediateColor(c1,c2,frac);
				m_colorCache.put(key, ic);
			}
		}
		return ic;
	} //
	
	protected Color getIntermediateColor(Color c1, Color c2, double frac) {
		return new Color((int)Math.round(frac*c2.getRed()   + (1-frac)*c1.getRed()),
					     (int)Math.round(frac*c2.getGreen() + (1-frac)*c1.getGreen()),
					     (int)Math.round(frac*c2.getBlue()  + (1-frac)*c1.getBlue()),
					     (int)Math.round(frac*c2.getAlpha() + (1-frac)*c1.getAlpha()));
	} //

} // end of class ColorInterpolator
