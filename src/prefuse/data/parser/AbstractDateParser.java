package prefuse.data.parser;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

public abstract class AbstractDateParser implements DataParser {

	public final boolean canParse(String text) {
        try {
            parse(text);
            return true;
        } catch ( DataParseException e ) {
            return false;
        }
	}

    public final String format(Object value) {
        return value==null ? null : m_dfmt.format(value);
    }

	public abstract Class<?> getType();

	public final Object parse(String text) throws DataParseException {
		return parseDate(text);
	}

	public abstract Date parseDate(String text) throws DataParseException;

    protected DateFormat    m_dfmt;
    protected ParsePosition m_pos;

    /**
     * Create a new DateParser.
     */
    public AbstractDateParser() {
        this(DateFormat.getDateInstance(DateFormat.SHORT));
    }

    /**
     * Create a new DateParser.
     * @param dateFormat the DateFormat instance to use for parsing
     */
    public AbstractDateParser(DateFormat dateFormat) {
        m_dfmt = dateFormat;
        m_pos = new ParsePosition(0);
    }

    public AbstractDateParser(Locale locale) {
    	this(DateFormat.getDateInstance(DateFormat.SHORT, locale));
	}

}

