package prefuse.action.assignment;

import prefuse.Constants;
import prefuse.data.Table;
import prefuse.data.column.ColumnMetadata;
import prefuse.data.tuple.TupleSet;
import prefuse.util.MathLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;

/**
 * <p>
 * Assignment Action that assigns size values for a group of items based upon
 * a data field. This action can be used to automatically vary item's on screen
 * sizes proportionally to an underlying data value. Sizes can be assigned along
 * a continuous scale, or can be binned into discrete size groups. Both 1D 
 * (length) and 2D (area) encodings are supported by this function.
 * 2D is assumed by default; use the setIs2DArea method to change this.</p>
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DataSizeAction extends SizeAction {
    
    // TODO: get data on just noticeable differences, use to select size
    // values more intelligently?
    
    protected String m_dataField;
    
    protected double m_baseSize = 1;
    protected double m_sizeRange;
    
    protected int m_scale = Constants.LINEAR_SCALE;
    protected int m_bins = Constants.CONTINUOUS;
    
    protected boolean m_inferBounds = true;
    protected boolean m_is2DArea = true;
    protected double m_minValue;
    protected double m_maxValue;
    
    /**
     * Create a new DataSizeAction.
     * @param group the data group to process
     * @param field the data field to base size assignments on
     */
    public DataSizeAction(String group, String field) {
        super(group);
        m_dataField = field;
    }

    /**
     * Create a new DataSizeAction.
     * @param group the data group to process
     * @param field the data field to base size assignments on
     * @param bins the number of discrete size values to use
     */
    public DataSizeAction(String group, String field, int bins) {
        this(group, field, bins, Constants.LINEAR_SCALE);
    }

    /**
     * Create a new DataSizeAction.
     * @param group the data group to process
     * @param field the data field to base size assignments on
     * @param bins the number of discrete size values to use
     * @param scale the scale type to use. One of
     * {@link prefuse.Constants#LINEAR_SCALE},
     * {@link prefuse.Constants#LOG_SCALE}, or
     * {@link prefuse.Constants#SQRT_SCALE}. 
     */
    public DataSizeAction(String group, String field, int bins, int scale) {
        super(group);
        m_dataField = field;
        setScale(scale);
        setBinCount(bins);
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Returns the data field used to encode size values.
     * @return the data field that is mapped to size values
     */
    public String getDataField() {
        return m_dataField;
    }
    
    /**
     * Set the data field used to encode size values.
     * @param field the data field to map to size values
     */
    public void setDataField(String field) {
        m_dataField = field;
    }
    
    /**
     * Returns the scale type used for encoding size values from the data.
     * @return the scale type. One of
     * {@link prefuse.Constants#LINEAR_SCALE}, 
     * {@link prefuse.Constants#SQRT_SCALE}, or
     * {@link prefuse.Constants#LOG_SCALE}.
     */
    public int getScale() {
        return m_scale;
    }
    
    /**
     * Set the scale (linear, square root, or log) to use for encoding size
     * values from the data.
     * @param scale the scale type to use. This value should be one of
     * {@link prefuse.Constants#LINEAR_SCALE}, 
     * {@link prefuse.Constants#SQRT_SCALE}, or
     * {@link prefuse.Constants#LOG_SCALE}.
     */
    public void setScale(int scale) {
        if ( scale < 0 || scale >= Constants.SCALE_COUNT )
            throw new IllegalArgumentException(
                    "Unrecognized scale value: "+scale);
        m_scale = scale;
    }
    
    /**
     * Returns the number of "bins" or distinct categories of sizes
     * @return the number of bins.
     */
    public int getBinCount() {
        return m_bins;
    }

    /**
     * Sets the number of "bins" or distinct categories of sizes
     * @param count the number of bins to set. The value
     * {@link Constants#CONTINUOUS} indicates not to use any binning.
     */
    public void setBinCount(int count) {
        m_bins = count;
    }
    
    /**
     * Indicates if the size values set by this function represent 2D areas.
     * That is, if the size is a 2D area or a 1D length. The size value will
     * be scaled appropriately to facilitate better perception of size
     * differences.
     * @return true if this instance is configured for area sizes, false for
     *  length sizes.
     * @see prefuse.util.PrefuseLib#getSize2D(double)
     */
    public boolean is2DArea() {
        return m_is2DArea;
    }
    
    /**
     * Sets if the size values set by this function represent 2D areas.
     * That is, if the size is a 2D area or a 1D length. The size value will
     * be scaled appropriately to facilitate better perception of size
     * differences.
     * @param isArea true to configure this instance for area sizes, false for
     *  length sizes
     * @see prefuse.util.PrefuseLib#getSize2D(double)
     */
    public void setIs2DArea(boolean isArea) {
        m_is2DArea = isArea;
    }

    /**
     * Gets the size assigned to the lowest-valued data items, typically 1.0.
     * @return the size for the lowest-valued data items
     */
    public double getBaseSize() {
        return m_baseSize;
    }

    /**
     * Sets the size assigned to the lowest-valued data items. By default,
     * this value is 1.0.
     * @param size the new size for the lowest-valued data items
     */
    public void setBaseSize(double size) {
        m_baseSize = size;
    }    
    
    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
        TupleSet ts = m_vis.getGroup(m_group);
        if ( !(ts instanceof Table) )
            return; // TODO: exception?
        Table t = (Table)ts;
        
        if ( !t.canGetDouble(m_dataField) ) 
            return; // TODO: exception ?
        
        ColumnMetadata md = t.getMetadata(m_dataField);
        if ( m_inferBounds ) {
            int minRow = md.getMinimumRow();
            int maxRow = md.getMaximumRow();
            m_minValue = t.getDouble(minRow, m_dataField);
            m_maxValue = t.getDouble(maxRow, m_dataField);
            m_sizeRange = m_maxValue/m_minValue - m_baseSize;
        }
        super.run(frac);
    }
    
    /**
     * @see prefuse.action.assignment.SizeAction#getSize(prefuse.visual.VisualItem)
     */
    public double getSize(VisualItem item) {
        double v = item.getDouble(m_dataField);
        double f = MathLib.interp(m_scale, v, m_minValue, m_maxValue);
        if ( m_bins < 1 ) {
            // continuous scale
            v = m_baseSize + f * m_sizeRange;
        } else {
            // binned sizes
            int bin = (int)((f-0.0001)*m_bins);
            v = m_baseSize + bin*(m_sizeRange/(m_bins-1));
        }
        // return the size value. if this action is configured to return
        // 2-dimensional sizes (ie area rather than length) then the
        // size value is appropriately scaled first
        return m_is2DArea ? PrefuseLib.getSize2D(v) : v;
    }
    
} // end of class DataSizeAction
