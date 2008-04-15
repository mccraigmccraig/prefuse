package prefuse.render;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import prefuse.Alignment;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.MathLib;
import prefuse.util.StrokeLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 * <p>
 * Renderer that draws edges as lines connecting nodes. Both straight and curved
 * lines are supported. Curved lines are drawn using cubic Bezier curves.
 * Subclasses can override the
 * {@link #getCurveControlPoints(EdgeItem, Point2D[], double, double, double, double)}
 * method to provide custom control point assignment for such curves.
 * </p>
 *
 * <p>
 * This class also supports arrows for directed edges. See the
 * {@link #setArrowType(EdgeArrowType)} method for more.
 * </p>
 *
 * @version 1.0
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class EdgeRenderer extends AbstractShapeRenderer {

	public static enum EdgeType {
		LINE, CURVE, CURVE_VIA_EDGE;
	}

	public static enum EdgeArrowType {
		/** No arrows on edges */
		NONE,
		/** Arrows on edges pointing from source to target */
		FORWARD,
		/** Arrows on edges pointing from target to source */
		REVERSE

	}

	protected Line2D m_line = new Line2D.Float();
	protected CubicCurve2D m_cubic = new CubicCurve2D.Float();

	protected EdgeType m_edgeType = EdgeType.LINE;
	protected Alignment m_xAlign1 = Alignment.CENTER;
	protected Alignment m_yAlign1 = Alignment.CENTER;
	protected Alignment m_xAlign2 = Alignment.CENTER;
	protected Alignment m_yAlign2 = Alignment.CENTER;
	protected double m_width = 1;
	protected float m_curWidth = 1;
	protected Point2D m_tmpPoints[] = new Point2D[2];
	protected Point2D m_ctrlPoints[] = new Point2D[2];
	protected Point2D m_isctPoints[] = new Point2D[2];
	protected Point2D m_intersectSourcePoints[] = new Point2D[2];
	protected Point2D m_intersectTargetPoints[] = new Point2D[2];
	protected boolean hideNodeInternalSegments;

	// arrow head handling
	protected EdgeArrowType m_edgeArrow = EdgeArrowType.FORWARD;
	protected int m_arrowWidth = 8;
	protected int m_arrowHeight = 12;
	protected Polygon m_arrowHead = updateArrowHead(m_arrowWidth, m_arrowHeight);
	protected AffineTransform m_arrowTrans = new AffineTransform();
	protected Shape m_curArrow;

	protected float splineSlack = 0.1f;

	/**
	 * Create a new EdgeRenderer.
	 */
	public EdgeRenderer() {
		m_tmpPoints[0] = new Point2D.Float();
		m_tmpPoints[1] = new Point2D.Float();
		m_ctrlPoints[0] = new Point2D.Float();
		m_ctrlPoints[1] = new Point2D.Float();
		m_isctPoints[0] = new Point2D.Float();
		m_isctPoints[1] = new Point2D.Float();
	}

	/**
	 * Create a new EdgeRenderer with the given edge type.
	 *
	 * @param edgeType
	 *            the edge type
	 */
	public EdgeRenderer(EdgeType edgeType) {
		this(edgeType, EdgeArrowType.FORWARD);
	}

	/**
	 * Create a new EdgeRenderer with the given edge and arrow types.
	 *
	 * @param edgeType
	 *            the edge type
	 * @param arrowType
	 *            the arrow type
	 * @see #setArrowType(EdgeArrowType)
	 */
	public EdgeRenderer(EdgeType edgeType, EdgeArrowType arrowType) {
		this();
		setEdgeType(edgeType);
		setArrowType(arrowType);
	}

	/**
	 * @see prefuse.render.AbstractShapeRenderer#getRenderType(prefuse.visual.VisualItem)
	 */
	@Override
	public RenderType getRenderType(VisualItem<?> item) {
		return RenderType.DRAW;
	}

	/**
	 * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
	 * TODO: refactor this method - it is far too complicated now
	 */
	@Override
	protected Shape getRawShape(VisualItem<?> item) {
		EdgeItem<?, ?> edge = (EdgeItem<?, ?>) item;
		VisualItem<?> item1 = edge.getSourceNode();
		VisualItem<?> item2 = edge.getTargetNode();

		if (item1 != item2) {
			GraphicsLib.getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
					m_xAlign1, m_yAlign1);
			GraphicsLib.getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
					m_xAlign2, m_yAlign2);
		} else {
			// use the center of the item when the items are the same
			GraphicsLib.getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
					Alignment.CENTER, Alignment.CENTER);
			GraphicsLib.getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
					Alignment.CENTER, Alignment.CENTER);
		}

		m_curWidth = (float) (m_width * getLineWidth(item));

		// create the arrow head, if needed
		EdgeItem<?, ?> e = (EdgeItem<?, ?>) item;

		final Point2D edgePos = new Point2D.Double(e.getX(), e.getY());
		// Calculate edge to source node intersection points

		int srcIntersection = GraphicsLib.intersectLineRectangle(
				m_edgeType == EdgeType.CURVE_VIA_EDGE ? edgePos
						: m_tmpPoints[1], m_tmpPoints[0], item1.getBounds(),
				m_intersectSourcePoints);
		if (srcIntersection <= 0 && m_edgeType == EdgeType.CURVE_VIA_EDGE) {
			srcIntersection = GraphicsLib.intersectLineRectangle(edgePos,
					m_tmpPoints[1], item1.getBounds(), m_intersectSourcePoints);
		}
		int tgtIntersection = GraphicsLib.intersectLineRectangle(
				m_edgeType == EdgeType.CURVE_VIA_EDGE ? edgePos
						: m_tmpPoints[0], m_tmpPoints[1], item2.getBounds(),
				m_intersectTargetPoints);
		if (tgtIntersection <= 0 && m_edgeType == EdgeType.CURVE_VIA_EDGE) {
			tgtIntersection = GraphicsLib.intersectLineRectangle(edgePos,
					m_tmpPoints[0], item2.getBounds(), m_intersectTargetPoints);
		}

		final double n1x = m_tmpPoints[0].getX();
		final double n1y = m_tmpPoints[0].getY();
		final double n2x = m_tmpPoints[1].getX();
		final double n2y = m_tmpPoints[1].getY();

		boolean[] pointUpdated = new boolean[2];

		if (e.isDirected() && m_edgeArrow != EdgeArrowType.NONE) {
			// get starting and ending edge endpoints
			boolean forward = m_edgeArrow == EdgeArrowType.FORWARD;
			// the orientation of the arrow is different when the curve is drawn
			// via an edge
			Point2D start = m_edgeType == EdgeType.CURVE_VIA_EDGE ? edgePos
					: m_tmpPoints[forward ? 0 : 1];
			Point2D end = m_tmpPoints[forward ? 1 : 0];
			// compute the intersection with the target bounding box

			int i = forward ? tgtIntersection : srcIntersection;
			if (i > 0) {
				end = (forward ? m_intersectTargetPoints
						: m_intersectSourcePoints)[0];
			}

			// create the arrow head shape
			AffineTransform at = getArrowTrans(start, end, m_curWidth);
			m_curArrow = at.createTransformedShape(m_arrowHead);

			// update the endpoints for the edge shape
			// need to bias this by arrow head size
			Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
			lineEnd.setLocation(0, -m_arrowHeight);
			at.transform(lineEnd, lineEnd);
			pointUpdated[forward ? 1 : 0] = true;

		} else {
			m_curArrow = null;
		}

		// reposition the start/end points
		if(m_edgeType == EdgeType.CURVE_VIA_EDGE) {
			if (!pointUpdated[0] && srcIntersection > 0) {
				m_tmpPoints[0] = m_intersectSourcePoints[0];
				pointUpdated[0] = true;
			}
			if (!pointUpdated[1] && tgtIntersection > 0) {
				m_tmpPoints[1] = m_intersectTargetPoints[0];
				pointUpdated[1] = true;
			}
		}

		// draw self-referencing edges
		if (item1 == item2) {
			Ellipse2D m_ellipse = new Ellipse2D.Double();
			m_ellipse.setFrame(m_tmpPoints[0].getX(), m_tmpPoints[0].getY(),
					40, 30);
			return m_ellipse;
		}

		// create the edge shape
		Shape shape = null;

		switch (m_edgeType) {
		case LINE:
			m_line.setLine(m_tmpPoints[0].getX(), m_tmpPoints[0].getY(), m_tmpPoints[1].getX(), m_tmpPoints[1].getY());
			shape = m_line;
			break;
		case CURVE:
			getCurveControlPoints(edge, m_ctrlPoints, m_tmpPoints[0].getX(), m_tmpPoints[0].getY(), m_tmpPoints[1].getX(), m_tmpPoints[1].getY());
			m_cubic.setCurve(n1x, n1y, m_ctrlPoints[0].getX(), m_ctrlPoints[0]
					.getY(), m_ctrlPoints[1].getX(), m_ctrlPoints[1].getY(),
					m_tmpPoints[1].getX(), m_tmpPoints[1].getY());
			shape = m_cubic;
			break;
		case CURVE_VIA_EDGE:

			List<Boolean> invisibleSegments = new ArrayList<Boolean>();
			List<Float> splinePoints = new ArrayList<Float>();

			splinePoints.add((float) n1x);
			splinePoints.add((float) n1y);

			if (pointUpdated[0]) {
				invisibleSegments.add(hideNodeInternalSegments);
				splinePoints.add((float) m_tmpPoints[0].getX());
				splinePoints.add((float) m_tmpPoints[0].getY());
			}

			invisibleSegments.add(false);
			splinePoints.add((float) e.getX());
			splinePoints.add((float) e.getY());

			if (pointUpdated[1]) {
				invisibleSegments.add(false);
				splinePoints.add((float) m_tmpPoints[1].getX());
				splinePoints.add((float) m_tmpPoints[1].getY());
				invisibleSegments.add(hideNodeInternalSegments);
			} else {
				invisibleSegments.add(false);
			}

			splinePoints.add((float) n2x);
			splinePoints.add((float) n2y);

			float[] pts = new float[splinePoints.size()];

			int i = 0;
			for (float pt : splinePoints) {
				pts[i++] = pt;
			}
			i = 0;
			boolean[] is = new boolean[invisibleSegments.size()];
			for (boolean b : invisibleSegments) {
				is[i++] = b;
			}
			shape = createCurvedEdge(pts, is);
			break;
		default:
			throw new IllegalStateException("Unknown edge type");
		}

		// return the edge shape
		return shape;
	}

	protected Shape createCurvedEdge(float[] poly, boolean[] invisibleSegments) {
		GeneralPath m_path = new GeneralPath();
		// initialize the path
		m_path.reset();
		m_path.moveTo(poly[0], poly[1]);

		GraphicsLib.cardinalSpline(m_path, poly, invisibleSegments,
				splineSlack, 0.0f, 0.0f);

		return m_path;
	}

	/**
	 * @see prefuse.render.Renderer#render(java.awt.Graphics2D,
	 *      prefuse.visual.VisualItem)
	 */
	@Override
	public void render(Graphics2D g, VisualItem<?> item) {

		// render the edge line
		super.render(g, item);

		// render the edge arrow head, if appropriate
		if (m_curArrow != null) {

			g.setPaint(ColorLib.getColor(item.getStrokeColor()));
			g.fill(m_curArrow);
		}
	}

	/**
	 * Returns an affine transformation that maps the arrowhead shape to the
	 * position and orientation specified by the provided line segment end
	 * points.
	 */
	protected AffineTransform getArrowTrans(Point2D p1, Point2D p2, double width) {
		m_arrowTrans.setToTranslation(p2.getX(), p2.getY());
		m_arrowTrans.rotate(-MathLib.PI_DIV_2
				+ Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX()));
		if (width > 1) {
			double scalar = width / 4;
			m_arrowTrans.scale(scalar, scalar);
		}
		return m_arrowTrans;
	}

	/**
	 * Update the dimensions of the arrow head, creating a new arrow head if
	 * necessary. The return value is also set as the member variable
	 * <code>m_arrowHead</code>
	 *
	 * @param w
	 *            the width of the untransformed arrow head base, in pixels
	 * @param h
	 *            the height of the untransformed arrow head, in pixels
	 * @return the untransformed arrow head shape
	 */
	protected Polygon updateArrowHead(int w, int h) {
		if (m_arrowHead == null) {
			m_arrowHead = new Polygon();
		} else {
			m_arrowHead.reset();
		}
		m_arrowHead.addPoint(0, 0);
		m_arrowHead.addPoint(-w / 2, -h);
		m_arrowHead.addPoint(w / 2, -h);
		m_arrowHead.addPoint(0, 0);
		return m_arrowHead;
	}

	/**
	 * @see prefuse.render.AbstractShapeRenderer#getTransform(prefuse.visual.VisualItem)
	 */
	@Override
	protected AffineTransform getTransform(VisualItem<?> item) {
		return null;
	}

	/**
	 * @see prefuse.render.Renderer#locatePoint(java.awt.geom.Point2D,
	 *      prefuse.visual.VisualItem)
	 */
	@Override
	public boolean locatePoint(Point2D p, VisualItem<?> item) {
		Shape s = getShape(item);
		if (s == null) {
			return false;
		} else {
			double width = Math.max(2, getLineWidth(item));
			double halfWidth = width / 2.0;
			return s.intersects(p.getX() - halfWidth, p.getY() - halfWidth,
					width, width);
		}
	}

	/**
	 * @see prefuse.render.Renderer#calculateBounds(VisualItem,Rectangle2D)
	 */
	@Override
	public void calculateBounds(VisualItem<?> item, Rectangle2D bounds) {
		if (!m_manageBounds) {
			bounds.setRect(item.getX(), item.getY(), 0, 0);
			return;
		}
		Shape shape = getShape(item);
		if (shape == null) {
			bounds.setRect(item.getX(), item.getY(), 0, 0);
			return;
		}
		GraphicsLib.calculateBounds(shape, getStroke(item), bounds);
		if (m_curArrow != null) {
			Rectangle2D.union(bounds, m_curArrow.getBounds2D(), bounds);
		}
	}

	/**
	 * Returns the line width to be used for this VisualItem. By default,
	 * returns the base width value set using the
	 * {@link #setDefaultLineWidth(double)} method, scaled by the item size
	 * returned by {@link VisualItem#getSize()}. Subclasses can override this
	 * method to perform custom line width determination, however, the preferred
	 * method is to change the item size value itself.
	 *
	 * @param item
	 *            the VisualItem for which to determine the line width
	 * @return the desired line width, in pixels
	 */
	protected double getLineWidth(VisualItem<?> item) {
		return item.getSize();
	}

	/**
	 * Returns the stroke value returned by {@link VisualItem#getStroke()},
	 * scaled by the current line width determined by the
	 * {@link #getLineWidth(VisualItem)} method. Subclasses may override this
	 * method to perform custom stroke assignment, but should respect the line
	 * width paremeter stored in the {@link #m_curWidth} member variable, which
	 * caches the result of <code>getLineWidth</code>.
	 *
	 * @see prefuse.render.AbstractShapeRenderer#getStroke(prefuse.visual.VisualItem)
	 */
	@Override
	protected BasicStroke getStroke(VisualItem<?> item) {
		return StrokeLib.getDerivedStroke(item.getStroke(), m_curWidth);
	}

	/**
	 * Determines the control points to use for cubic (Bezier) curve edges.
	 * Override this method to provide custom curve specifications. To reduce
	 * object initialization, the entries of the Point2D array are already
	 * initialized, so use the <tt>Point2D.setLocation()</tt> method rather
	 * than <tt>new Point2D.Double()</tt> to more efficiently set custom
	 * control points.
	 *
	 * @param eitem
	 *            the EdgeItem we are determining the control points for
	 * @param cp
	 *            array of Point2D's (length >= 2) in which to return the
	 *            control points
	 * @param x1
	 *            the x co-ordinate of the first node this edge connects to
	 * @param y1
	 *            the y co-ordinate of the first node this edge connects to
	 * @param x2
	 *            the x co-ordinate of the second node this edge connects to
	 * @param y2
	 *            the y co-ordinate of the second node this edge connects to
	 */
	protected void getCurveControlPoints(EdgeItem<?, ?> eitem, Point2D[] cp,
			double x1, double y1, double x2, double y2) {
		double dx = x2 - x1, dy = y2 - y1;
		cp[0].setLocation(x1 + 2 * dx / 3, y1);
		cp[1].setLocation(x2 - dx / 8, y2 - dy / 8);
	}

	/**
	 * Returns the type of the drawn edge.
	 *
	 * @return the edge type
	 */
	public EdgeType getEdgeType() {
		return m_edgeType;
	}

	/**
	 * Sets the type of the drawn edge.
	 *
	 * @param type
	 *            the new edge type
	 */
	public void setEdgeType(EdgeType type) {
		m_edgeType = type;
	}

	/**
	 * Returns the type of the drawn edge.
	 */
	public EdgeArrowType getArrowType() {
		return m_edgeArrow;
	}

	/**
	 * Sets the type of the drawn edge.
	 *
	 * @param type
	 *            the new arrow type
	 */
	public void setArrowType(EdgeArrowType type) {
		m_edgeArrow = type;
	}

	/**
	 * Sets the dimensions of an arrow head for a directed edge. This specifies
	 * the pixel dimensions when both the zoom level and the size factor (a
	 * combination of item size value and default stroke width) are 1.0.
	 *
	 * @param width
	 *            the untransformed arrow head width, in pixels. This specifies
	 *            the span of the base of the arrow head.
	 * @param height
	 *            the untransformed arrow head height, in pixels. This specifies
	 *            the distance from the point of the arrow to its base.
	 */
	public void setArrowHeadSize(int width, int height) {
		m_arrowWidth = width;
		m_arrowHeight = height;
		m_arrowHead = updateArrowHead(width, height);
	}

	/**
	 * Get the height of the untransformed arrow head. This is the distance, in
	 * pixels, from the tip of the arrow to its base.
	 *
	 * @return the default arrow head height
	 */
	public int getArrowHeadHeight() {
		return m_arrowHeight;
	}

	/**
	 * Get the width of the untransformed arrow head. This is the length, in
	 * pixels, of the base of the arrow head.
	 *
	 * @return the default arrow head width
	 */
	public int getArrowHeadWidth() {
		return m_arrowWidth;
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
	 * Get the vertical alignment of the edge mount point with the second node.
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

	/**
	 * Sets the default width of lines. This width value will be scaled by the
	 * value of an item's size data field. The default base width is 1.
	 *
	 * @param w
	 *            the desired default line width, in pixels
	 */
	public void setDefaultLineWidth(double w) {
		m_width = w;
	}

	/**
	 * Gets the default width of lines. This width value that will be scaled by
	 * the value of an item's size data field. The default base width is 1.
	 *
	 * @return the default line width, in pixels
	 */
	public double getDefaultLineWidth() {
		return m_width;
	}

	public float getSplineSlack() {
		return splineSlack;
	}

	public void setSplineSlack(float splineSlack) {
		this.splineSlack = splineSlack;
	}

	public boolean isHideNodeInternalSegments() {
		return hideNodeInternalSegments;
	}

	public void setHideNodeInternalSegments(boolean hideNodeInternalSegments) {
		this.hideNodeInternalSegments = hideNodeInternalSegments;
	}

} // end of class EdgeRenderer
