package prefuse.util.ui;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.awt.*;
import javax.swing.*;

import prefuse.Display;
import prefuse.util.display.PaintListener;

/**
 * This object attaches itself to a Display and can scroll the Display as
 * required. It uses the Display's item bounds and current position to calculate
 * the model data for the JScrollBar.
 * 
 * <p>
 * The Display will pan as the scroll bar is being adjusted.
 * </p>
 * 
 * @author Anton Marsden
 */
public class DisplayScrollBar extends JScrollBar {

	protected final Display display;

	protected int panOffset = 0;

	protected int visiblePolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

	public DisplayScrollBar(Display d, int orientation) {
		this(d, orientation, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	/**
	 * @param d
	 *            the display to scroll
	 * @param orientation
	 *            the orientation of the scroll bar (as per JScrollBar)
	 * @param visiblePolicy
	 *            the visibility policy for the scroll bar. Legal values are
	 *            <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED</code>,
	 *            <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER</code>
	 *            and <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS</code>
	 * @see JScrollBar
	 * @see ScrollPaneConstants
	 */
	public DisplayScrollBar(Display d, int orientation, int visiblePolicy) {
		super(orientation);
		this.display = d;
		this.visiblePolicy = visiblePolicy;

		// initialise the model
		updateModel();

		this.display.addPaintListener(new PaintListener() {

			public void prePaint(Display d, Graphics2D g) {
			}

			public void postPaint(Display d, Graphics2D g) {
				updateModel();
			}
		});

		this.addAdjustmentListener(new AdjustmentListener() {

			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (e.getValueIsAdjusting() && display.isTranformInProgress()) {
					return;
				}
				int value = getValue();

				if (getOrientation() == HORIZONTAL) {
					display.pan(panOffset - value, 0d);
				} else if (getOrientation() == VERTICAL) {
					display.pan(0d, panOffset - value);
				} else {
					// unknown orientation
					return;
				}
				display.repaint();
				if (e.getValueIsAdjusting()) {
					panOffset = value;
				} else {
					panOffset = 0;
				}
			}
		});

	}

	/**
	 * @return the visibility policy for the scroll bar. Legal values are
	 *         <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED</code>,
	 *         <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER</code> and
	 *         <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS</code>
	 */
	public int getVisiblePolicy() {
		return visiblePolicy;
	}

	/**
	 * @param visiblePolicy
	 *            the visibility policy for the scroll bar. Legal values are
	 *            <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED</code>,
	 *            <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER</code>
	 *            and <code>ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS</code>
	 */
	public void setVisiblePolicy(int visiblePolicy) {
		this.visiblePolicy = visiblePolicy;
	}

	/**
	 * @return the Display to scroll
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * Updates the range model for the scroll bar based in the Display's current
	 * coordinates and items.
	 */
	protected void updateModel() {

		Rectangle2D rect = display.getItemBounds();

		AffineTransform transform = display.getTransform();

		Point2D itemTopLeft = new Point2D.Double();

		transform.transform(new Point2D.Double(rect.getMinX(), rect.getMinY()),
				itemTopLeft);

		Point2D itemBottomRight = new Point2D.Double();
		transform.transform(new Point2D.Double(rect.getMaxX(), rect.getMaxY()),
				itemBottomRight);

		int min = 0;
		int max = 0;
		int extent = 0;
		if (getOrientation() == HORIZONTAL) {

			min = Math.min((int)Math.floor(itemTopLeft.getX()), 0);
			extent = display.getWidth();
			max = Math.max((int)Math.ceil(itemBottomRight.getX()), extent);

		} else if (getOrientation() == VERTICAL) {
			min = Math.min((int)Math.floor(itemTopLeft.getY()), 0);
			extent = display.getHeight();
			max = Math.max((int)Math.ceil(itemBottomRight.getY()), extent);
		} else {
			// unknown orientation
			return;
		}

		if (extent <= 0) {
			extent = 1;
		}

		panOffset = 0;

		getModel().setRangeProperties(0, extent, (int) min, (int) max, false);

		setBlockIncrement(extent);
		
		// TODO: should we change the unit increment to something like extent/10 ?

		final boolean visible;

		switch (visiblePolicy) {
		case ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER:
			visible = false;
			break;
		case ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED:
			visible = min < 0 || extent < max;
			break;
		case ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS:
		default:
			visible = true;
			break;
		}

		setVisible(visible);

	}
}
