package prefuse.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import prefuse.Alignment;
import prefuse.render.RenderType;
import prefuse.visual.VisualItem;

/**
 * Library of useful computer graphics routines such as geometry routines for computing the
 * intersection of different shapes and rendering methods for computing bounds and performing
 * optimized drawing.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class GraphicsLib {

	public static enum IntersectionType {
		/** Indicates intersection between shapes */
		INTERSECTION,
		/** Indicates no intersection between shapes */
		NO_INTERSECTION,
		/** Indicates coincidence between shapes */
		COINCIDENT,
		/** Indicates two lines are parallel */
		PARALLEL
	}

	/**
	 * Compute the intersection of two line segments.
	 * 
	 * @param a
	 *            the first line segment
	 * @param b
	 *            the second line segment
	 * @param intersect
	 *            a Point in which to store the intersection point
	 * @return the intersection code. One of {@link IntersectionType#NO_INTERSECTION},
	 *         {@link IntersectionType#COINCIDENT}, or {@link IntersectionType#PARALLEL}.
	 */
	public static IntersectionType intersectLineLine(final Line2D a, final Line2D b, final Point2D intersect) {
		final double a1x = a.getX1(), a1y = a.getY1();
		final double a2x = a.getX2(), a2y = a.getY2();
		final double b1x = b.getX1(), b1y = b.getY1();
		final double b2x = b.getX2(), b2y = b.getY2();
		return intersectLineLine(a1x, a1y, a2x, a2y, b1x, b1y, b2x, b2y, intersect);
	}

	/**
	 * Compute the intersection of two line segments.
	 * 
	 * @param a1x
	 *            the x-coordinate of the first endpoint of the first line
	 * @param a1y
	 *            the y-coordinate of the first endpoint of the first line
	 * @param a2x
	 *            the x-coordinate of the second endpoint of the first line
	 * @param a2y
	 *            the y-coordinate of the second endpoint of the first line
	 * @param b1x
	 *            the x-coordinate of the first endpoint of the second line
	 * @param b1y
	 *            the y-coordinate of the first endpoint of the second line
	 * @param b2x
	 *            the x-coordinate of the second endpoint of the second line
	 * @param b2y
	 *            the y-coordinate of the second endpoint of the second line
	 * @param intersect
	 *            a Point in which to store the intersection point
	 * @return the intersection code. One of {@link IntersectionType#NO_INTERSECTION},
	 *         {@link IntersectionType#COINCIDENT}, or {@link IntersectionType#PARALLEL}.
	 */
	public static IntersectionType intersectLineLine(final double a1x, final double a1y, final double a2x, final double a2y,
			final double b1x, final double b1y, final double b2x, final double b2y, final Point2D intersect) {
		final double ua_t = (b2x - b1x) * (a1y - b1y) - (b2y - b1y) * (a1x - b1x);
		final double ub_t = (a2x - a1x) * (a1y - b1y) - (a2y - a1y) * (a1x - b1x);
		final double u_b = (b2y - b1y) * (a2x - a1x) - (b2x - b1x) * (a2y - a1y);

		if (u_b != 0) {
			final double ua = ua_t / u_b;
			final double ub = ub_t / u_b;

			if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1) {
				intersect.setLocation(a1x + ua * (a2x - a1x), a1y + ua * (a2y - a1y));
				return IntersectionType.INTERSECTION;
			} else {
				return IntersectionType.NO_INTERSECTION;
			}
		} else {
			return ua_t == 0 || ub_t == 0 ? IntersectionType.COINCIDENT : IntersectionType.PARALLEL;
		}
	}

	/**
	 * Compute the intersection of a line and a rectangle.
	 * 
	 * @param a1
	 *            the first endpoint of the line
	 * @param a2
	 *            the second endpoint of the line
	 * @param r
	 *            the rectangle
	 * @param pts
	 *            a length 2 or greater array of points in which to store the results
	 * @return the intersection code. One of {@link IntersectionType#NO_INTERSECTION},
	 *         {@link IntersectionType#COINCIDENT}, or {@link IntersectionType#PARALLEL}.
	 */
	public static int intersectLineRectangle(final Point2D a1, final Point2D a2, final Rectangle2D r, final Point2D[] pts) {
		final double a1x = a1.getX(), a1y = a1.getY();
		final double a2x = a2.getX(), a2y = a2.getY();
		final double mxx = r.getMaxX(), mxy = r.getMaxY();
		final double mnx = r.getMinX(), mny = r.getMinY();

		if (pts[0] == null) {
			pts[0] = new Point2D.Double();
		}
		if (pts[1] == null) {
			pts[1] = new Point2D.Double();
		}

		int i = 0;
		if (intersectLineLine(mnx, mny, mxx, mny, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		if (intersectLineLine(mxx, mny, mxx, mxy, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(mxx, mxy, mnx, mxy, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(mnx, mxy, mnx, mny, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		return i;
	}

	/**
	 * Compute the intersection of a line and a rectangle.
	 * 
	 * @param l
	 *            the line
	 * @param r
	 *            the rectangle
	 * @param pts
	 *            a length 2 or greater array of points in which to store the results
	 * @return the intersection code. One of {@link IntersectionType#NO_INTERSECTION},
	 *         {@link IntersectionType#COINCIDENT}, or {@link IntersectionType#PARALLEL}.
	 */
	public static int intersectLineRectangle(final Line2D l, final Rectangle2D r, final Point2D[] pts) {
		final double a1x = l.getX1(), a1y = l.getY1();
		final double a2x = l.getX2(), a2y = l.getY2();
		final double mxx = r.getMaxX(), mxy = r.getMaxY();
		final double mnx = r.getMinX(), mny = r.getMinY();

		if (pts[0] == null) {
			pts[0] = new Point2D.Double();
		}
		if (pts[1] == null) {
			pts[1] = new Point2D.Double();
		}

		int i = 0;
		if (intersectLineLine(mnx, mny, mxx, mny, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		if (intersectLineLine(mxx, mny, mxx, mxy, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(mxx, mxy, mnx, mxy, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(mnx, mxy, mnx, mny, a1x, a1y, a2x, a2y, pts[i]) == IntersectionType.INTERSECTION) {
			i++;
		}
		return i;
	}

	/**
	 * Computes the 2D convex hull of a set of points using Graham's scanning algorithm. The
	 * algorithm has been implemented as described in Cormen, Leiserson, and Rivest's Introduction
	 * to Algorithms.
	 * 
	 * The running time of this algorithm is O(n log n), where n is the number of input points.
	 * 
	 * @param pts
	 *            the input points in [x0,y0,x1,y1,...] order
	 * @param len
	 *            the length of the pts array to consider (2 * #points)
	 * @return the convex hull of the input points
	 */
	public static double[] convexHull(final double[] pts, final int len) {
		if (len < 6) {
			throw new IllegalArgumentException("Input must have at least 3 points");
		}
		final int plen = len / 2 - 1;
		final float[] angles = new float[plen];
		final int[] idx = new int[plen];
		final int[] stack = new int[len / 2];
		return convexHull(pts, len, angles, idx, stack);
	}

	/**
	 * Computes the 2D convex hull of a set of points using Graham's scanning algorithm. The
	 * algorithm has been implemented as described in Cormen, Leiserson, and Rivest's Introduction
	 * to Algorithms.
	 * 
	 * The running time of this algorithm is O(n log n), where n is the number of input points.
	 * 
	 * @param pts
	 * @return the convex hull of the input points
	 */
	public static double[] convexHull(final double[] pts, final int len, final float[] angles, final int[] idx, final int[] stack) {
		// check arguments
		final int plen = len / 2 - 1;
		if (len < 6) {
			throw new IllegalArgumentException("Input must have at least 3 points");
		}
		if (angles.length < plen || idx.length < plen || stack.length < len / 2) {
			throw new IllegalArgumentException("Pre-allocated data structure too small");
		}

		int i0 = 0;
		// find the starting ref point: leftmost point with the minimum y coord
		for (int i = 2; i < len; i += 2) {
			if (pts[i + 1] < pts[i0 + 1]) {
				i0 = i;
			} else if (pts[i + 1] == pts[i0 + 1]) {
				i0 = pts[i] < pts[i0] ? i : i0;
			}
		}

		// calculate polar angles from ref point and sort
		for (int i = 0, j = 0; i < len; i += 2) {
			if (i == i0) {
				continue;
			}
			angles[j] = (float) Math.atan2(pts[i + 1] - pts[i0 + 1], pts[i] - pts[i0]);
			idx[j++] = i;
		}
		ArrayLib.sort(angles, idx, plen);

		// toss out duplicated angles
		float angle = angles[0];
		int ti = 0, tj = idx[0];
		for (int i = 1; i < plen; i++) {
			final int j = idx[i];
			if (angle == angles[i]) {
				// keep whichever angle corresponds to the most distant
				// point from the reference point
				final double x1 = pts[tj] - pts[i0];
				final double y1 = pts[tj + 1] - pts[i0 + 1];
				final double x2 = pts[j] - pts[i0];
				final double y2 = pts[j + 1] - pts[i0 + 1];
				final double d1 = x1 * x1 + y1 * y1;
				final double d2 = x2 * x2 + y2 * y2;
				if (d1 >= d2) {
					idx[i] = -1;
				} else {
					idx[ti] = -1;
					angle = angles[i];
					ti = i;
					tj = j;
				}
			} else {
				angle = angles[i];
				ti = i;
				tj = j;
			}
		}

		// initialize our stack
		int sp = 0;
		stack[sp++] = i0;
		int j = 0;
		for (int k = 0; k < 2; j++) {
			if (idx[j] != -1) {
				stack[sp++] = idx[j];
				k++;
			}
		}

		// do graham's scan
		for (; j < plen; j++) {
			if (idx[j] == -1) {
				continue; // skip tossed out points
			}
			while (isNonLeft(i0, stack[sp - 2], stack[sp - 1], idx[j], pts)) {
				sp--;
			}
			stack[sp++] = idx[j];
		}

		// construct the hull
		final double[] hull = new double[2 * sp];
		for (int i = 0; i < sp; i++) {
			hull[2 * i] = pts[stack[i]];
			hull[2 * i + 1] = pts[stack[i] + 1];
		}

		return hull;
	}

	/**
	 * Convex hull helper method for detecting a non left turn about 3 points
	 */
	private static boolean isNonLeft(final int i0, final int i1, final int i2, final int i3, final double[] pts) {
		double l1, l2, l4, l5, l6, angle1, angle2, angle;

		l1 = Math.sqrt(Math.pow(pts[i2 + 1] - pts[i1 + 1], 2) + Math.pow(pts[i2] - pts[i1], 2));
		l2 = Math.sqrt(Math.pow(pts[i3 + 1] - pts[i2 + 1], 2) + Math.pow(pts[i3] - pts[i2], 2));
		l4 = Math.sqrt(Math.pow(pts[i3 + 1] - pts[i0 + 1], 2) + Math.pow(pts[i3] - pts[i0], 2));
		l5 = Math.sqrt(Math.pow(pts[i1 + 1] - pts[i0 + 1], 2) + Math.pow(pts[i1] - pts[i0], 2));
		l6 = Math.sqrt(Math.pow(pts[i2 + 1] - pts[i0 + 1], 2) + Math.pow(pts[i2] - pts[i0], 2));

		angle1 = Math.acos((l2 * l2 + l6 * l6 - l4 * l4) / (2 * l2 * l6));
		angle2 = Math.acos((l6 * l6 + l1 * l1 - l5 * l5) / (2 * l6 * l1));

		angle = Math.PI - angle1 - angle2;

		if (angle <= 0.0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Computes the mean, or centroid, of a set of points
	 * 
	 * @param pts
	 *            the points array, in x1, y1, x2, y2, ... arrangement.
	 * @param len
	 *            the length of the array to consider
	 * @return the centroid as a length-2 float array
	 */
	public static float[] centroid(final float pts[], final int len) {
		final float[] c = new float[] { 0, 0 };
		for (int i = 0; i < len; i += 2) {
			c[0] += pts[i];
			c[1] += pts[i + 1];
		}
		c[0] /= len / 2;
		c[1] /= len / 2;
		return c;
	}

	/**
	 * Expand a polygon by adding the given distance along the line from the centroid of the
	 * polygon.
	 * 
	 * @param pts
	 *            the polygon to expand, a set of points in a float array
	 * @param len
	 *            the length of the range of the array to consider
	 * @param amt
	 *            the amount by which to expand the polygon, each point will be moved this distance
	 *            along the line from the centroid of the polygon to the given point.
	 */
	public static void growPolygon(final float pts[], final int len, final float amt) {
		final float[] c = centroid(pts, len);
		for (int i = 0; i < len; i += 2) {
			final float vx = pts[i] - c[0];
			final float vy = pts[i + 1] - c[1];
			final float norm = (float) Math.sqrt(vx * vx + vy * vy);
			pts[i] += amt * vx / norm;
			pts[i + 1] += amt * vy / norm;
		}
	}

	/**
	 * Compute a cardinal spline, a series of cubic Bezier splines smoothly connecting a set of
	 * points. Cardinal splines maintain C(1) continuity, ensuring the connected spline segments
	 * form a differentiable curve, ensuring at least a minimum level of smoothness.
	 * 
	 * @param pts
	 *            the points to interpolate with a cardinal spline
	 * @param slack
	 *            a parameter controlling the "tightness" of the spline to the control points, 0.10
	 *            is a typically suitable value
	 * @param closed
	 *            true if the cardinal spline should be closed (i.e. return to the starting point),
	 *            false for an open curve
	 * @return the cardinal spline as a Java2D {@link java.awt.geom.GeneralPath} instance.
	 */
	public static GeneralPath cardinalSpline(final float pts[], final float slack, final boolean closed) {
		final GeneralPath path = new GeneralPath();
		path.moveTo(pts[0], pts[1]);
		return cardinalSpline(path, pts, slack, closed, 0f, 0f);
	}

	/**
	 * Compute a cardinal spline, a series of cubic Bezier splines smoothly connecting a set of
	 * points. Cardinal splines maintain C(1) continuity, ensuring the connected spline segments
	 * form a differentiable curve, ensuring at least a minimum level of smoothness.
	 * 
	 * @param pts
	 *            the points to interpolate with a cardinal spline
	 * @param start
	 *            the starting index from which to read points
	 * @param npoints
	 *            the number of points to consider
	 * @param slack
	 *            a parameter controlling the "tightness" of the spline to the control points, 0.10
	 *            is a typically suitable value
	 * @param closed
	 *            true if the cardinal spline should be closed (i.e. return to the starting point),
	 *            false for an open curve
	 * @return the cardinal spline as a Java2D {@link java.awt.geom.GeneralPath} instance.
	 */
	public static GeneralPath cardinalSpline(final float pts[], final int start, final int npoints, final float slack,
			final boolean closed) {
		final GeneralPath path = new GeneralPath();
		path.moveTo(pts[start], pts[start + 1]);
		return cardinalSpline(path, pts, start, npoints, slack, closed, 0f, 0f);
	}

	/**
	 * Compute a cardinal spline, a series of cubic Bezier splines smoothly connecting a set of
	 * points. Cardinal splines maintain C(1) continuity, ensuring the connected spline segments
	 * form a differentiable curve, ensuring at least a minimum level of smoothness.
	 * 
	 * @param p
	 *            the GeneralPath instance to use to store the result
	 * @param pts
	 *            the points to interpolate with a cardinal spline
	 * @param slack
	 *            a parameter controlling the "tightness" of the spline to the control points, 0.10
	 *            is a typically suitable value
	 * @param closed
	 *            true if the cardinal spline should be closed (i.e. return to the starting point),
	 *            false for an open curve
	 * @param tx
	 *            a value by which to translate the curve along the x-dimension
	 * @param ty
	 *            a value by which to translate the curve along the y-dimension
	 * @return the cardinal spline as a Java2D {@link java.awt.geom.GeneralPath} instance.
	 */
	public static GeneralPath cardinalSpline(final GeneralPath p, final float pts[], final float slack, final boolean closed,
			final float tx, final float ty) {
		int npoints = 0;
		for (; npoints < pts.length; ++npoints) {
			if (Float.isNaN(pts[npoints])) {
				break;
			}
		}
		return cardinalSpline(p, pts, 0, npoints / 2, slack, closed, tx, ty);
	}

	/**
	 * Compute a cardinal spline, a series of cubic Bezier splines smoothly connecting a set of
	 * points. Cardinal splines maintain C(1) continuity, ensuring the connected spline segments
	 * form a differentiable curve, ensuring at least a minimum level of smoothness.
	 * 
	 * @param p
	 *            the GeneralPath instance to use to store the result
	 * @param pts
	 *            the points to interpolate with a cardinal spline
	 * @param start
	 *            the starting index from which to read points
	 * @param npoints
	 *            the number of points to consider
	 * @param slack
	 *            a parameter controlling the "tightness" of the spline to the control points, 0.10
	 *            is a typically suitable value
	 * @param closed
	 *            true if the cardinal spline should be closed (i.e. return to the starting point),
	 *            false for an open curve
	 * @param tx
	 *            a value by which to translate the curve along the x-dimension
	 * @param ty
	 *            a value by which to translate the curve along the y-dimension
	 * @return the cardinal spline as a Java2D {@link java.awt.geom.GeneralPath} instance.
	 */
	public static GeneralPath cardinalSpline(final GeneralPath p, final float pts[], final int start, final int npoints,
			final float slack, final boolean closed, final float tx, final float ty) {
		// compute the size of the path
		final int len = 2 * npoints;
		final int end = start + len;

		if (len < 6) {
			throw new IllegalArgumentException("To create spline requires at least 3 points");
		}

		float dx1, dy1, dx2, dy2;

		// compute first control point
		if (closed) {
			dx2 = pts[start + 2] - pts[end - 2];
			dy2 = pts[start + 3] - pts[end - 1];
		} else {
			dx2 = pts[start + 4] - pts[start];
			dy2 = pts[start + 5] - pts[start + 1];
		}

		// repeatedly compute next control point and append curve
		int i;
		for (i = start + 2; i < end - 2; i += 2) {
			dx1 = dx2;
			dy1 = dy2;
			dx2 = pts[i + 2] - pts[i - 2];
			dy2 = pts[i + 3] - pts[i - 1];
			p.curveTo(tx + pts[i - 2] + slack * dx1, ty + pts[i - 1] + slack * dy1, tx + pts[i] - slack * dx2,
					ty + pts[i + 1] - slack * dy2, tx + pts[i], ty + pts[i + 1]);
		}

		// compute last control point
		if (closed) {
			dx1 = dx2;
			dy1 = dy2;
			dx2 = pts[start] - pts[i - 2];
			dy2 = pts[start + 1] - pts[i - 1];
			p.curveTo(tx + pts[i - 2] + slack * dx1, ty + pts[i - 1] + slack * dy1, tx + pts[i] - slack * dx2,
					ty + pts[i + 1] - slack * dy2, tx + pts[i], ty + pts[i + 1]);

			dx1 = dx2;
			dy1 = dy2;
			dx2 = pts[start + 2] - pts[end - 2];
			dy2 = pts[start + 3] - pts[end - 1];
			p.curveTo(tx + pts[end - 2] + slack * dx1, ty + pts[end - 1] + slack * dy1, tx + pts[0] - slack * dx2,
					ty + pts[1] - slack * dy2, tx + pts[0], ty + pts[1]);
			p.closePath();
		} else {
			p.curveTo(tx + pts[i - 2] + slack * dx2, ty + pts[i - 1] + slack * dy2, tx + pts[i] - slack * dx2,
					ty + pts[i + 1] - slack * dy2, tx + pts[i], ty + pts[i + 1]);
		}
		return p;
	}

	/**
	 * Compute a cardinal spline, a series of cubic Bezier splines smoothly connecting a set of
	 * points. Cardinal splines maintain C(1) continuity, ensuring the connected spline segments
	 * form a differentiable curve, ensuring at least a minimum level of smoothness.
	 * 
	 * @param p
	 *            the GeneralPath instance to use to store the result
	 * @param pts
	 *            the points to interpolate with a cardinal spline
	 * @param start
	 *            the starting index from which to read points
	 * @param npoints
	 *            the number of points to consider
	 * @param slack
	 *            a parameter controlling the "tightness" of the spline to the control points, 0.10
	 *            is a typically suitable value
	 * @param closed
	 *            true if the cardinal spline should be closed (i.e. return to the starting point),
	 *            false for an open curve
	 * @param tx
	 *            a value by which to translate the curve along the x-dimension
	 * @param ty
	 *            a value by which to translate the curve along the y-dimension
	 * @return the cardinal spline as a Java2D {@link java.awt.geom.GeneralPath} instance.
	 */
	public static GeneralPath cardinalSpline(final GeneralPath p, final float pts[], final boolean[] invisibleSegments,
			final float slack, final float tx, final float ty) {
		// compute the size of the path
		final int end = pts.length;

		if (end < 6) {
			throw new IllegalArgumentException("To create spline requires at least 3 points");
		}
		if (pts.length / 2 != invisibleSegments.length + 1) {
			throw new IllegalArgumentException("Incorrect number of invisible segment flags: ");
		}

		float dx1, dy1, dx2, dy2;

		// compute first control point
		dx2 = pts[4] - pts[0];
		dy2 = pts[5] - pts[1];

		// repeatedly compute next control point and append curve
		int i;
		for (i = 2; i < end - 2; i += 2) {
			dx1 = dx2;
			dy1 = dy2;
			dx2 = pts[i + 2] - pts[i - 2];
			dy2 = pts[i + 3] - pts[i - 1];
			if (!invisibleSegments[i / 2 - 1]) {
				p.curveTo(tx + pts[i - 2] + slack * dx1, ty + pts[i - 1] + slack * dy1, tx + pts[i] - slack * dx2,
						ty + pts[i + 1] - slack * dy2, tx + pts[i], ty + pts[i + 1]);
			}
		}

		// compute last control point
		if (!invisibleSegments[i / 2 - 1]) {
			p.curveTo(tx + pts[i - 2] + slack * dx2, ty + pts[i - 1] + slack * dy2, tx + pts[i] - slack * dx2,
					ty + pts[i + 1] - slack * dy2, tx + pts[i], ty + pts[i + 1]);
		}

		return p;
	}

	/**
	 * Computes a set of curves using the cardinal spline approach, but using straight lines for
	 * completely horizontal or vertical segments.
	 * 
	 * @param p
	 *            the GeneralPath instance to use to store the result
	 * @param pts
	 *            the points to interpolate with the spline
	 * @param epsilon
	 *            threshold value under which to treat the difference between two values to be zero.
	 *            Used to determine which segments to treat as lines rather than curves.
	 * @param slack
	 *            a parameter controlling the "tightness" of the spline to the control points, 0.10
	 *            is a typically suitable value
	 * @param closed
	 *            true if the spline should be closed (i.e. return to the starting point), false for
	 *            an open curve
	 * @param tx
	 *            a value by which to translate the curve along the x-dimension
	 * @param ty
	 *            a value by which to translate the curve along the y-dimension
	 * @return the stack spline as a Java2D {@link java.awt.geom.GeneralPath} instance.
	 */
	public static GeneralPath stackSpline(final GeneralPath p, final float[] pts, final float epsilon, final float slack,
			final boolean closed, final float tx, final float ty) {
		int npoints = 0;
		for (; npoints < pts.length; ++npoints) {
			if (Float.isNaN(pts[npoints])) {
				break;
			}
		}
		return stackSpline(p, pts, 0, npoints / 2, epsilon, slack, closed, tx, ty);
	}

	/**
	 * Computes a set of curves using the cardinal spline approach, but using straight lines for
	 * completely horizontal or vertical segments.
	 * 
	 * @param p
	 *            the GeneralPath instance to use to store the result
	 * @param pts
	 *            the points to interpolate with the spline
	 * @param start
	 *            the starting index from which to read points
	 * @param npoints
	 *            the number of points to consider
	 * @param epsilon
	 *            threshold value under which to treat the difference between two values to be zero.
	 *            Used to determine which segments to treat as lines rather than curves.
	 * @param slack
	 *            a parameter controlling the "tightness" of the spline to the control points, 0.10
	 *            is a typically suitable value
	 * @param closed
	 *            true if the spline should be closed (i.e. return to the starting point), false for
	 *            an open curve
	 * @param tx
	 *            a value by which to translate the curve along the x-dimension
	 * @param ty
	 *            a value by which to translate the curve along the y-dimension
	 * @return the stack spline as a Java2D {@link java.awt.geom.GeneralPath} instance.
	 */
	public static GeneralPath stackSpline(final GeneralPath p, final float pts[], final int start, final int npoints,
			final float epsilon, final float slack, final boolean closed, final float tx, final float ty) {
		// compute the size of the path
		final int len = 2 * npoints;
		final int end = start + len;

		if (len < 6) {
			throw new IllegalArgumentException("To create spline requires at least 3 points");
		}

		float dx1, dy1, dx2, dy2;
		// compute first control point
		if (closed) {
			dx2 = pts[start + 2] - pts[end - 2];
			dy2 = pts[start + 3] - pts[end - 1];
		} else {
			dx2 = pts[start + 4] - pts[start];
			dy2 = pts[start + 5] - pts[start + 1];
		}

		// repeatedly compute next control point and append curve
		int i;
		for (i = start + 2; i < end - 2; i += 2) {
			dx1 = dx2;
			dy1 = dy2;
			dx2 = pts[i + 2] - pts[i - 2];
			dy2 = pts[i + 3] - pts[i - 1];
			if (Math.abs(pts[i] - pts[i - 2]) < epsilon || Math.abs(pts[i + 1] - pts[i - 1]) < epsilon) {
				p.lineTo(tx + pts[i], ty + pts[i + 1]);
			} else {
				p.curveTo(tx + pts[i - 2] + slack * dx1, ty + pts[i - 1] + slack * dy1, tx + pts[i] - slack * dx2,
						ty + pts[i + 1] - slack * dy2, tx + pts[i], ty + pts[i + 1]);
			}
		}

		// compute last control point
		dx1 = dx2;
		dy1 = dy2;
		dx2 = pts[start] - pts[i - 2];
		dy2 = pts[start + 1] - pts[i - 1];
		if (Math.abs(pts[i] - pts[i - 2]) < epsilon || Math.abs(pts[i + 1] - pts[i - 1]) < epsilon) {
			p.lineTo(tx + pts[i], ty + pts[i + 1]);
		} else {
			p.curveTo(tx + pts[i - 2] + slack * dx1, ty + pts[i - 1] + slack * dy1, tx + pts[i] - slack * dx2,
					ty + pts[i + 1] - slack * dy2, tx + pts[i], ty + pts[i + 1]);
		}

		// close the curve if requested
		if (closed) {
			if (Math.abs(pts[end - 2] - pts[0]) < epsilon || Math.abs(pts[end - 1] - pts[1]) < epsilon) {
				p.lineTo(tx + pts[0], ty + pts[1]);
			} else {
				dx1 = dx2;
				dy1 = dy2;
				dx2 = pts[start + 2] - pts[end - 2];
				dy2 = pts[start + 3] - pts[end - 1];
				p.curveTo(tx + pts[end - 2] + slack * dx1, ty + pts[end - 1] + slack * dy1, tx + pts[0] - slack * dx2,
						ty + pts[1] - slack * dy2, tx + pts[0], ty + pts[1]);
			}
			p.closePath();
		}
		return p;
	}

	/**
	 * Expand a rectangle by the given amount.
	 * 
	 * @param r
	 *            the rectangle to expand
	 * @param amount
	 *            the amount by which to expand the rectangle
	 */
	public static void expand(final Rectangle2D r, final double amount) {
		r.setRect(r.getX() - amount, r.getY() - amount, r.getWidth() + 2 * amount, r.getHeight() + 2 * amount);
	}

	// ------------------------------------------------------------------------

	/**
	 * Sets a VisualItem's bounds based on its shape and stroke type. This method is optimized to
	 * avoid calling .getBounds2D where it can, thus avoiding object initialization and reducing
	 * object churn.
	 * 
	 * @param item
	 *            the VisualItem whose bounds are to be set
	 * @param shape
	 *            a Shape from which to determine the item bounds
	 * @param stroke
	 *            the stroke type that will be used for drawing the object, and may affect the final
	 *            bounds. A null value indicates the default (line width = 1) stroke is used.
	 */
	public static void setBounds(final VisualItem<?> item, final Shape shape, final BasicStroke stroke) {
		final Rectangle2D bounds = new Rectangle2D.Double();
		calculateBounds(shape, stroke, bounds);
		item.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * Calculates bounds based on a shape and stroke type. This method is optimized to avoid calling
	 * .getBounds2D where it can, thus avoiding object initialization and reducing object churn.
	 * 
	 * @param shape
	 *            a Shape from which to determine the bounds
	 * @param stroke
	 *            the stroke type that will be used for drawing the object, and may affect the final
	 *            bounds. A null value indicates the default (line width = 1) stroke is used.
	 * @param bounds
	 *            the rectangle to populate with the bounds
	 */
	public static void calculateBounds(final Shape shape, final BasicStroke stroke, final Rectangle2D bounds) {
		double x, y, w, h, lw, lw2;

		if (shape instanceof RectangularShape) {
			// this covers rectangle, rounded rectangle, ellipse, and arcs
			final RectangularShape r = (RectangularShape) shape;
			x = r.getX();
			y = r.getY();
			w = r.getWidth();
			h = r.getHeight();
		} else if (shape instanceof Line2D) {
			// this covers straight lines
			final Line2D l = (Line2D) shape;
			x = l.getX1();
			y = l.getY1();
			w = l.getX2();
			h = l.getY2();
			if (w < x) {
				lw = x;
				x = w;
				w = lw - x;
			} else {
				w = w - x;
			}
			if (h < y) {
				lw = y;
				y = h;
				h = lw - y;
			} else {
				h = h - y;
			}
		} else {
			// this covers any other arbitrary shapes, but
			// takes a small object allocation / garbage collection hit
			final Rectangle2D r = shape.getBounds2D();
			x = r.getMinX();
			y = r.getMinY();
			w = r.getWidth();
			h = r.getHeight();
		}

		// adjust boundary for stroke length as necessary
		if (stroke != null && (lw = stroke.getLineWidth()) > 1) {
			lw2 = lw / 2.0;
			x -= lw2;
			y -= lw2;
			w += lw;
			h += lw;
		}

		bounds.setRect(x, y, w, h);
	}

	/**
	 * Render a shape associated with a VisualItem into a graphics context. This method uses the
	 * {@link java.awt.Graphics} interface methods when it can, as opposed to the
	 * {@link java.awt.Graphics2D} methods such as {@link java.awt.Graphics2D#draw(java.awt.Shape)}
	 * and {@link java.awt.Graphics2D#fill(java.awt.Shape)}, resulting in a significant performance
	 * increase on the Windows platform, particularly for rectangle and line drawing calls.
	 * 
	 * @param g
	 *            the graphics context to render to
	 * @param item
	 *            the item being represented by the shape, this instance is used to get the correct
	 *            color values for the drawing
	 * @param shape
	 *            the shape to render
	 * @param stroke
	 *            the stroke type to use for drawing the object.
	 * @param type
	 *            the rendering type indicating if the shape should be drawn, filled, or both.
	 */
	public static void paint(final Graphics2D g, final VisualItem<?> item, final Shape shape, final BasicStroke stroke,
			final RenderType type) {
		paint(g, ColorLib.getColor(item.getStrokeColor()), ColorLib.getColor(item.getFillColor()), shape, stroke, type);
	}

	/**
	 * Render a shape associated with a VisualItem into a graphics context. This method uses the
	 * {@link java.awt.Graphics} interface methods when it can, as opposed to the
	 * {@link java.awt.Graphics2D} methods such as {@link java.awt.Graphics2D#draw(java.awt.Shape)}
	 * and {@link java.awt.Graphics2D#fill(java.awt.Shape)}, resulting in a significant performance
	 * increase on the Windows platform, particularly for rectangle and line drawing calls.
	 * 
	 * @param g
	 *            the graphics context to render to
	 * @param strokeColor
	 *            the stroke color to use for drawing the object.
	 * @param fillColor
	 *            the fill color to use for drawing the object.
	 * @param shape
	 *            the shape to render
	 * @param stroke
	 *            the stroke type to use for drawing the object.
	 * @param type
	 *            the rendering type indicating if the shape should be drawn, filled, or both.
	 */
	public static void paint(final Graphics2D g, final Color strokeColor, final Color fillColor, final Shape shape,
			final BasicStroke stroke, final RenderType type) {
		// if render type is NONE, then there is nothing to do
		if (type == RenderType.NONE) {
			return;
		}

		// set up colors
		final boolean sdraw = (type == RenderType.DRAW || type == RenderType.DRAW_AND_FILL) && strokeColor.getAlpha() != 0;
		final boolean fdraw = (type == RenderType.FILL || type == RenderType.DRAW_AND_FILL) && fillColor.getAlpha() != 0;
		if (!(sdraw || fdraw)) {
			return;
		}
		
		Stroke origStroke = null;
		if (sdraw) {
			origStroke = g.getStroke();
			g.setStroke(stroke);
		}

		int x, y, w, h, aw, ah;
		double xx, yy, ww, hh;

		// see if an optimized (non-shape) rendering call is available for us
		// these can speed things up significantly on the windows JRE
		// it is stupid we have to do this, but we do what we must
		// if we are zoomed in, we have no choice but to use
		// full precision rendering methods.
		final AffineTransform at = g.getTransform();
		final double scale = Math.max(at.getScaleX(), at.getScaleY());
		if (scale > 1.5) {
			if (fdraw) {
				g.setPaint(fillColor);
				g.fill(shape);
			}
			if (sdraw) {
				g.setPaint(strokeColor);
				g.draw(shape);
			}
		} else if (shape instanceof RectangularShape) {
			final RectangularShape r = (RectangularShape) shape;
			xx = r.getX();
			ww = r.getWidth();
			yy = r.getY();
			hh = r.getHeight();

			x = (int) xx;
			y = (int) yy;
			w = (int) (ww + xx - x);
			h = (int) (hh + yy - y);

			if (shape instanceof Rectangle2D) {
				if (fdraw) {
					g.setPaint(fillColor);
					g.fillRect(x, y, w, h);
				}
				if (sdraw) {
					g.setPaint(strokeColor);
					g.drawRect(x, y, w, h);
				}
			} else if (shape instanceof RoundRectangle2D) {
				final RoundRectangle2D rr = (RoundRectangle2D) shape;
				aw = (int) rr.getArcWidth();
				ah = (int) rr.getArcHeight();
				if (fdraw) {
					g.setPaint(fillColor);
					g.fillRoundRect(x, y, w, h, aw, ah);
				}
				if (sdraw) {
					g.setPaint(strokeColor);
					g.drawRoundRect(x, y, w, h, aw, ah);
				}
			} else if (shape instanceof Ellipse2D) {
				if (fdraw) {
					g.setPaint(fillColor);
					g.fillOval(x, y, w, h);
				}
				if (sdraw) {
					g.setPaint(strokeColor);
					g.drawOval(x, y, w, h);
				}
			} else {
				if (fdraw) {
					g.setPaint(fillColor);
					g.fill(shape);
				}
				if (sdraw) {
					g.setPaint(strokeColor);
					g.draw(shape);
				}
			}
		} else if (shape instanceof Line2D) {
			if (sdraw) {
				final Line2D l = (Line2D) shape;
				x = (int) (l.getX1() + 0.5);
				y = (int) (l.getY1() + 0.5);
				w = (int) (l.getX2() + 0.5);
				h = (int) (l.getY2() + 0.5);
				g.setPaint(strokeColor);
				g.drawLine(x, y, w, h);
			}
		} else {
			if (fdraw) {
				g.setPaint(fillColor);
				g.fill(shape);
			}
			if (sdraw) {
				// there is an issue deep within Java that shows itself if the 
				// scale values are incredibly small. 
				if (at.getScaleX() < 1e-5 || at.getScaleY()< 1e-5) {
					return;
				}
				g.setPaint(strokeColor);
				g.draw(shape);
			}
		}
		if (sdraw) {
			g.setStroke(origStroke);
		}
	}

	/**
	 * Helper method, which calculates the top-left co-ordinate of a rectangle given the rectangle's
	 * alignment.
	 */
	public static void getAlignedPoint(final Point2D p, final Rectangle2D r, final Alignment xAlign, final Alignment yAlign) {
		double x = r.getX(), y = r.getY();
		final double w = r.getWidth(), h = r.getHeight();
		if (xAlign == Alignment.CENTER) {
			x = x + w / 2;
		} else if (xAlign == Alignment.RIGHT) {
			x = x + w;
		}
		if (yAlign == Alignment.CENTER) {
			y = y + h / 2;
		} else if (yAlign == Alignment.BOTTOM) {
			y = y + h;
		}
		p.setLocation(x, y);
	}

} // end of class GraphicsLib
