package test.prefuse.data.util.collections;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import prefuse.util.collections.CompositeList;

public class CompositeListTest extends TestCase {
	
	public void testBasic() {
		List<Integer> l1 = Arrays.asList(0, 1, 2);
		List<Integer> l2 = Arrays.asList(3, 4, 5);
		List<Integer> l3 = Arrays.asList(6, 7);
		List<Integer> l4 = Arrays.asList();
		List<Integer> l5 = Arrays.asList(8, 9);
		CompositeList<Integer> c = new CompositeList<Integer>(l1,l2,l3,l4,l5);
		assertEquals(10, c.size());
		for(int i = 0; i < 10; i++) {
			assertEquals((Integer) i, c.get(i));
		}
		try {
			c.get(-2);
			fail("Exception expected");
		} catch (IndexOutOfBoundsException e) {
			// fall through
		}
		try {
			c.get(10);
			fail("Exception expected");
		} catch (IndexOutOfBoundsException e) {
			// fall through
		}
	}

}
