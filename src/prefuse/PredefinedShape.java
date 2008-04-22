package prefuse;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * Predefined ShapeBuilders
 *
 * @author Anton Marsden
 */
public enum PredefinedShape implements ShapeBuilder {
	/** No shape. Draw nothing. */
	NONE {
		public Shape createEmptyShape() {
			return null;
		}

		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
		}
	},
	/** Rectangle/Square shape */
	RECTANGLE {
		public Shape createEmptyShape() {
			return new Rectangle2D.Double();
		}

		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			Rectangle2D.Double s = (Rectangle2D.Double) shape;
			s.setFrame(x, y, width, height);
		}
	},
	/** Ellipse/Circle shape */
	ELLIPSE {
		public Shape createEmptyShape() {
			return new Ellipse2D.Double();
		}

		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			Ellipse2D.Double e = (Ellipse2D.Double) shape;
			e.setFrame(x, y, width, height);
		}
	},
	/** Diamond shape */
	DIAMOND {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			GeneralPath path = (GeneralPath) shape;
			path.reset();
			path.moveTo((float) x, (float) (y + 0.5f * height));
			path.lineTo((float) (x + 0.5f * height), (float) y);
			path.lineTo((float) (x + height), (float) (y + 0.5f * height));
			path.lineTo((float) (x + 0.5f * height), (float) (y + height));
			path.closePath();
		}
	},
	/** Cross shape */
	CROSS {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			GeneralPath path = (GeneralPath) shape;
			double h14 = 3 * height / 8, h34 = 5 * height / 8;
			path.reset();
			path.moveTo((float) (x + h14), (float) y);
			path.lineTo((float) (x + h34), (float) y);
			path.lineTo((float) (x + h34), (float) (y + h14));
			path.lineTo((float) (x + height), (float) (y + h14));
			path.lineTo((float) (x + height), (float) (y + h34));
			path.lineTo((float) (x + h34), (float) (y + h34));
			path.lineTo((float) (x + h34), (float) (y + height));
			path.lineTo((float) (x + h14), (float) (y + height));
			path.lineTo((float) (x + h14), (float) (y + h34));
			path.lineTo((float) x, (float) (y + h34));
			path.lineTo((float) x, (float) (y + h14));
			path.lineTo((float) (x + h14), (float) (y + h14));
			path.closePath();
		}
	},
	/** Star shape */
	STAR {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			double s = height / (2 * Math.sin(Math.toRadians(54)));
			double shortSide = height / (2 * Math.tan(Math.toRadians(54)));
			double mediumSide = s * Math.sin(Math.toRadians(18));
			double longSide = s * Math.cos(Math.toRadians(18));
			double innerLongSide = s / (2 * Math.cos(Math.toRadians(36)));
			double innerShortSide = innerLongSide
					* Math.sin(Math.toRadians(36));
			double innerMediumSide = innerLongSide
					* Math.cos(Math.toRadians(36));
			GeneralPath path = (GeneralPath) shape;
			path.reset();
			path.moveTo((float) x, (float) (y + shortSide));
			path.lineTo((float) (x + innerLongSide), (float) (y + shortSide));
			path.lineTo((float) (x + height / 2), (float) y);
			path.lineTo((float) (x + height - innerLongSide),
					(float) (y + shortSide));
			path.lineTo((float) (x + height), (float) (y + shortSide));
			path.lineTo((float) (x + height - innerMediumSide), (float) (y
					+ shortSide + innerShortSide));
			path.lineTo((float) (x + height - mediumSide),
							(float) (y + height));
			path.lineTo((float) (x + height / 2), (float) (y + shortSide
					+ longSide - innerShortSide));
			path.lineTo((float) (x + mediumSide), (float) (y + height));
			path.lineTo((float) (x + innerMediumSide),
					(float) (y + shortSide + innerShortSide));
			path.closePath();
		}
	},
	/** Up-pointing triangle shape */
	TRIANGLE_UP {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			GeneralPath path = (GeneralPath) shape;
			path.reset();
			path.moveTo((float) x, (float) (y + height));
			path.lineTo((float) (x + height / 2), (float) y);
			path.lineTo((float) (x + height), (float) (y + height));
			path.closePath();
		}
	},
	/** Down-pointing triangle shape */
	TRIANGLE_DOWN {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			GeneralPath path = (GeneralPath) shape;
			path.reset();
			path.moveTo((float) x, (float) y);
			path.lineTo((float) (x + height), (float) y);
			path.lineTo((float) (x + height / 2), (float) (y + height));
			path.closePath();
		}
	},
	/** Left-pointing triangle shape */
	TRIANGLE_LEFT {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			GeneralPath path = new GeneralPath();
			path.reset();
			path.moveTo((float) (x + height), (float) y);
			path.lineTo((float) (x + height), (float) (y + height));
			path.lineTo((float) x, (float) (y + height / 2));
			path.closePath();
		}
	},
	/** Right-pointing triangle shape */
	TRIANGLE_RIGHT {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			GeneralPath path = (GeneralPath) shape;
			path.reset();
			path.moveTo((float) x, (float) (y + height));
			path.lineTo((float) (x + height), (float) (y + height / 2));
			path.lineTo((float) x, (float) y);
			path.closePath();
		}
	},
	/** Hexagon shape */
	HEXAGON {
		public void updateShape(Shape shape, double x, double y, double width,
				double height) {
			width = height / 2; // !!
			GeneralPath path = (GeneralPath) shape;
			path.reset();
			path.moveTo((float) x, (float) (y + 0.5f * height));
			path.lineTo((float) (x + 0.5f * width), (float) y);
			path.lineTo((float) (x + 1.5f * width), (float) y);
			path.lineTo((float) (x + 2.0f * width),
							(float) (y + 0.5f * height));
			path.lineTo((float) (x + 1.5f * width), (float) (y + height));
			path.lineTo((float) (x + 0.5f * width), (float) (y + height));
			path.closePath();
		}
	};

	public Shape createEmptyShape() {
		return new GeneralPath();
	}

	public Shape createShape(double x, double y, double width, double height) {
		Shape shape = createEmptyShape();
		updateShape(shape, x, y, width, height);
		return shape;
	}

}
