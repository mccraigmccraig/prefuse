package prefuse.util;

import java.awt.BasicStroke;

import prefuse.util.collections.IntObjectHashMap;

/**
 * Library maintaining a cache of drawing strokes and other useful stroke
 * computation routines.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class StrokeLib {

    private static final IntObjectHashMap strokeMap = new IntObjectHashMap();
    private static int misses = 0;
    private static int lookups = 0;
    
    /**
     * Get a cap square, join mitred, non-dashed stroke of the given width.
     * @param width the requested stroke width
     * @return the stroke
     */
    public static BasicStroke getStroke(int width) {
        return getStroke(width,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER);
    }
    
    /**
     * Get a non-dashed stroke of the given width, cap, and join
     * @param width the requested stroke width
     * @param cap the requested cap type, one of
     * {@link java.awt.BasicStroke#JOIN_BEVEL},
     * {@link java.awt.BasicStroke#JOIN_MITER}, or
     * {@link java.awt.BasicStroke#JOIN_ROUND}
     * @param join the requested join type, one of
     * {@link java.awt.BasicStroke#CAP_BUTT},
     * {@link java.awt.BasicStroke#CAP_ROUND}, or
     * {@link java.awt.BasicStroke#CAP_SQUARE}
     * @return the stroke
     */
    public static BasicStroke getStroke(int width, int cap, int join) {
        int key = (width<<4)+(cap<<2)+join;
        BasicStroke s = null;
        if ( (s=(BasicStroke)strokeMap.get(key)) == null ) {
            s = new BasicStroke(width, cap, join);
            strokeMap.put(key, s);
            ++misses;
        }
        ++lookups;
        return s;
    }
    
    /**
     * Get the number of cache misses to the Stroke object cache.
     * @return the number of cache misses
     */
    public static int getCacheMissCount() {
        return misses;
    }
    
    /**
     * Get the number of cache lookups to the Stroke object cache.
     * @return the number of cache lookups
     */
    public static int getCacheLookupCount() {
        return lookups;
    }
    
    /**
     * Clear the Stroke object cache.
     */
    public static void clearCache() {
        strokeMap.clear();
    }    
    
} // end of class StrokeLib
