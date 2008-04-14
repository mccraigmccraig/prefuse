package prefuse.render;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import prefuse.visual.VisualItem;

/**
 * @author Anton Marsden
 */
public class CompositeRenderer implements Renderer {

	private final Renderer interactiveRenderer;
	private final Renderer supportingRenderer;

	public CompositeRenderer(Renderer interactiveRenderer, Renderer supportingRenderer) {
		this.interactiveRenderer = interactiveRenderer;
		this.supportingRenderer = supportingRenderer;
	}

	public boolean locatePoint(Point2D p, VisualItem<?> item) {
		return interactiveRenderer.locatePoint(p, item);
	}

	public void render(Graphics2D g, VisualItem<?> item) {
		supportingRenderer.render(g, item);
		interactiveRenderer.render(g, item);
	}

	public void calculateBounds(VisualItem<?> item, Rectangle2D bounds) {
		// TODO: handle empty bounds - don't use createUnion???
		interactiveRenderer.calculateBounds(item, bounds);
		Rectangle2D supp = new Rectangle2D.Double();
		supportingRenderer.calculateBounds(item, supp);
		Rectangle2D.union(bounds, supp, bounds);
	}

	public Renderer getInteractiveRenderer() {
		return interactiveRenderer;
	}

	public Renderer getSupportingRenderer() {
		return supportingRenderer;
	}

	public boolean managesBounds() {
		return true;
	}


}
