package prefuse.action.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.StartVisiblePredicate;

/**
 * Layout Action that sets the positions for newly collapsed or newly
 * expanded nodes of a tree. This action updates positions such that
 * nodes flow out from their parents or collapse back into their parents
 * upon animated transitions.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class CollapsedSubtreeLayout extends Layout {

    private Orientation m_orientation;
    private final Point2D m_point = new Point2D.Double();

    /**
     * Create a new CollapsedSubtreeLayout. By default, nodes will collapse
     * to the center point of their parents.
     * @param group the data group to layout (only newly collapsed or newly
     * expanded items will be considered, as determined by their current
     * visibility settings).
     */
    public CollapsedSubtreeLayout(String group) {
        this(group, Orientation.CENTER);
    }

    /**
     * Create a new CollapsedSubtreeLayout.
     * @param group the data group to layout (only newly collapsed or newly
     * expanded items will be considered, as determined by their current
     * visibility settings).
     * @param orientation the layout orientation, determining which point
     * nodes will collapse/expand from.
     */
    public CollapsedSubtreeLayout(String group, Orientation orientation) {
        super(group);
        m_orientation = orientation;
    }

    // ------------------------------------------------------------------------

    /**
     * Get the layout orientation, determining which point nodes will collapse
     * or expand from.
     * @return the layout orientation
     */
    public Orientation getOrientation() {
        return m_orientation;
    }

    /**
     * Set the layout orientation, determining which point nodes will collapse
     * or expand from.
     */
    public void setOrientation(Orientation orientation) {
        m_orientation = orientation;
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.Action#run(double)
     */
    @Override
	public void run(double frac) {
        // handle newly expanded subtrees - ensure they emerge from
        // a visible ancestor node
    	for(VisualItem<?> item : m_vis.visibleItems(m_group)) {
            if ( item instanceof NodeItem && !item.isStartVisible() ) {
                NodeItem<?,?> n = (NodeItem<?,?>)item;
                Point2D p = getPoint(n, true);
                n.setStartX(p.getX());
                n.setStartY(p.getY());
            }
        }

        // handle newly collapsed nodes - ensure they collapse to
        // the greatest visible ancestor node
    	for(VisualItem<?> item : m_vis.items(m_group, StartVisiblePredicate.TRUE)) {
            if ( item instanceof NodeItem && !item.isEndVisible() ) {
                NodeItem<?,?> n = (NodeItem<?,?>)item;
                Point2D p = getPoint(n, false);
                n.setStartX(n.getEndX());
                n.setStartY(n.getEndY());
                n.setEndX(p.getX());
                n.setEndY(p.getY());
            }
        }

    }

    private Point2D getPoint(NodeItem<?,?> n, boolean start) {
        // find the visible ancestor
        NodeItem<?,?> p = n.getParent();
        if ( start ) {
			for (; p!=null && !p.isStartVisible(); p=p.getParent()) {
				;
			}
		} else {
			for (; p!=null && !p.isEndVisible(); p=p.getParent()) {
				;
			}
		}
        if ( p == null ) {
            m_point.setLocation(n.getX(), n.getY());
            return m_point;
        }

        // get the vanishing/appearing point
        double x = start ? p.getStartX() : p.getEndX();
        double y = start ? p.getStartY() : p.getEndY();
        Rectangle2D b = p.getBounds();
        switch ( m_orientation ) {
        case LEFT_RIGHT:
            m_point.setLocation(x+b.getWidth(), y);
            break;
        case RIGHT_LEFT:
            m_point.setLocation(x-b.getWidth(), y);
            break;
        case TOP_BOTTOM:
            m_point.setLocation(x, y+b.getHeight());
            break;
        case BOTTOM_TOP:
            m_point.setLocation(x, y-b.getHeight());
            break;
        case CENTER:
            m_point.setLocation(x, y);
            break;
        }
        return m_point;
    }

} // end of class CollapsedSubtreeLayout
