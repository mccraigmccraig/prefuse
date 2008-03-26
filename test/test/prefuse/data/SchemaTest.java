package test.prefuse.data;

import prefuse.data.Schema;
import junit.framework.TestCase;

public class SchemaTest extends TestCase {

	public void testEquals() {
		Schema s1 = new Schema();
		s1.addColumn("c1", int.class);
		
		Schema s2 = new Schema();
		s2.addColumn("c1", int.class);
		
		assertEquals(s1, s2);
	}

	public void testLock() {
		Schema s1 = new Schema();
		s1.addColumn("c1", int.class);
		s1.lockSchema();
		try {
			s1.addColumn("c2", int.class);
			fail("Shouldn't be allowed to modify a locked schema!");
		} catch (Exception ex) {
			// expected
		}
	}
	
	public void testClone() throws Exception {
		Schema s1 = new Schema();
		s1.addColumn("c1", int.class);
		s1.lockSchema();
		
		assertTrue(s1.isLocked());
		Schema s2 = (Schema) s1.clone();
				assertEquals(s1, s2);
		assertFalse(s2.isLocked());
		assertEquals(s1, s2);
		s2.addColumn("c2", int.class);
		assertTrue(!s1.equals(s2));
	}

}
