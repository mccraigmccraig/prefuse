package prefuse.action;

/**
 *
 * @author Anton Marsden
 */
public enum DataType {
    UNKNOWN,
    /** A nominal (categorical) data type */
    NOMINAL,
    /** An ordinal (ordered) data type */
    ORDINAL,
    /** A numerical (quantitative) data type */
    NUMERICAL;
    /** The total number of data type values */
}
