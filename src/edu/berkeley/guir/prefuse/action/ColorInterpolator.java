package edu.berkeley.guir.prefuse.action;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * 
 * Mar 8, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
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
public class ColorInterpolator extends AbstractAction {
    
	private HashMap m_colorCache;
	
	public ColorInterpolator() {
		m_colorCache = new HashMap();		
	} //
	
	
	/**
	 * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
	 */
	public void run(ItemRegistry registry, double frac) {
		Iterator iter = registry.getItems();
		while ( iter.hasNext() ) {
			GraphItem item = (GraphItem)iter.next();
			
            Paint c1 = item.getStartColor(), c2 = item.getEndColor();
			item.setColor(getInterpolatedColor((Color)c1,(Color)c2,frac));
			
			Paint f1 = item.getStartFillColor(), f2 = item.getEndFillColor();
			item.setFillColor(getInterpolatedColor((Color)f1,(Color)f2,frac));
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
