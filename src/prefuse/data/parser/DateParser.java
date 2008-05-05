package prefuse.data.parser;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * DataParser instance that parses Date values as java.sql.Time instances, representing a particular
 * date (but not a specific time on that day). This class uses a backing
 * {@link java.text.DateFormat} instance to perform parsing. The DateFormat instance to use can be
 * passed in to the constructor, or by default the DateFormat returned by
 * {@link java.text.DateFormat#getDateInstance(int)} with an argument of
 * {@link java.text.DateFormat#SHORT} is used.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DateParser extends AbstractDateParser {

	protected DateFormat m_dfmt;
	protected ParsePosition m_pos;

	/**
	 * Create a new DateParser.
	 */
	public DateParser() {
		this(DateFormat.getDateInstance(DateFormat.SHORT));
	}

	/**
	 * Create a new DateParser.
	 * 
	 * @param dateFormat
	 *            the DateFormat instance to use for parsing
	 */
	public DateParser(final DateFormat dateFormat) {
		m_dfmt = dateFormat;
		m_pos = new ParsePosition(0);
	}

	public DateParser(final Locale locale) {
		this(DateFormat.getDateInstance(DateFormat.SHORT, locale));
	}

	/**
	 * Returns java.sql.Date.
	 * 
	 * @see prefuse.data.parser.DataParser#getType()
	 */
	@Override
	public Class<?> getType() {
		return Date.class;
	}

	/**
	 * Parse a Date value from a text string.
	 * 
	 * @param text
	 *            the text string to parse
	 * @return the parsed Date value
	 * @throws DataParseException
	 *             if an error occurs during parsing
	 */
	@Override
	public java.util.Date parseDate(final String text) throws DataParseException {
		m_pos.setErrorIndex(0);
		m_pos.setIndex(0);

		// parse the data value, convert to the wrapper type
		Date d = null;

		if (d == null) {
			try {
				d = Date.valueOf(text);
				m_pos.setIndex(text.length());
			} catch (final IllegalArgumentException e) {
				d = null;
			}
		}

		if (d == null) {
			final java.util.Date d1 = m_dfmt.parse(text, m_pos);
			if (d1 != null) {
				d = new Date(d1.getTime());
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

} // end of class DateParser
