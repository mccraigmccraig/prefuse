package edu.berkeley.guir.prefusex.layout;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;

/**
 * SpecifiedLayout
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class SpecifiedLayout extends Layout {

    private String xAttr = "x";
    private String yAttr = "y";
    private String fixedAttr = "fixed";
    
    /**
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
    public void run(ItemRegistry registry, double frac) {
        Iterator iter = registry.getNodeItems();
        while ( iter.hasNext() ) {
            NodeItem nitem = (NodeItem)iter.next();
            try {
				double x = Double.parseDouble(nitem.getAttribute(xAttr));
				double y = Double.parseDouble(nitem.getAttribute(yAttr));
				boolean fx = "true".equalsIgnoreCase(nitem.getAttribute(fixedAttr));
                nitem.updateLocation(x,y);
				nitem.setLocation(x,y);
				nitem.setFixed(fx);
			} catch ( Exception e ) {
			}
        }
    } //

} // end of class SpecifiedLayout
