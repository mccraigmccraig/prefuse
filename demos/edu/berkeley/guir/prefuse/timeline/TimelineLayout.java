/*
 * Created on Jul 12, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.Dimension;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;

/**
 * Layout the timeline itself; need length of timeline with respect to display's size 
 * (which was needed previously for rendering size of timeline node).
 * 
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineLayout extends Layout implements TimelineConstants {
	protected final int m_timelineLength, m_numDivisions;
	
	public TimelineLayout(final int m_timelineLength, final int m_numDivisions) {
		this.m_timelineLength = m_timelineLength;
		this.m_numDivisions = m_numDivisions;
	}
	
	// this should be part of the timeline library
	protected String getNotchIndex(final String nodeID) {
		return nodeID.replaceFirst(NOTCH, "");
	}
	
	public void run(final ItemRegistry registry, final double frac) {
		final Iterator nodeItems = registry.getNodeItems();
		final Dimension displaySize = registry.getDisplay(0).getSize();
		final double y = displaySize.getHeight() / 3;
		final double leftOffset = (displaySize.getWidth() - m_timelineLength) / 2; // probably everything should be doubles
		VisualItem node;
		while (nodeItems.hasNext()) {
			node = (VisualItem) nodeItems.next();
			if (node.getAttribute(NODE_TYPE).equals(NOTCH_TYPE)) {
				final String notchIndexString = getNotchIndex(node.getAttribute(XMLGraphReader.XMLGraphHandler.ID));
				final double x;
				if (notchIndexString.equals(START)) {
					x = leftOffset;
				} else if (notchIndexString.equals(END)) {
					x = leftOffset + m_timelineLength;
				} else { // a regular notch node
					final int notchIndex = new Integer(notchIndexString).intValue();
					x = leftOffset + ((double) notchIndex / m_numDivisions * m_timelineLength);
				}
				setLocation(node, null, x, y);
			}
		}
	}
}
