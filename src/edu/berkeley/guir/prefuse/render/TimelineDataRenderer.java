/*
 * Created on Jul 8, 2004
 */
package edu.berkeley.guir.prefuse.render;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.demos.TimelineDemo;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.util.FontLib;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineDataRenderer extends TextItemRenderer {
	private static final float HEIGHT_OFFSET = -15;
	
	protected final Line2D m_line = new Line2D.Float();
	
	public TimelineDataRenderer() {
	}
	
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
			if (id.equals(TimelineDemo.NOTCH_START)) {
			} else if (id.equals(TimelineDemo.NOTCH_END)) {
			} else { // regular notch
			}
			return m_line;
		}
		else { // data element, for now just have it be a rectangle
			m_font = item.getFont();
	        
	        // make renderer size-aware
	        double size = item.getSize();
	        if ( size != 1 )
	            m_font = FontLib.getFont(m_font.getName(), m_font.getStyle(),
	                    (int)Math.round(size*m_font.getSize()));
	        
			FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);
			double h = fm.getHeight();
			//System.out.println("heigh is "+h);
			double w = 20;//set the width based on the start & end years, in size?
			getAlignedPoint(m_tmpPoint, item, w, h, m_xAlign, m_yAlign);
			m_textBox.setFrame(m_tmpPoint.getX(),m_tmpPoint.getY(),w,h);
			return m_textBox;
		}

	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#render(java.awt.Graphics2D, edu.berkeley.guir.prefuse.VisualItem)
	 */
	public void render(Graphics2D g, VisualItem item) {
        Shape shape = getShape(item);
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
                // center the year over the node
				g.drawString(s, (float)x-fm.stringWidth(s)/2, (float)y+fm.getAscent()+HEIGHT_OFFSET);
				if ( isHyperlink(item) ) {
                    int lx = (int)Math.round(x), ly = (int)Math.round(y);
					g.drawLine(lx,ly,lx+fm.stringWidth(s),ly+fm.getHeight()-1);
				}
			}
		}
	} //

	public static void main(String[] args) {
	}
}
