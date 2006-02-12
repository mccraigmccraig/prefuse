package prefuse.data.parser;


/**
 * Interface for data parsers, which parse a data value from a text String.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public interface DataParser {
    
    /**
     * Get the data type for the values parsed by this parser.
     * @return the parsed data type for this parser as a Java Class instance
     */
    public Class getType();
    
    /**
     * Indicates if the given text string can be successfully parsed by
     * this parser.
     * @param text the text string to check for parsability
     * @return true if the string can be successfully parsed into this
     * parser's data type, false otherwise
     */
    public boolean canParse(String text);
    
    /**
     * Parse the given text string to a data value.
     * @param text the text string to parse
     * @return the parsed data value, which will be an instance of the
     * Class returned by the {@link #getType()} method
     * @throws DataParseException if an error occurs during parsing
     */
    public Object parse(String text) throws DataParseException; 
   
} // end of interface DataParser
