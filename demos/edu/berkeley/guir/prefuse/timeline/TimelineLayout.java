/*
 * Created on Oct 27, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;

/**
 * @author Jack Li jackli(AT)cs_D0Tcmu_D0Tedu
 */
public abstract class TimelineLayout extends Layout implements TimelineConstants {
	protected final int m_timelineLength, m_numDivisions;
	private final int start;
	private final int end;
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param m_timelineLength
	 * @param m_numDivisions
	 */
    public TimelineLayout(final int start, final int end,
    		final int m_timelineLength, final int m_numDivisions) {
        this.start = start;
        this.end = end;
		this.m_timelineLength = m_timelineLength;
		this.m_numDivisions = m_numDivisions;
    }

    /**
     * @return Returns the end.
     */
    public int getEnd() {
        return end;
    }
    
    /**
     * @return Returns the start.
     */
    public int getStart() {
        return start;
    }	// this should be part of the timeline library
	
    protected String getNotchIndex(final String nodeID) {
		return nodeID.replaceFirst(NOTCH, "");
	}
    
    protected double getNodePosition(final int startYear, final int endYear, final double leftOffset) {
        final int yearsFromLeft = startYear - start;
        final double centerCorrection = ((double) endYear - startYear) / (end - start) * m_timelineLength / 2;
        final double fractionFromLeft = (double) yearsFromLeft / (end - start); // horn or not?
        return leftOffset + centerCorrection + (fractionFromLeft * m_timelineLength);
    }

    public void run(final ItemRegistry registry, final double frac) {
        final Iterator nodeItems = registry.getFilteredGraph().getNodes();//getNodeItems();
        final Dimension displaySize = registry.getDisplay(0).getSize();
        final double leftOffset = (displaySize.getWidth() - m_timelineLength) / 2;
        VisualItem node;
        while (nodeItems.hasNext()) {
            node = (VisualItem) nodeItems.next();
            if (node.getAttribute(NODE_TYPE).equals(NOTCH_TYPE)) {
                layoutNotchNode(displaySize, leftOffset, node);
            } else { // regular data node
                final Rectangle2D bounds = getLayoutBounds(registry);
                layoutDataNode(leftOffset, bounds, node);
            }
        }
    }

    protected abstract void layoutDataNode(final double leftOffset, final Rectangle2D bounds, VisualItem node);
    

	/**
	 * @param displaySize
	 * @param leftOffset
	 * @param node
	 */
	private void layoutNotchNode(final Dimension displaySize, final double leftOffset, VisualItem node) {
		final String notchIndexString = getNotchIndex(node
		        .getAttribute(XMLGraphReader.XMLGraphHandler.ID));
		final double y = displaySize.getHeight() / 3;
		final double x;
		if (notchIndexString.equals(START)) {
		    x = leftOffset;
		    //node.setFixed(true);
		} else if (notchIndexString.equals(END)) {
		    x = leftOffset + m_timelineLength;
		    //node.setFixed(true);
		} else { // a regular notch node
		    final int notchIndex = new Integer(notchIndexString)
		            .intValue();
		    x = leftOffset
		            + ((double) notchIndex / m_numDivisions * m_timelineLength);
		}
		setLocation(node, null, x, y);
	}
}
