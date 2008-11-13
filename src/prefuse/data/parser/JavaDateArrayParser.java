package prefuse.data.parser;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * DataParser instance the parses an array of java.util.Date values from a text string.
 * Values are expected to be comma separated and can be within brackets,
 * parentheses, or curly braces.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class JavaDateArrayParser implements DataParser {

    private final JavaDateParser dateParser;

    public JavaDateArrayParser(DateFormat dateFormat) {
    	this.dateParser = new JavaDateParser(dateFormat);
    }

    public JavaDateArrayParser() {
        this.dateParser = new JavaDateParser();
    }

    public JavaDateArrayParser(Locale locale) {
        this.dateParser = new JavaDateParser(locale);
    }

    /**
     * Returns java.util.Date[].class.
     *
     * @see prefuse.data.parser.DataParser#getType()
     */
    public Class<?> getType() {
        return Date[].class;
    }

    /**
     * @see prefuse.data.parser.DataParser#format(java.lang.Object)
     */
    public String format(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof Date[])) {
            throw new IllegalArgumentException(
                    "This class can only format Objects of type java.util.Date[].");
        }

        Date[] values = (Date[]) value;
        StringBuffer sbuf = new StringBuffer();
        sbuf.append('[');
        for (int i = 0; i < values.length; ++i) {
            if (i > 0) {
                sbuf.append(",");
            }
            sbuf.append(dateParser.format(values[i]));
        }
        sbuf.append(']');
        return sbuf.toString();
    }

    /**
     * @see prefuse.data.parser.DataParser#canParse(java.lang.String)
     */
    public boolean canParse(String text) {

        try {
            StringTokenizer st = new StringTokenizer(text, "\"[](){},");
            while (st.hasMoreTokens()) {
                dateParser.parseDate(st.nextToken());
            }
            return true;
        } catch (DataParseException e) {
            return false;
        }
    }

    /**
     * Parse a Date array from a text string.
     *
     * @param text
     *            the text string to parse
     * @return the parsed Date array
     * @throws DataParseException
     *             if an error occurs during parsing
     */
    public Object parse(String text) throws DataParseException {
        StringTokenizer st = new StringTokenizer(text, "\"[](){},");
        Date[] array = new Date[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); ++i) {
            String tok = st.nextToken();
            array[i] = dateParser.parseDate(tok);
        }
        return array;
    }

} // end of class LongArrayParser
