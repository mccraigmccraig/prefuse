/*
 * Created on Jul 8, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineDataRenderer extends TextItemRenderer implements TimelineConstants {
	// (( CONSTANTS )) \\
	private static final float HEIGHT_OFFSET = -15;
	private static final int NODE_HEIGHT = 14;
	
	
	// (( FIELDS )) \\
	protected final Line2D m_line = new Line2D.Float();
	private final int timelineStart, timelineEnd, timelineLength;
	private double nodeLength;
	
	
	// (( CONSTRUCTOR )) \\
	public TimelineDataRenderer(final int timelineStart, final int timelineEnd, final int timelineLength) {
		this.timelineStart = timelineStart;
		this.timelineEnd = timelineEnd;
		this.timelineLength = timelineLength;
	}
	
	
	// (( METHODS )) \\
	/**
	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.VisualItem)
	 */
	protected Shape getRawShape(VisualItem item) {
		final String nodeType = item.getAttribute(TimelineDemo.NODE_TYPE);
		if (nodeType.equals(TimelineDemo.NOTCH_TYPE)) {
			final String id = item.getAttribute(XMLGraphReader.XMLGraphHandler.ID);

			//getAlignedPoint(m_tmpPoint, item, w, h, m_xAlign, m_yAlign);
			final double itemXCoord = item.getX(), itemYCoord = item.getY();
			m_line.setLine(itemXCoord, itemYCoord+10, itemXCoord, itemYCoord-10);
/*			if (id.equals(TimelineDemo.NOTCH_START)) {
			} else if (id.equals(TimelineDemo.NOTCH_END)) {
			} else { // regular notch
			}*/
			return m_line;
		}
		else { // data element, for now just have it be a rectangle
			//get width
			//one, get start year and end year attributes
			final String startYearString = item.getAttribute(START_YEAR);
			final String endYearString = item.getAttribute(END_YEAR);
			final int startYear, endYear;
			if (startYearString.equals(TIMELINE_START)) {
				startYear = timelineStart;
			} else {
				startYear = new Integer(startYearString).intValue();
			}
			if (endYearString.equals(TIMELINE_END)) {
				endYear = timelineEnd;
			} else {
				endYear = new Integer(endYearString).intValue();
			}
			
			//set the nodeLength based on the start & end years
			final int nodeSpan = endYear - startYear;
			final int timelineSpan = timelineEnd - timelineStart;
			nodeLength = timelineLength * nodeSpan / timelineSpan;
			
			getAlignedPoint(m_tmpPoint, item, nodeLength, NODE_HEIGHT, m_xAlign, m_yAlign);
			m_textBox.setFrame(m_tmpPoint.getX(),m_tmpPoint.getY(),nodeLength,NODE_HEIGHT);
			return m_textBox;
		}
	} //

	
	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#render(java.awt.Graphics2D, edu.berkeley.guir.prefuse.VisualItem)
	 */
	public void render(Graphics2D g, VisualItem item) {
        Shape shape = getShape(item); // this call ensures timeline
        if ( shape != null ) {
            super.drawShape(g, item, shape);
        
            // now render the text
			String s = getText(item);
			if ( s != null ) {			
				Rectangle2D r = shape.getBounds2D();
				g.setPaint(item.getColor());
				g.setFont(m_font);
				FontMetrics fm = g.getFontMetrics();
                double size = item.getSize();
                double x = r.getX() + size*m_horizBorder;
                double y = r.getY() + size*m_vertBorder;
                x += nodeLength / 2;
                y += HEIGHT_OFFSET;
                // center the year over the node
				g.drawString(s, (float)x-fm.stringWidth(s)/2, (float)y+fm.getAscent());
				if ( isHyperlink(item) ) {
                    int lx = (int)Math.round(x), ly = (int)Math.round(y);
					g.drawLine(lx,ly,lx+fm.stringWidth(s),ly+fm.getHeight()-1);
				}
			}
		}
	}
} // end of class TimelineDataRenderer
