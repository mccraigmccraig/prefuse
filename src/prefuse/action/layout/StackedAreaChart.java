package prefuse.action.layout;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;

import prefuse.data.Table;
import prefuse.data.query.NumberRangeModel;
import prefuse.util.ArrayLib;
import prefuse.util.MathLib;
import prefuse.util.PrefuseLib;
import prefuse.util.ui.ValuedRangeModel;
import prefuse.visual.VisualItem;

/**
 * Layout Action that computes a stacked area chart, in which a series of
 * data values are consecutively stacked on top of each other.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class StackedAreaChart extends Layout {

    private String m_field;
    private String m_start;
    private String m_end;
    
    private String[] columns;
    private double[] baseline;
    private float[] poly;
    private double m_padding = 0.05;
    private float m_threshold;
    private Rectangle2D bounds;
    
    private NumberRangeModel m_model;
    
    /**
     * Create a new StackedAreaChart.
     * @param group the data group to layout
     * @param field the data field in which to store computed polygons
     * @param columns the various data fields, in sorted order, that
     * should be referenced for each consecutive point of a stack layer
     */
    public StackedAreaChart(String group, String field, String[] columns) {
        this(group, field, columns, 1.0);
    }
    
    /**
     * Create a new StackedAreaChart.
     * @param group the data group to layout
     * @param field the data field in which to store computed polygons
     * @param columns the various data fields, in sorted order, that
     * should be referenced for each consecutive point of a stack layer
     * @param threshold height threshold under which stacks should not
     * be made visible.
     */
    public StackedAreaChart(String group, String field, String[] columns,
                            double threshold)
    {
        super(group);
        this.columns = columns;
        baseline = new double[columns.length];
        poly = new float[4*columns.length];
        
        m_field = field;
        m_start = PrefuseLib.getStartField(field);
        m_end = PrefuseLib.getEndField(field);
        setThreshold(threshold);
        
        m_model = new NumberRangeModel(0,1,0,1);
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Gets the percentage of the layout bounds that should be reserved for
     * empty space at the top of the stack.
     * @return the padding percentage
     */
    public double getPaddingPercentage() {
        return m_padding;
    }
    
    /**
     * Sets the percentage of the layout bounds that should be reserved for
     * empty space at the top of the stack.
     * @param p the padding percentage to use
     */
    public void setPaddingPercentage(double p) {
        if ( p < 0 || p > 1 )
            throw new IllegalArgumentException(
                    "Illegal padding percentage: " + p);
        m_padding = p;
    }
    
    /**
     * Get the minimum height threshold under which stacks should not be
     * made visible.
     * @return the minimum height threshold for visibility
     */
    public double getThreshold() {
        return m_threshold;
    }
    
    /**
     * Set the minimum height threshold under which stacks should not be
     * made visible.
     * @param threshold the minimum height threshold for visibility to use
     */
    public void setThreshold(double threshold) {
        m_threshold = (float)threshold;
    }
    
    /**
     * Get the range model describing the range occupied by the value
     * stack.
     * @return the stack range model
     */
    public ValuedRangeModel getRangeModel() {
        return m_model;
    }
    
// TODO: support externally driven range specification (i.e. stack zooming)
//    public void setRangeModel(NumberRangeModel model) {
//        m_model = model;
//    }
    
    // ------------------------------------------------------------------------
    
    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
        Arrays.fill(baseline, 0);
        
        bounds = getLayoutBounds();
        float inc = (float) (bounds.getMaxX()-bounds.getMinX())
                        / (columns.length-1);
        int len = columns.length;
        
        // first walk
        Iterator iter = m_vis.visibleItems(m_group);
        while ( iter.hasNext() ) {
            VisualItem item = (VisualItem)iter.next();
            for ( int i=0; i<columns.length; ++i ) {
                baseline[i] += item.getDouble(columns[i]);
            }
        }
        double maxValue = ArrayLib.max(baseline);
        maxValue += m_padding*maxValue;
        Arrays.fill(baseline, bounds.getMaxY());
        
        m_model.setValueRange(0, maxValue, 0, maxValue);
        
        // second walk
        //VisualItem prev = null;
        Table t = (Table)m_vis.getGroup(m_group);
        iter = t.tuplesReversed();
        while ( iter.hasNext() ) {
            VisualItem item = (VisualItem)iter.next();
            if ( !item.isVisible() ) continue;
            
            float height = 0;
            
            for ( int i=len; --i >= 0; ) {
                poly[2*(len-1-i)] = (float)bounds.getMinX() + i*inc;
                poly[2*(len-1-i)+1] = (float)baseline[i];
            }
            for ( int i=0; i<columns.length; ++i ) {
                int base = 2*(len+i);
                double value = item.getDouble(columns[i]);
                baseline[i] -= (float)bounds.getHeight() * 
                                 MathLib.linearInterp(value,0,maxValue);
                poly[base] = (float)bounds.getMinX() + i*inc;
                poly[base+1] = (float)baseline[i];
                height = Math.max(height, poly[2*(len-1-i)+1]-poly[base+1]);
            }
            if ( height < m_threshold ) {
                item.setVisible(false);
                // update previous item to prevent empty space
                //if ( prev != null )
                //    updateBaseline(prev, poly);
            }
//            } else {
//                prev = item;
//            }

            setX(item, null, 0);
            setY(item, null, 0);
            setPolygon(item, poly);
        }
    }
    
//    private void updateBaseline(VisualItem item, float[] poly) {
//        float[] a = getPolygon(item, m_field);
//        float[] e = getPolygon(item, m_end);
//        int len = poly.length/2;
//        System.arraycopy(poly, len, a, len, len);
//        System.arraycopy(poly, len, e, len, len);
//    }
    
    /**
     * Sets the polygon values for a visual item.
     */
    private void setPolygon(VisualItem item, float[] poly) {
        float[] a = getPolygon(item, m_field);
        float[] s = getPolygon(item, m_start);
        float[] e = getPolygon(item, m_end);
        System.arraycopy(a, 0, s, 0, a.length);
        System.arraycopy(poly, 0, a, 0, poly.length);
        System.arraycopy(poly, 0, e, 0, poly.length);
        item.setValidated(false);
    }
    
    /**
     * Get the polygon values for a visual item.
     */
    private float[] getPolygon(VisualItem item, String field) {
        float[] poly = (float[])item.get(field);
        if ( poly == null || poly.length < 4*columns.length ) {
            int len = columns.length;
            float inc = (float)(bounds.getMaxX()-bounds.getMinX())/(len-1);
            
            poly = new float[4*len];
            Arrays.fill(poly, (float)bounds.getMaxY());
            for ( int i=0; i<len; ++i ) {
                float x = i*inc + (float)bounds.getMinX();
                poly[2*(len+i)] = x;
                poly[2*(len-1-i)] = x;
            }
            item.set(field, poly);
        }
        return poly;
    }
    
} // end of class StackedLineChart
