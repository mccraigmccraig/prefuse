package prefuse.action.layout;

import java.util.Iterator;

import prefuse.data.Table;
import prefuse.render.PolygonRenderer;
import prefuse.visual.VisualItem;

/**
 * Layout Action that updates the outlines of polygons in a stacked line chart,
 * properly setting the coordinates of "collapsed" stacks.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class CollapsedStackLayout extends Layout {

    private String    m_polyField;
    
    /**
     * Create a new CollapsedStackLayout. The polygon field is assumed to be
     * {@link prefuse.render.PolygonRenderer#POLYGON}.
     * @param group the data group to layout
     */
    public CollapsedStackLayout(String group) {
        this(group, PolygonRenderer.POLYGON);
    }
    
    /**
     * Create a new CollapsedStackLayout.
     * @param group the data group to layout
     * @param field the data field from which to lookup the polygons
     */
    public CollapsedStackLayout(String group, String field) {
        super(group);
        m_polyField = field;
    }
    
    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
        VisualItem lastItem = null;
        float maxY = (float)getLayoutBounds().getMaxY();
        
        // TODO: generalize this -- we want tuplesReversed available for general sets
        Iterator iter = ((Table)m_vis.getGroup(m_group)).tuplesReversed();
        while ( iter.hasNext() ) {
            VisualItem item = (VisualItem)iter.next();
            boolean prev = item.isStartVisible();
            boolean cur = item.isVisible();
            
            if ( !prev && cur ) {
                // newly visible, update contour
                float[] f = (float[])item.get(m_polyField);
                if ( f == null ) continue;
                
                if ( lastItem == null ) {
                    // no previous items, smash y-values to the floor
                    for ( int i=1; i<f.length; i+=2 )
                        f[i] = maxY;
                } else {
                    // previous visible item, smash y-values to the
                    // visible item's contour
                    float[] l = (float[])lastItem.get(m_polyField);
                    for ( int i=1; i<f.length/2; i+=2 )
                        f[i] = f[f.length-i] = l[i];
                }
            } else if ( prev && cur ) {
                // this item was previously visible, remember it
                lastItem = item;
            }
        }
    }
    
} // end of class CollapsedStackAction
