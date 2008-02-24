package prefuse;

import java.awt.Shape;

public interface ShapeBuilder {

	Shape createEmptyShape();
	
	Shape createShape(double x, double y, double width, double height);
	
	void updateShape(Shape shape, double x, double y, double width, double height);
	
}
