package edu.berkeley.guir.prefuse.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Renders edges with text labels.
 * 
 * May 28, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class TextEdgeRenderer extends DefaultEdgeRenderer {

	protected Graphics2D m_g;
	protected String m_labelName = "label";
	protected int m_horizBorder = 3;
	protected int m_vertBorder = 0;
	
	protected Font m_font = new Font("SansSerif", Font.PLAIN, 10);
	
	protected Rectangle2D m_textBox = new Rectangle2D.Double();

	/**
	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRenderType()
	 */
	public int getRenderType() {
		return RENDER_TYPE_FILL;
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.GraphItem)
	 */
	protected Shape getRawShape(GraphItem item) {
		Line2D line = (Line2D)super.getRawShape(item);
		Area a1 = new Area(line);
		
		String s = (String)item.getAttribute(m_labelName);
		FontMetrics fm = m_g.getFontMetrics(m_font);
		int h = fm.getHeight() + 2*m_vertBorder;
		int w = fm.stringWidth(s) + 2*m_horizBorder;
		
		double x = ((line.getX1()+line.getX2()) / 2.0) - (w/2);
		double y = ((line.getY1()+line.getY2()) / 2.0) - (h/2);
		
		m_textBox.setRect(x,y,w,h);
		Area a2 = new Area(m_textBox);
		a1.add(a2);
		return a1;
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#render(java.awt.Graphics2D, edu.berkeley.guir.prefuse.GraphItem)
	 */
	public void render(Graphics2D g, GraphItem item) {
		m_g = g;
		
		Shape rawShape = getRawShape(item);
		Shape shape;
		if ( m_edgeType == EDGE_TYPE_CURVE ) {
			shape = m_cubic;
		} else if ( m_width > 1 ) {
			shape = m_fline;
		} else {
			shape = m_line;
		}
		if (shape != null) {
			g.setColor(item.getColor());			
			g.draw(shape);
		}
		
		shape = m_textBox;		
		Color fillColor = Color.WHITE;
		Color itemColor = item.getColor();
		if (shape != null) {
			switch (getRenderType()) {
				case RENDER_TYPE_DRAW :
					g.setColor(itemColor);
					g.draw(shape);
					break;
				case RENDER_TYPE_FILL :
					g.setColor(fillColor);
					g.fill(shape);
					break;
				case RENDER_TYPE_DRAW_AND_FILL :
					g.setColor(fillColor);
					g.fill(shape);
					g.setColor(itemColor);
					g.draw(shape);
					break;
			}
			g.setColor(itemColor);
			String s = (String)item.getAttribute(m_labelName);
			Rectangle r = shape.getBounds();
			g.setColor(item.getColor());
			g.setFont(m_font);
			g.drawString(s, r.x+m_horizBorder, r.y+m_vertBorder+5*r.height/6);
		}
	} //

} // end of class TextEdgeRenderer
