package prefuse.action.layout.graph;

import java.awt.geom.Point2D;

import java.util.Map;
import java.util.Set;

import prefuse.Alignment;

import prefuse.action.layout.Layout;
import prefuse.data.Graph;

import prefuse.util.GraphicsLib;
import prefuse.util.MathLib;
import prefuse.util.ObjectPair;
import prefuse.util.PrefuseLib;
import prefuse.util.collections.MultiMap;
import prefuse.util.ui.PolarLine2D;

import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * This layout positions the edges in a graph to be half way between the nodes
 * (as per the EdgePositioningLayout). If there are multiple node-node edges
 * then the edge positions will be spread out along the equi-distant line
 * between the nodes.
 * 
 * @author Anton Marsden
 */
public class MultiEdgePositioningLayout extends Layout {
	protected String m_edgeGroup;

	protected Alignment m_xAlign1 = Alignment.CENTER;
	protected Alignment m_yAlign1 = Alignment.CENTER;
	protected Alignment m_xAlign2 = Alignment.CENTER;
	protected Alignment m_yAlign2 = Alignment.CENTER;

	protected double edgeSeparation = 50.0;

	private final MultiMap<ObjectPair<? extends NodeItem<?, ?>>, EdgeItem<?, ?>> pairToEdges = new MultiMap<ObjectPair<? extends NodeItem<?, ?>>, EdgeItem<?, ?>>();

	public MultiEdgePositioningLayout(String graph) {
		super(graph);
		m_edgeGroup = PrefuseLib.getGroupName(graph, Graph.EDGES);
	}

	/**
	 * Get the separation gap for edges linking the same two nodes.
	 * 
	 * @return the edge separation
	 */
	public double getEdgeSeparation() {
		return edgeSeparation;
	}

	/**
	 * Sets the separation gap between the edges linking the same two nodes.
	 * 
	 * @param edgeSeparation
	 *            the edge separation
	 */
	public void setEdgeSeparation(double edgeSeparation) {
		this.edgeSeparation = edgeSeparation;
	}

	/**
	 * Get the horizontal alignment of the edge mount point with the first node.
	 * 
	 * @return the horizontal alignment
	 */
	public Alignment getHorizontalAlignment1() {
		return m_xAlign1;
	}

	/**
	 * Get the vertical alignment of the edge mount point with the first node.
	 * 
	 * @return the vertical alignment
	 */
	public Alignment getVerticalAlignment1() {
		return m_yAlign1;
	}

	/**
	 * Get the horizontal alignment of the edge mount point with the second
	 * node.
	 * 
	 * @return the horizontal alignment
	 */
	public Alignment getHorizontalAlignment2() {
		return m_xAlign2;
	}

	/**
	 * Get the vertical aligment of the edge mount point with the second node.
	 * 
	 * @return the vertical alignment
	 */
	public Alignment getVerticalAlignment2() {
		return m_yAlign2;
	}

	/**
	 * Set the horizontal alignment of the edge mount point with the first node.
	 * 
	 * @param align
	 *            the horizontal alignment
	 */
	public void setHorizontalAlignment1(Alignment align) {
		m_xAlign1 = align;
	}

	/**
	 * Set the vertical alignment of the edge mount point with the first node.
	 * 
	 * @param align
	 *            the vertical alignment
	 */
	public void setVerticalAlignment1(Alignment align) {
		m_yAlign1 = align;
	}

	/**
	 * Set the horizontal alignment of the edge mount point with the second
	 * node.
	 * 
	 * @param align
	 *            the horizontal alignment
	 */
	public void setHorizontalAlignment2(Alignment align) {
		m_xAlign2 = align;
	}

	/**
	 * Set the vertical alignment of the edge mount point with the second node.
	 * 
	 * @param align
	 *            the vertical alignment
	 */
	public void setVerticalAlignment2(Alignment align) {
		m_yAlign2 = align;
	}

	public void run(double frac) {
		pairToEdges.clear();

		// locate all the edges
		for (VisualItem<?> item : m_vis.visibleItems(m_edgeGroup)) {
			EdgeItem<?, ?> edge = (EdgeItem<?, ?>) item;
			pairToEdges.put(new ObjectPair(edge.getSourceNode(), edge
					.getTargetNode()), edge);
		}

		final double halfEdgeSep = edgeSeparation / 2.0d;

		Point2D centroid = new Point2D.Double();

		for (Map.Entry<ObjectPair<? extends NodeItem<?, ?>>, Set<EdgeItem<?, ?>>> entry : pairToEdges
				.entrySet()) {
			ObjectPair nodePair = entry.getKey();
			final Set<EdgeItem<?, ?>> edges = entry.getValue();
			// handle the normal case (single edge) efficiently
			if (edges.size() == 1) {
				EdgeItem<?, ?> edge = edges.iterator().next();
				if (!edge.isFixed()) {
					getCentroid(centroid, nodePair);
					PrefuseLib.setX(edge, null, centroid.getX());
					PrefuseLib.setY(edge, null, centroid.getY());
				}
			} else {
				// TODO: better handle the case where edges go in both
				// directions
				final boolean odd = edges.size() % 2 == 1;
				final PolarLine2D origLine = getPolarLine(nodePair);
				getCentroid(centroid, nodePair);
				PolarLine2D perpLine = new PolarLine2D(centroid.getX(),
						centroid.getY(), 0.0, origLine.getTheta());
				int count = 0;
				for (EdgeItem<?, ?> edge : edges) {
					if (!edge.isFixed()) {

						if (odd) {
							perpLine.setRadius(edgeSeparation
									* ((count + 1) / 2));
							perpLine.setTheta(origLine.getTheta()
									+ (count % 2 == 0 ? MathLib.PI_DIV_2 : -MathLib.PI_DIV_2));
						} else {
							perpLine.setRadius(halfEdgeSep + edgeSeparation
									* (count / 2));
							perpLine.setTheta(origLine.getTheta()
									+ (count % 2 == 0 ? MathLib.PI_DIV_2 : -MathLib.PI_DIV_2));
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

	protected PolarLine2D getPolarLine(ObjectPair<? extends NodeItem<?, ?>> pair) {
		Point2D start = new Point2D.Double();
		Point2D end = new Point2D.Double();
		GraphicsLib.getAlignedPoint(start, pair.getA().getBounds(), m_xAlign1,
				m_yAlign1);
		GraphicsLib.getAlignedPoint(end, pair.getB().getBounds(), m_xAlign2,
				m_yAlign2);
		return new PolarLine2D(start, end);
	}

	protected void getCentroid(Point2D p,
			ObjectPair<? extends NodeItem<?, ?>> pair) {
		Point2D start = new Point2D.Double();
		Point2D end = new Point2D.Double();
		GraphicsLib.getAlignedPoint(start, pair.getA().getBounds(), m_xAlign1,
				m_yAlign1);
		GraphicsLib.getAlignedPoint(end, pair.getB().getBounds(), m_xAlign2,
				m_yAlign2);
		p.setLocation((end.getX() + start.getX()) / 2.0, (end.getY() + start
				.getY()) / 2.0);

	}

}
