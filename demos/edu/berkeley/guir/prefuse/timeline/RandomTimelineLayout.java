/*
 * Created on Oct 27, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.geom.Rectangle2D;

import edu.berkeley.guir.prefuse.VisualItem;

/**
 * @author Jack Li jackli(AT)cs_D0Tcmu_D0Tedu
 */
public class RandomTimelineLayout extends TimelineLayout {

    /**
     * @param start
     * @param end
     * @param m_timelineLength
     * @param m_numDivisions
     */
    public RandomTimelineLayout(int start, int end, int m_timelineLength,
            int m_numDivisions) {
        super(start, end, m_timelineLength, m_numDivisions);
    }
    
	/**
	 * @param leftOffset
	 * @param bounds
	 * @param node
	 */
	protected void layoutDataNode(final double leftOffset, final Rectangle2D bounds, VisualItem node) {
		final String startYearString = node.getAttribute(START_YEAR);
		final String endYearString = node.getAttribute(END_YEAR);

		final int startYear, endYear;
		if (startYearString.equals(TIMELINE_START)) {
			startYear = getStart();
		} else {
			startYear = new Integer(startYearString).intValue(); // whoever gets children's hsopital
		}
		if (endYearString.equals(TIMELINE_END)) {
			endYear = getEnd();
		} else {
			endYear = new Integer(endYearString).intValue();
		}
		final double x = getNodePosition(startYear, endYear, leftOffset);
		final double yOffset;
		final String nodeType = node.getAttribute(NODE_TYPE);
		if (nodeType.equals(PERIOD_TYPE)) {
		    yOffset = 1.0 / 6;
		} else if (nodeType.equals(EVENT_TYPE)) {
		    yOffset = 2.0 / 5;
		} else if (nodeType.equals(PERSON_TYPE)) {
		    yOffset = 3.0 / 5;
		} else if (nodeType.equals(PIECE_TYPE)) {
		    yOffset = 4.0 / 5;
		} else {
		    yOffset = 1.0;
		}
		//final double y = bounds.getY() + Math.random()*bounds.getHeight();
		final double y = bounds.getY() + yOffset * bounds.getHeight()
				+ Math.random() / 8 * bounds.getHeight();
		setLocation(node, null, x, y);
	}
}
