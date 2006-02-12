package prefuse.data.parser;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParsePosition;

/**
 * DataParser instance that parses Date values as java.util.Date instances,
 * representing a particular date and time.
 * This class uses a backing {@link java.text.DateFormat} instance to
 * perform parsing. The DateFormat instance to use can be passed in to the
 * constructor, or by default the DateFormat returned by
 * {@link java.text.DateFormat#getDateTimeInstance(int, int)} with both
 * arguments being {@link java.text.DateFormat#SHORT} is used.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DateTimeParser implements DataParser {
    
    protected DateFormat    m_dfmt;
    protected ParsePosition m_pos;
    
    /**
     * Create a new DateTimeParser.
     */
    public DateTimeParser() {
        this(DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT));
    }
    
    /**
     * Create a new DateTimeParser.
     * @param dateFormat the DateFormat instance to use for parsing
     */
    public DateTimeParser(DateFormat dateFormat) {
        m_dfmt = dateFormat;
        m_pos = new ParsePosition(0);
    }
    
    /**
     * Returns java.util.Date.class.
     * @see prefuse.data.parser.DataParser#getType()
     */
    public Class getType() {
        return Date.class;
    }
    
    /**
     * @see prefuse.data.parser.DataParser#canParse(java.lang.String)
     */
    public boolean canParse(String text) {
        try {
            parseDate(text);
            return true;
        } catch ( DataParseException e ) {
            return false;
        }
    }
    
    /**
     * @see prefuse.data.parser.DataParser#parse(java.lang.String)
     */
    public Object parse(String text) throws DataParseException {
        return parseDate(text);
    }
    
    /**
     * Parse a Date value from a text string.
     * @param text the text string to parse
     * @return the parsed Date value
     * @throws DataParseException if an error occurs during parsing
     */
    public Date parseDate(String text) throws DataParseException {
        m_pos.setErrorIndex(0);
        m_pos.setIndex(0);
        
        // parse the data value
        Date d = m_dfmt.parse(text, m_pos);
        
        // date format will parse substrings successfully, so we need
        // to check the position to make sure the whole value was used
        if ( d == null || m_pos.getIndex() < text.length() ) {
            throw new DataParseException("Could not parse Date: "+text);
        } else {
            return d;
        }
    }
         
} // end of class DateTimeParser
