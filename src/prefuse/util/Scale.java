package prefuse.util;

/**
 *
 * @author Anton Marsden
 */
public enum Scale {
    /** A linear scale */
    LINEAR,
    /** A logarithmic (base 10) scale */
    LOG,
    /** A square root scale */
    SQRT,
    /** A quantile scale, based on the underlying distribution */
    QUANTILE

}
