package prefuse.action.assignment;

import java.util.Map;

import prefuse.Constants;
import prefuse.data.Table;
import prefuse.data.column.ColumnMetadata;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.MathLib;
import prefuse.visual.VisualItem;

/**
 * <p>
 * Assignment Action that assigns color values for a group of items based upon
 * a data field. The type of color encoding used is dependent upon the
 * reported data type. Nominal (categorical) data is encoded using a different
 * hue for each unique data value. Ordinal (ordered) data is shown using
 * a grayscale color ramp. Numerical (quantitative) data is mapped into
 * a color spectrum based on the range of the values. The color spectrum
 * is continuous by default, but can also be binned into a few discrete
 * steps (see {@link #setBinCount(int)}). In all cases, the default color
 * palette used by this Action can be replaced with a user-specified palette
 * provided to the DataColorAction constructor.
 * </p>
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DataColorAction extends ColorAction {

    private String m_dataField;
    private int    m_type;
    private int    m_scale = Constants.LINEAR_SCALE;
    
    private double   m_min;
    private double   m_max;
    private int      m_bins = Constants.CONTINUOUS;
    private Map      m_omap;
    private ColorMap m_cmap = new ColorMap(null,0,1);
    private int[]    m_palette;
    
    /**
     * Create a new DataColorAction
     * @param group the data group to process
     * @param dataField the data field to base size assignments on
     * @param dataType the data type to use for the data field. One of
     * {@link prefuse.Constants#LINEAR_SCALE},
     * {@link prefuse.Constants#LOG_SCALE}, or
     * {@link prefuse.Constants#SQRT_SCALE}. 
     * @param colorField the color field to assign
     */
    public DataColorAction(String group, String dataField, 
                             int dataType, String colorField)
    {
        super(group, colorField);
        setDataType(dataType);
        setDataField(dataField);
    }
    
    /**
     * Create a new DataColorAction
     * @param group the data group to process
     * @param dataField the data field to base size assignments on
     * @param dataType the data type to use for the data field. One of
     * {@link prefuse.Constants#LINEAR_SCALE},
     * {@link prefuse.Constants#LOG_SCALE}, or
     * {@link prefuse.Constants#SQRT_SCALE}. 
     * @param colorField the color field to assign
     * @param palette the color palette to use. See
     * {@link prefuse.util.ColorLib} for color palette generators.
     */
    public DataColorAction(String group, String dataField, 
            int dataType, String colorField, int[] palette)
    {
        super(group, colorField);
        setDataType(dataType);
        setDataField(dataField);
        m_palette = palette;
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
     * Return the data type used by this action. This value is one of
     * {@link prefuse.Constants#NOMINAL}, {@link prefuse.Constants#ORDINAL},
     * {@link prefuse.Constants#NUMERICAL}, or
     * {@link prefuse.Constants#UNKNOWN}.
     * @return the data type used by this action
     */
    public int getDataType() {
        return m_type;
    }
    
    /**
     * Set the data type used by this action.
     * @param type the data type used by this action, one of
     * {@link prefuse.Constants#NOMINAL}, {@link prefuse.Constants#ORDINAL},
     * {@link prefuse.Constants#NUMERICAL}, or
     * {@link prefuse.Constants#UNKNOWN}.
     */
    public void setDataType(int type) {
        if ( type < 0 || type >= Constants.DATATYPE_COUNT )
            throw new IllegalArgumentException(
                    "Unrecognized data type: "+type);
        m_type = type;
    }
    
    /**
     * Returns the scale type used for encoding color values from the data.
     * This value is only used for {@link prefuse.Constants#NUMERICAL}
     * data.
     * @return the scale type. One of
     * {@link prefuse.Constants#LINEAR_SCALE}, 
     * {@link prefuse.Constants#SQRT_SCALE}, or
     * {@link prefuse.Constants#LOG_SCALE}.
     */
    public int getScale() {
        return m_scale;
    }
    
    /**
     * Set the scale (linear, square root, or log) to use for encoding color
     * values from the data. This value is only used for
     * {@link prefuse.Constants#NUMERICAL} data.
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
     * Returns the number of "bins" or discrete steps of color. This value
     * is only used for numerical data.
     * @return the number of bins.
     */
    public int getBinCount() {
        return m_bins;
    }

    /**
     * Sets the number of "bins" or or discrete steps of color. This value
     * is only used for numerical data.
     * @param count the number of bins to set. The value 
     * {@link Constants#CONTINUOUS} indicates not to use any binning.
     */
    public void setBinCount(int count) {
        m_bins = count;
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
        setup(t);
        
        super.run(frac);
    }
    
    /**
     * Set up the state of this function for the provided Table.
     */
    protected void setup(Table t) {
        ColumnMetadata md = t.getMetadata(m_dataField);
        int size = 64;
        
        int[] palette = m_palette;
        
        switch ( m_type ) {
        case Constants.NOMINAL:
        case Constants.ORDINAL:
            m_omap = md.getOrdinalMap(); 
            m_min = 0;
            m_max = size = m_omap.size()-1;
            palette = (m_palette!=null ? m_palette 
                                       : createPalette(m_type, size));
            m_cmap.setColorPalette(palette);
            m_cmap.setMinValue(m_min); m_cmap.setMaxValue(m_max);
            return;
        case Constants.NUMERICAL:
            m_min = t.getDouble(md.getMinimumRow(), m_dataField);
            m_max = t.getDouble(md.getMaximumRow(), m_dataField);
            m_omap = null;
            size = m_bins > 0 ? m_bins : size;
            palette = (m_palette!=null ? m_palette 
                                       : createPalette(m_type, size));
            m_cmap.setColorPalette(palette);
            m_cmap.setMinValue(0.0); m_cmap.setMaxValue(1.0);
            return;
        }
    }
    
    /**
     * Create a color palette of the requested type and size.
     */
    protected static int[] createPalette(int type, int size) {
        switch ( type ) {
        case Constants.NOMINAL:
            return ColorLib.getCategoryPalette(size);
        case Constants.NUMERICAL:
            return ColorLib.getHotPalette(size);
        case Constants.ORDINAL:
        default:
            return ColorLib.getGrayscalePalette(size);
        }
    }
    
    /**
     * @see prefuse.action.assignment.ColorAction#getColor(prefuse.visual.VisualItem)
     */
    public int getColor(VisualItem item) {
        switch ( m_type ) {
        case Constants.NUMERICAL:
            double v = item.getDouble(m_dataField);
            double f = MathLib.interp(m_scale, v, m_min, m_max);
            return m_cmap.getColor(f);
        default:
            Integer idx = (Integer)m_omap.get(item.get(m_dataField));
            return m_cmap.getColor(idx.doubleValue());
        }
    }
    
} // end of class DataColorAction
