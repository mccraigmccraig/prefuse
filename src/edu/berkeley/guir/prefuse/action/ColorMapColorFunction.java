package edu.berkeley.guir.prefuse.action;

import java.awt.Paint;

import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.util.ColorMap;

/**
 * Color function that uses a color map to determine color values.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class ColorMapColorFunction extends ColorFunction {

    /**
     * The color map used by this color function.
     */
    protected ColorMap colorMap;
    
    /**
     * Default constructor. Using this constructor requires that
     * <code>setColorMap</code> be subsequently called before the
     * <code>run</code> method is ever called. Otherwise a null
     * pointer exception will result.
     */
    public ColorMapColorFunction() {
    } //
    
    /**
     * Create a new ColorFunction that uses the given color map.
     * @param cmap
     */
    public ColorMapColorFunction(ColorMap cmap) {
        colorMap = cmap;
    } //
    
    /**
     * Returns a value to serve as an index into the color map. Subclasses
     * should override this method to determine which variable(s) will
     * determine this item's color.
     * @param item the item to get the index value for
     * @return the index value
     */
    public abstract double getIndexValue(GraphItem item);
    
    /**
     * Returns the color for the given item. The color is determined by
     * getting an index value and then returning the corresponding color
     * from the color map. Subclasses can override this method to bypass
     * the color map.
     * @param item the item to get the color for
     * @return the item's color
     */
    public Paint getColor(GraphItem item) {
        return colorMap.getColor(getIndexValue(item));
    } //

    /**
     * Returns the fill color for the given item. The color is determined by
     * getting an index value and then returning the corresponding color
     * from the color map. Subclasses can override this method to bypass
     * the color map.
     * @param item the item to get the fill color for
     * @return the item's fill color
     */
    public Paint getFillColor(GraphItem item) {
        return colorMap.getColor(getIndexValue(item));
    } //
    
    /**
     * Returns the current color map.
     * @return Returns the color map.
     */
    public ColorMap getColorMap() {
        return colorMap;
    } //

    /**
     * Sets the color map to use.
     * @param colorMap Sets the color map.
     */
    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    } //

} // end of class ColorFunction
