package edu.berkeley.guir.prefusex.layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.Layout;

/**
 * Performs a random layout of graph nodes within the layout's bounds and
 * (optionally) a specified margin;
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class RandomLayout extends Layout {

    private int m_margin = 0;
    
    public RandomLayout() {
    } //
    
    public RandomLayout(int margin) {
        m_margin = margin;
    } //
    
    public void run(ItemRegistry registry, double frac) {
        Rectangle2D b = getBounds(registry);
        double x, y;
        double w = b.getWidth() - 2*m_margin;
        double h = b.getHeight() - 2*m_margin;
        Iterator nodeIter = registry.getNodeItems();
        while ( nodeIter.hasNext() ) {
            GraphItem item = (GraphItem)nodeIter.next();
            x = m_margin + Math.random()*w;
            y = m_margin + Math.random()*h;
            item.updateLocation(x,y);
            item.setLocation(x,y);
        }
    } //

} // end of class RandomLayout
