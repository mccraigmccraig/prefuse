package test.prefuse.data.parser;

import java.lang.reflect.Array;
import java.util.Collection;

import prefuse.data.parser.DataParser;
import junit.framework.TestCase;

public abstract class AbstractParserTest extends TestCase {

	public void testObjectsToPass() throws Exception {
		DataParser parser = getParserInstance();
		for (Object o : getObjectsToPass()) {
			// make sure the object is of a valid class
			assertTrue(parser.getType().isAssignableFrom(o.getClass()));
			String formatted = parser.format(o);
			assertTrue(parser.canParse(formatted));
			Object o2 = parser.parse(formatted);
			if (o.getClass().isArray()) {
				assertEquals(o.getClass(), o2.getClass());
				assertTrue(arrayEquals(o, o2));
			} else {
				assertEquals(o, o2);
			}
		}
	}

	public void testObjectsToFail() throws Exception {
		DataParser parser = getParserInstance();
		for (Object o : getObjectsToFail()) {
			try {
				String formatted = parser.format(o);
				boolean canParse = parser.canParse(formatted);
				Object o2 = null;
				try {
					o2 = parser.parse(formatted);
					assertTrue(canParse);
				} catch (Exception ex) {
					assertFalse(canParse);
					throw ex;
				}

				if (o.getClass().isArray()) {
					if(o.getClass().equals(o2.getClass())) {
						assertFalse(arrayEquals(o, o2));
					}
				} else {
					assertFalse(o.equals(o2));
				}

			} catch (Exception ex) {
				// a good thing
			}
		}
	}

	public void testStringsToPass() throws Exception {
		DataParser parser = getParserInstance();
		for (String formatted : getStringsToPass()) {
			Object o = parser.parse(formatted);
			// make sure the object is of a valid class
			assertTrue(parser.getType().isAssignableFrom(o.getClass()));
			String output = parser.format(o);
			assertEquals(formatted, output);
		}
	}

	public void testStringsToFail() throws Exception {
		DataParser parser = getParserInstance();
		for (String formatted : getStringsToFail()) {
			Object o = null;
			try {
				o = parser.parse(formatted);
				String output = parser.format(o);
				assertFalse(formatted.equals(output));
			} catch (Exception ex) {
				// a good thing
			}
		}
	}

	public boolean arrayEquals(Object a1, Object a2) {
		if(Array.getLength(a1) != Array.getLength(a2)) {
			return false;
		}
		final int len = Array.getLength(a1);
		for (int i = 0; i < len; i++) {
			if(!Array.get(a1, i).equals(Array.get(a2, i))) {
				return false;
			}
		}
		return true;
	}

	protected abstract DataParser getParserInstance();

	protected abstract Collection<?> getObjectsToPass();

	protected abstract Collection<?> getObjectsToFail();

	protected abstract Collection<String> getStringsToPass();

	protected abstract Collection<String> getStringsToFail();
}
