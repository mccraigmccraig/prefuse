package edu.berkeley.guir.prefuse.render;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Interface for rendering GraphItems. Supports drawing as well as location
 * and bounding box routines.
 * 
 * Created on May 2, 2003
 * 
 * @author newbergr
 * @author jheer
 */
public interface Renderer {
 
  /**
   * Render item into Graphics2D context.
   * @param g the Graphics2D context
   * @param item the item to draw
   */
  public void render(Graphics2D g, GraphItem item);

  /**
   * Returns true if the Point is located inside coordinates of the item.
   * @param p the point to test for containment
   * @param item the item to test containment against
   * @return true if the point is contained within the the item, else false
   */
  public boolean locatePoint(Point2D p, GraphItem item);
  
  /**
   * Returns a bounding rectangle for the item.
   * @param item the item to compute the bounding box for
   * @return the item's bounding box as a Rectangle
   */
  public Rectangle getBounds(GraphItem item);  
  
} // end of interface Renderer