package prefuse.data.parser;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DataParser instance that parses Date values as java.util.Date instances, representing a
 * particular date and time. This class uses a backing {@link java.text.DateFormat} instance to
 * perform parsing. The DateFormat instance to use can be passed in to the constructor, or by
 * default the DateFormat returned by {@link java.text.DateFormat#getDateTimeInstance(int, int)}
 * with both arguments being {@link java.text.DateFormat#SHORT} is used.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class JavaDateParser extends AbstractDateParser {

	/**
	 * Create a new DateTimeParser.
	 */
	public JavaDateParser() {
		this(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT));
	}

	/**
	 * Create a new DateTimeParser.
	 * 
	 * @param dateFormat
	 *            the DateFormat instance to use for parsing
	 */
	public JavaDateParser(final DateFormat dateFormat) {
		super(dateFormat);
	}

	public JavaDateParser(final Locale locale) {
		this(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale));
	}

	@Override
	public Class<?> getType() {
		return Date.class;
	}

	@Override
	public Date parseDate(final String text) throws DataParseException {
		m_pos.setErrorIndex(0);
		m_pos.setIndex(0);

		// parse the data value, convert to the wrapper type
		Date d = null;
		try {
			d = new Date(java.sql.Date.valueOf(text).getTime());
			m_pos.setIndex(text.length());
		} catch (final IllegalArgumentException e) {
			d = m_dfmt.parse(text, m_pos);
		}

		if (d == null) {
			try {
				d = CalendarParser.parse(text).getTime();
				m_pos.setIndex(text.length());
			} catch (final CalendarParserException e) {
				d = null;
			}
		}

		// date format will parse substrings successfully, so we need
		// to check the position to make sure the whole value was used
		if (d == null || m_pos.getIndex() < text.length()) {
			throw new DataParseException("Could not parse Date: " + text);
		} else {
			return d;
		}
	}

}
