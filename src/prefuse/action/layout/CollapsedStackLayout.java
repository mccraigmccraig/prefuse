package prefuse.action.layout;

import java.awt.geom.Rectangle2D;
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

    private final String    m_polyField;
    private Orientation      m_orientation = Orientation.BOTTOM_TOP;
    private final boolean   m_horiz = false;
    private final boolean   m_top = false;

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
     * Returns the orientation of this layout. One of
     * {@link Orientation#BOTTOM_TOP} (to grow bottom-up),
     * {@link Orientation#TOP_BOTTOM} (to grow top-down),
     * {@link Orientation#LEFT_RIGHT} (to grow left-right), or
     * {@link Orientation#RIGHT_LEFT} (to grow right-left).
     * @return the orientation of this layout
     */
    public Orientation getOrientation() {
        return m_orientation;
    }

    /**
     * Sets the orientation of this layout. Must be one of
     * {@link Orientation#BOTTOM_TOP} (to grow bottom-up),
     * {@link Orientation#TOP_BOTTOM} (to grow top-down),
     * {@link Orientation#LEFT_RIGHT} (to grow left-right), or
     * {@link Orientation#RIGHT_LEFT} (to grow right-left).
     * @param orient the desired orientation of this layout
     * @throws IllegalArgumentException if the orientation value
     * is not a valid value
     */
    public void setOrientation(Orientation orient) {
    	if(orient == Orientation.CENTER) {
    		throw new IllegalArgumentException(
                "Invalid orientation value: "+orient);
    	}
        m_orientation = orient;
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    @Override
	public void run(double frac) {
        VisualItem<?> lastItem = null;

        Rectangle2D bounds = getLayoutBounds();
        float floor = (float)
            (m_horiz ? (m_top?bounds.getMaxX():bounds.getMinX())
                     : m_top?bounds.getMinY():bounds.getMaxY());
        int bias = m_horiz ? 0 : 1;

        // TODO: generalize this -- we want tuplesReversed available for general sets
        for(VisualItem<?> item : ((Table<? extends VisualItem<?>>)m_vis.getGroup(m_group)).tuplesReversed()) {
            boolean prev = item.isStartVisible();
            boolean cur = item.isVisible();

            if ( !prev && cur ) {
                // newly visible, update contour
                float[] f = (float[])item.get(m_polyField);
                if ( f == null ) {
					continue;
				}

                if ( lastItem == null ) {
                    // no previous items, smash values to the floor
                    for ( int i=0; i<f.length; i+=2 ) {
						f[i+bias] = floor;
					}
                } else {
                    // previous visible item, smash values to the
                    // visible item's contour
                    float[] l = (float[])lastItem.get(m_polyField);
                    for ( int i=0; i<f.length/2; i+=2 ) {
						f[i+bias] = f[f.length-2-i+bias]
                                  = l[i+bias];
					}
                }
            } else if ( prev && cur ) {
                // this item was previously visible, remember it
                lastItem = item;
            }
        }
    }

} // end of class CollapsedStackAction
