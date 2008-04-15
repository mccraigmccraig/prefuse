package prefuse.controls;

import java.awt.event.MouseEvent;

import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * <p>
 * A ControlListener that sets the highlighted status (using the
 * {@link prefuse.visual.VisualItem#setHighlighted(boolean)
 * VisualItem.setHighlighted} method) for nodes neighboring the node currently
 * under the mouse pointer. The highlight flag might then be used by a color
 * function to change node appearance as desired.
 * </p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class NeighborHighlightControl extends ControlAdapter {

	private String activity = null;
	private boolean highlightWithInvisibleEdge = false;

	/**
	 * Creates a new highlight control.
	 */
	public NeighborHighlightControl() {
		this(null);
	}

	/**
	 * Creates a new highlight control that runs the given activity whenever the
	 * neighbor highlight changes.
	 *
	 * @param activity
	 *            the update Activity to run
	 */
	public NeighborHighlightControl(String activity) {
		this.activity = activity;
	}

	/**
	 * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem,
	 *      java.awt.event.MouseEvent)
	 */
	@Override
	public void itemEntered(VisualItem<?> item, MouseEvent e) {
		if (item instanceof NodeItem) {
			this.<NodeItem, EdgeItem> setNeighborHighlight(
					(NodeItem<?, ?>) item, true);
		}
	}

	/**
	 * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem,
	 *      java.awt.event.MouseEvent)
	 */
	@Override
	public void itemExited(VisualItem<?> item, MouseEvent e) {
		if (item instanceof NodeItem) {
			this.<NodeItem, EdgeItem> setNeighborHighlight(
					(NodeItem<?, ?>) item, false);
		}
	}

	/**
	 * Set the highlighted state of the neighbors of a node.
	 *
	 * @param n
	 *            the node under consideration
	 * @param state
	 *            the highlighting state to apply to neighbors
	 */
	protected <N extends NodeItem<N, E>, E extends EdgeItem<N, E>> void setNeighborHighlight(
			N n, boolean state) {
		for (EdgeItem<N, ?> eitem : n.edges()) {
			N nitem = eitem.getAdjacentNode(n);
			if (eitem.isVisible() || highlightWithInvisibleEdge) {
				eitem.setHighlighted(state);
				nitem.setHighlighted(state);
			}
		}
		if (activity != null) {
			n.getVisualization().run(activity);
		}
	}

	/**
	 * Indicates if neighbor nodes with edges currently not visible still get
	 * highlighted.
	 *
	 * @return true if neighbors with invisible edges still get highlighted,
	 *         false otherwise.
	 */
	public boolean isHighlightWithInvisibleEdge() {
		return highlightWithInvisibleEdge;
	}

	/**
	 * Determines if neighbor nodes with edges currently not visible still get
	 * highlighted.
	 *
	 * @param highlightWithInvisibleEdge
	 *            assign true if neighbors with invisible edges should still get
	 *            highlighted, false otherwise.
	 */
	public void setHighlightWithInvisibleEdge(boolean highlightWithInvisibleEdge) {
		this.highlightWithInvisibleEdge = highlightWithInvisibleEdge;
	}

	/**
	 *
	 * @return the activity that is run when the neighbor highlight changes
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * Sets the activity that is run when the neighbor highlight changes
	 * @param activity
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

} // end of class NeighborHighlightControl
