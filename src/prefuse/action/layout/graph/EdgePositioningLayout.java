package prefuse.action.layout.graph;

import java.awt.geom.Point2D;

import prefuse.Alignment;
import prefuse.action.layout.Layout;
import prefuse.data.Graph;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Positions edges at the centroid of their nodes - should be run after a node
 * layout algorithm has positioned the nodes. Some renderers require prefuse.visual.EdgeItem.getX()
 * and prefuse.visual.EdgeItem.getY() to be set up correctly, and most layouts don't position the
 * edges.
 *
 * @author Anton Marsden
 * @see prefuse.render.LabelRenderer
 */
public class EdgePositioningLayout extends Layout {

	protected String m_edgeGroup;

    protected Alignment     m_xAlign1   = Alignment.CENTER;
    protected Alignment     m_yAlign1   = Alignment.CENTER;
    protected Alignment     m_xAlign2   = Alignment.CENTER;
    protected Alignment     m_yAlign2   = Alignment.CENTER;

	public EdgePositioningLayout(String graph) {
		super(graph);
		m_edgeGroup = PrefuseLib.getGroupName(graph, Graph.EDGES);
	}

    /**
     * Get the horizontal aligment of the edge mount point with the first node.
     * @return the horizontal alignment
     */
    public Alignment getHorizontalAlignment1() {
        return m_xAlign1;
    }

    /**
     * Get the vertical alignment of the edge mount point with the first node.
     * @return the vertical alignment
     */
    public Alignment getVerticalAlignment1() {
        return m_yAlign1;
    }

    /**
     * Get the horizontal alignment of the edge mount point with the second
     * node.
     * @return the horizontal alignment
     */
    public Alignment getHorizontalAlignment2() {
        return m_xAlign2;
    }

    /**
     * Get the vertical aligment of the edge mount point with the second node.
     * @return the vertical alignment
     */
    public Alignment getVerticalAlignment2() {
        return m_yAlign2;
    }

    /**
     * Set the horizontal alignment of the edge mount point with the first node.
     * @param align the horizontal alignment
     */
    public void setHorizontalAlignment1(Alignment align) {
        m_xAlign1 = align;
    }

    /**
     * Set the vertical alignment of the edge mount point with the first node.
     * @param align the vertical alignment
     */
    public void setVerticalAlignment1(Alignment align) {
        m_yAlign1 = align;
    }

    /**
     * Set the horizontal alignment of the edge mount point with the second
     * node.
     * @param align the horizontal alignment
     */
    public void setHorizontalAlignment2(Alignment align) {
        m_xAlign2 = align;
    }

    /**
     * Set the vertical alignment of the edge mount point with the second node.
     * @param align the vertical alignment
     */
    public void setVerticalAlignment2(Alignment align) {
        m_yAlign2 = align;
    }

	@Override
	public void run(double frac) {
    	Point2D.Float start = new Point2D.Float();
    	Point2D.Float end = new Point2D.Float();
        float[] coords = new float[4];
        for(VisualItem<?> item : m_vis.visibleItems(m_edgeGroup)) {
        	EdgeItem<?,?> edge = (EdgeItem<?,?>) item;
        	NodeItem<?,?> src = edge.getSourceNode();
        	NodeItem<?,?> tgt = edge.getTargetNode();

        	GraphicsLib.getAlignedPoint(start, src.getBounds(), m_xAlign1, m_yAlign1);
        	GraphicsLib.getAlignedPoint(end, tgt.getBounds(), m_xAlign2, m_yAlign2);
        	coords[0] = start.x;
        	coords[1] = start.y;
        	coords[2] = end.x;
        	coords[3] = end.y;
        	float[] centroid = GraphicsLib.centroid(coords, 4);
            if ( !edge.isFixed() ) {
            	setX(edge, null, centroid[0]);
            	setY(edge, null, centroid[1]);
            }
        }
	}

}
