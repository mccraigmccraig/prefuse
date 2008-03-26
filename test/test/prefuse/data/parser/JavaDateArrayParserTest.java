package test.prefuse.data.parser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import prefuse.data.parser.DataParser;
import prefuse.data.parser.JavaDateArrayParser;

public class JavaDateArrayParserTest extends AbstractParserTest {

	private Calendar calendar;

	public void setUp() {
		calendar = Calendar.getInstance();
	}

	@Override
	protected Collection<?> getObjectsToPass() {
		calendar.clear();
		calendar.set(2007, 1, 1);
		Date d0 = calendar.getTime();
		Date d1 = new Date(0L);
		calendar.set(1997, 1, 1);
		Date d2 = calendar.getTime();
		return Arrays.asList(new Date[][] {
				new Date[] {},
				new Date[] {d0},
				new Date[] {d0, d1},
				new Date[] {d2}
		});
	}

	protected Collection<?> getObjectsToFail() {
		return Arrays.asList(new Object[] {
		});
	}

	@Override
	protected Collection<String> getStringsToPass() {
		return Arrays.asList(new String[] {
				"[1/02/07 00:00]",
				"[1/02/97 00:00]",
				"[1/02/07 00:00,31/01/97 00:00]",
		});
	}

	protected Collection<String> getStringsToFail() {
		return Arrays.asList(new String[] {
				"",
				"[1/2/07 00:00]", // no leading 0 in month ... maybe this should pass???
		});
	}

	@Override
	protected DataParser getParserInstance() {
		return new JavaDateArrayParser();
	}



}
