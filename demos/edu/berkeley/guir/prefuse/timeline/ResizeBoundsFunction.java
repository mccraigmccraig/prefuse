/*
 * Created on Aug 5, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.action.Action;
import edu.berkeley.guir.prefusex.distortion.FisheyeDistortion;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class ResizeBoundsFunction extends AbstractAction implements Action,
		TimelineConstants {
	// (( FIELDS )) \\
	private final FisheyeDistortion feye;

	
	// (( CONSTRUCTORS )) \\
	/**
	 * 
	 */
	public ResizeBoundsFunction(final FisheyeDistortion feye) {
		this.feye = feye;
	}

	
	// (( METHODS )) \\
	/* (non-Javadoc)
	 * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
	 */
	public void run(ItemRegistry registry, double frac) {
        final Iterator iter = registry.getNodeItems();
        int i = 0;
        //for each item, resize/distort the endpoints
        while ( iter.hasNext() ) {
            //System.out.println(i++);
            final VisualItem item = (VisualItem)iter.next();
            final String leftString = item.getAttribute(LEFT_NORMAL);
            if (leftString != null) {
            	final double originalLeft = new Double(leftString).doubleValue();
            	final double originalRight = new Double(item.getAttribute(
            			RIGHT_NORMAL)).doubleValue();
            	item.setVizAttribute(LEFT_DISTORTED, 
            			new Double(feye.fisheyeMove(originalLeft, registry)));
            	item.setVizAttribute(RIGHT_DISTORTED,
            			new Double(feye.fisheyeMove(originalRight, registry)));
            }
            
            //if ( item.isFixed() ) continue;
            
            
            // reset distorted values
/*            item.getLocation().setLocation(item.getEndLocation());
            item.setSize(item.getEndSize());
            
            // compute distortion if we have a distortion focus
            if ( anchor != null ) {
                Rectangle2D bbox = item.getBounds();
                Point2D loc = item.getLocation();
                transformPoint(item.getEndLocation(), 
                        loc, anchor, bounds);
                if ( m_sizeDistorted ) {
                    double sz = transformSize(bbox, loc, anchor, bounds);
                    item.setSize(sz*item.getEndSize());
                }
            }*/
        }

	}

	public static void main(String[] args) {
	}
}
