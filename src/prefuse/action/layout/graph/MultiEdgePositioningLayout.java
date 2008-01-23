package prefuse.action.layout.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.Map;
import java.util.Set;

import prefuse.Alignment;

import prefuse.action.layout.Layout;
import prefuse.data.Graph;

import prefuse.util.PrefuseLib;
import prefuse.util.collections.MultiMap;
import prefuse.util.display.PolarLine2D;

import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * This layout positions the edges in a graph to be half way between 
 * 
 * @author Anton Marsden
 */
public class MultiEdgePositioningLayout extends Layout
{
	protected String m_edgeGroup;

    protected Alignment     m_xAlign1   = Alignment.CENTER;
    protected Alignment     m_yAlign1   = Alignment.CENTER;
    protected Alignment     m_xAlign2   = Alignment.CENTER;
    protected Alignment     m_yAlign2   = Alignment.CENTER;

    protected double edgeSeparation = 50.0;
    
    private final MultiMap<NodePair, EdgeItem<?,?>> pairToEdges = new MultiMap<NodePair, EdgeItem<?,?>>();

    private static final double PI_DIV_2 = Math.PI / 2.0d;

	public MultiEdgePositioningLayout(String graph) {
		super(graph);
		m_edgeGroup = PrefuseLib.getGroupName(graph, Graph.EDGES);
	}

	public double getEdgeSeparation() {
		return edgeSeparation;
	}

	public void setEdgeSeparation(double edgeSeparation) {
		this.edgeSeparation = edgeSeparation;
	}

	/**
     * Get the horizontal alignment of the edge mount point with the first node.
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

    public void run( double frac )
    {
    	pairToEdges.clear();
    	
        // locate all the edges
        for ( VisualItem<?> item : m_vis.visibleItems( m_edgeGroup ) )
        {
            EdgeItem<?, ?> edge = (EdgeItem<?, ?>) item;
			pairToEdges.put(new NodePair(edge.getSourceItem(), edge
					.getTargetItem()), edge);
        }

        final double halfEdgeSep = edgeSeparation / 2.0d;

        for (Map.Entry<NodePair, Set<EdgeItem<?, ?>>> entry : pairToEdges
				.entrySet()) {
			NodePair nodePair = entry.getKey();
			final Set<EdgeItem<?, ?>> edges = entry.getValue();
			// handle the normal case (single edge) efficiently
			if (edges.size() == 1) {
				EdgeItem<?, ?> edge = edges.iterator().next();
				if (!edge.isFixed()) {
					Point2D centroid = getCentroid(nodePair);
					PrefuseLib.setX(edge, null, centroid.getX());
					PrefuseLib.setY(edge, null, centroid.getY());
				}
			} else {
				// TODO: better handle the case where edges go in both directions
				final boolean odd = edges.size() % 2 == 1;
				final PolarLine2D origLine = getPolarLine(nodePair);
				Point2D centroid = getCentroid(nodePair);
				PolarLine2D perpLine = new PolarLine2D(centroid.getX(),
						centroid.getY(), 0.0, origLine.getTheta());
				int count = 0;
				for (EdgeItem<?, ?> edge : edges) {
					if (!edge.isFixed()) {

						if (odd) {
							perpLine.setRadius(edgeSeparation
									* ((count + 1) / 2));
							perpLine.setTheta(origLine.getTheta()
									+ (count % 2 == 0 ? PI_DIV_2 : -PI_DIV_2));
						} else {
							perpLine.setRadius(halfEdgeSep + edgeSeparation
									* (count / 2));
							perpLine.setTheta(origLine.getTheta()
									+ (count % 2 == 0 ? PI_DIV_2 : -PI_DIV_2));
						}

						PrefuseLib.setX(edge, null, perpLine.getX2());
						PrefuseLib.setY(edge, null, perpLine.getY2());
					}
					// increase the count regardless of whether we position the
					// edge
					count++;
				}
			}
		}

    }

    protected PolarLine2D getPolarLine( NodePair pair )
    {
        Point2D start = getAlignedPoint( pair.a.getBounds(), m_xAlign1, m_yAlign1);
        Point2D end   = getAlignedPoint( pair.b.getBounds(), m_xAlign2, m_yAlign2);

        return new PolarLine2D( start, end );
    }

    protected Point2D getCentroid( NodePair pair )
    {
        Point2D start = getAlignedPoint( pair.a.getBounds(), m_xAlign1, m_yAlign1);
        Point2D end   = getAlignedPoint( pair.b.getBounds(), m_xAlign2, m_yAlign2);

        return new Point2D.Double( ( end.getX() + start.getX() ) / 2.0, ( end.getY() + start.getY() ) / 2.0 );
    }

    protected static Point2D getAlignedPoint( Rectangle2D r, Alignment xAlign, Alignment yAlign )
    {
        double x = r.getX();
        double y = r.getY();
        double w = r.getWidth();
        double h = r.getHeight();
        if ( xAlign == Alignment.CENTER )
        {
            x = x + ( w / 2 );
        }
        else if ( xAlign == Alignment.RIGHT )
        {
            x = x + w;
        }
        if ( yAlign == Alignment.CENTER )
        {
            y = y + ( h / 2 );
        }
        else if ( yAlign == Alignment.BOTTOM )
        {
            y = y + h;
        }

        return new Point2D.Double( x, y );
    }

    private static class NodePair
    {
        private final NodeItem<?,?> a;
        private final NodeItem<?,?> b;

        public NodePair( NodeItem<?,?> a, NodeItem<?,?> b )
        {
            this.a = a;
            this.b = b;
        }

        public boolean equals( Object other )
        {
            if ( this == other )
            {
                return true;
            }
            if ( !( other instanceof NodePair ) )
            {
                return false;
            }
            NodePair o = (NodePair) other;

            return a == o.a && b == o.b || a == o.b && b == o.a;
        }

        public int hashCode()
        {
            return a.hashCode() ^ b.hashCode();
        }
    }
}
