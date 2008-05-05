package prefuse.data.parser;

/**
 * Thrown when an invalid date is encountered in <tt>CalendarParser</tt>.
 * 
 * 
 * Created on 20/02/2008
 * 
 * @author <a href="http://www.jogiles.co.nz">Jonathan Giles</a>
 */

public class CalendarParserException extends Exception {

	public static enum ExceptionTypes {
		GENERIC_EXCEPTION {},
		ONLY_YEAR_FOUND_EXCEPTION {};
	}

	private ExceptionTypes exceptionType;

	// /**
	// * Default date format exception.
	// */
	// public CalendarParserException() {
	// super();
	// }

	/**
	 * Date format exception.
	 * 
	 * @param str
	 *            error message
	 */
	public CalendarParserException(final String str) {
		super(str);
	}

	public CalendarParserException(final String str, final ExceptionTypes type) {
		super(str);
		this.exceptionType = type;
	}

	public ExceptionTypes getExceptionType() {
		return exceptionType;
	}
}
