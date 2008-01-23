package prefuse.action.assignment;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import prefuse.action.DataType;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.DataLib;
import prefuse.util.MathLib;
import prefuse.util.Scale;
import prefuse.visual.VisualItem;

/**
 * <p>
 * Assignment Action that assigns color values for a group of items based upon
 * a data field. The type of color encoding used is dependent upon the
 * reported data type. Nominal (categorical) data is encoded using a different
 * hue for each unique data value. Ordinal (ordered) and Numerical
 * (quantitative) data is shown using a grayscale color ramp. In all cases,
 * the default color palette used by this Action can be replaced with a
 * client-specified palette provided to the DataColorAction constructor.
 * </p>
 *
 * <p>
 * The color spectra for numerical data is continuous by default, but can also
 * be binned into a few discrete steps (see {@link #setBinCount(int)}).
 * Quantitative data can also be colored on different numerical scales. The
 * default scale is a linear scale (specified by
 * {@link Scale#LINEAR}), but logarithmic and square root scales can
 * be used (specified by {@link Scale#LOG} and
 * {@link Scale#SQRT} respectively. Finally, the scale can be broken
 * into quantiles, reflecting the statistical distribution of the values rather
 * than just the total data value range, using the
 * {@link Scale#QUANTILE} value. For the quantile scale to work, you
 * also need to specify the number of bins to use (see
 * {@link #setBinCount(int)}). This value will determine the number of
 * quantiles that the data should be divided into.
 * </p>
 *
 * </p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DataColorAction extends ColorAction {

    private String m_dataField;
    private DataType    m_type;
    private Scale    m_scale = Scale.LINEAR;
    private Scale    m_tempScale;

    private double[] m_dist;
    private int      m_bins = BinConstants.CONTINUOUS;
    private Map<Object,Integer>      m_omap;
    private Object[] m_olist;
    private final ColorMap m_cmap = new ColorMap(null,0,1);
    private int[]    m_palette;


    /**
     * Create a new DataColorAction
     * @param group the data group to process
     * @param dataField the data field to base size assignments on
     * @param dataType the data type to use for the data field.
     * @param colorField the color field to assign
     */
    public DataColorAction(String group, String dataField,
    		DataType dataType, String colorField)
    {
        super(group, colorField);
        setDataType(dataType);
        setDataField(dataField);
    }

    /**
     * Create a new DataColorAction
     * @param group the data group to process
     * @param dataField the data field to base size assignments on
     * @param dataType the data type to use for the data field
     * @param colorField the color field to assign
     * @param palette the color palette to use. See
     * {@link prefuse.util.ColorLib} for color palette generators.
     */
    public DataColorAction(String group, String dataField,
            DataType dataType, String colorField, int[] palette)
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
     * @return the data type used by this action
     */
    public DataType getDataType() {
        return m_type;
    }

    /**
     * @param type the data type used by this action
     */
    public void setDataType(DataType type) {
        if ( type == DataType.UNKNOWN) {
			throw new IllegalArgumentException(
                    "Unrecognized data type: "+type);
		}
        m_type = type;
    }

    /**
     * Returns the scale type used for encoding color values from the data.
     * This value is only used for {@link DataType#NUMERICAL}
     * data.
     * @return the scale type
     */
    public Scale getScale() {
        return m_scale;
    }

    /**
     * Set the scale (linear, square root, or log) to use for encoding color
     * values from the data. This value is only used for
     * {@link DataType#NUMERICAL} data.
     * @param scale the scale type to use.
     * If {@link Scale#QUANTILE} is used, the number of
     * bins to use must also be specified to a value greater than zero using
     * the {@link #setBinCount(int)} method.
     */
    public void setScale(Scale scale) {
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
     * {@link BinConstants#CONTINUOUS} indicates not to use any binning. If the
     * scale type set using the {@link #setScale(Scale)} method is
     * {@link Scale#QUANTILE}, the bin count <strong>must</strong>
     * be greater than zero.
     */
    public void setBinCount(int count) {
        if ( m_scale == Scale.QUANTILE && count <= 0 ) {
            throw new IllegalArgumentException(
                "The quantile scale can not be used without binning. " +
                "Use a bin value greater than zero.");
        }
        m_bins = count;
    }

    /**
     * This operation is not supported by the DataColorAction type.
     * Calling this method will result in a thrown exception.
     * @see prefuse.action.assignment.ColorAction#setDefaultColor(int)
     * @throws UnsupportedOperationException
     */
    @Override
	public void setDefaultColor(int color) {
        throw new UnsupportedOperationException();
    }

    /**
     * Manually sets the ordered list of values to use for color assignment.
     * Normally, this ordering is computed using the methods of the
     * {@link prefuse.util.DataLib} class. This method allows you to set your
     * own custom ordering. This ordering corresponds to the ordering of
     * colors in this action's color palette. If the provided array of values
     * is missing a value contained within the data, an exception will result
     * during execution of this action.
     * @param values the ordered list of values. If this array is missing a
     * value contained within data processed by this action, an exception
     * will be thrown when this action is run.
     */
    public void setOrdinalMap(Object[] values) {
        m_olist = values;
        m_omap = new HashMap<Object,Integer>();
        for ( int i=0; i<values.length; ++i ) {
            m_omap.put(values[i], new Integer(i));
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Set up the state of this encoding Action.
     * @see prefuse.action.EncoderAction#setup()
     */
    @Override
	protected void setup() {
        int size = 64;

        int[] palette = m_palette;

        // switch up scale if necessary
        m_tempScale = m_scale;
        if ( m_scale == Scale.QUANTILE && m_bins <= 0 ) {
            Logger.getLogger(getClass().getName()).warning(
                    "Can't use quantile scale with no binning. " +
                    "Defaulting to linear scale. Set the bin value " +
                    "greater than zero to use a quantile scale.");
            m_scale = Scale.LINEAR;
        }

        // compute distribution and color map
        switch ( m_type ) {
        case NOMINAL:
        case ORDINAL:
            m_dist = getDistribution();
            size = m_omap.size();
            palette = m_palette!=null ? m_palette : createPalette(size);
            m_cmap.setColorPalette(palette);
            m_cmap.setMinValue(m_dist[0]);
            m_cmap.setMaxValue(m_dist[1]);
            return;
        case NUMERICAL:
            m_dist = getDistribution();
            size = m_bins > 0 ? m_bins : size;
            palette = m_palette!=null ? m_palette : createPalette(size);
            m_cmap.setColorPalette(palette);
            m_cmap.setMinValue(0.0);
            m_cmap.setMaxValue(1.0);
            return;
        }
    }

    @Override
	protected void finish() {
        // reset scale in case it needed to be changed due to errors
        m_scale = m_tempScale;
    }

    /**
     * Computes the distribution (either min/max or quantile values) used to
     * help assign colors to data values.
     */
    protected double[] getDistribution() {
        TupleSet<? extends VisualItem<?>> ts = m_vis.getGroup(m_group);

        if ( m_type == DataType.NUMERICAL ) {
            m_omap = null;
            if ( m_scale == Scale.QUANTILE && m_bins > 0 ) {
                double[] values =
                        DataLib.toDoubleArray(ts.tuples(), m_dataField);
                return MathLib.quantiles(m_bins, values);
            } else {
                double[] dist = new double[2];
                dist[0] = DataLib.min(ts, m_dataField).getDouble(m_dataField);
                dist[1] = DataLib.max(ts, m_dataField).getDouble(m_dataField);
                return dist;
            }
        } else {
            if ( m_olist == null ) {
				m_omap = DataLib.ordinalMap(ts, m_dataField);
			}
            return new double[] { 0, m_omap.size()-1 };
        }
    }

    /**
     * Create a color palette of the requested type and size.
     */
    protected int[] createPalette(int size) {
        switch ( m_type ) {
        case NOMINAL:
            return ColorLib.getCategoryPalette(size);
        case NUMERICAL:
        case ORDINAL:
        default:
            return ColorLib.getGrayscalePalette(size);
        }
    }

    /**
     * @see prefuse.action.assignment.ColorAction#getColor(prefuse.visual.VisualItem)
     */
    @Override
	public int getColor(VisualItem<?> item) {
        // check for any cascaded rules first
        Object o = lookup(item);
        if ( o != null ) {
            if ( o instanceof ColorAction ) {
                return ((ColorAction)o).getColor(item);
            } else if ( o instanceof Integer ) {
                return ((Integer)o).intValue();
            } else {
                Logger.getLogger(this.getClass().getName())
                    .warning("Unrecognized Object from predicate chain.");
            }
        }

        // otherwise perform data-driven assignment
        switch ( m_type ) {
        case NUMERICAL:
            double v = item.getDouble(m_dataField);
            double f = MathLib.interp(m_scale, v, m_dist);
            return m_cmap.getColor(f);
        default:
            Integer idx = m_omap.get(item.get(m_dataField));
            return m_cmap.getColor(idx.doubleValue());
        }
    }

} // end of class DataColorAction
