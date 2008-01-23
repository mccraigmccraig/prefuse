/**
 *
 */
package prefuse.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Tuple;

/**
 * Iterator that provides a sorted iteration over a set of tuples.
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class SortedTupleIterable<T extends Tuple<?>> implements Iterable<T> {

    private List<T> m_tuples;
    private Comparator<? super T> m_cmp;

    /**
     * Create a new SortedTupleIterator that sorts tuples in the given
     * iterator using the given comparator.
     * @param iter the source iterator of tuples
     * @param c the comparator to use for sorting
     */
    public SortedTupleIterable(Iterable<T> iter, Comparator<? super T> c) {
        this(iter, 128, c);
    }

    /**
     * Create a new SortedTupleIterator that sorts tuples in the given
     * iterator using the given comparator.
     * @param iter the source iterator of tuples
     * @param size the expected number of tuples in the iterator
     * @param c the comparator to use for sorting
     */
    public SortedTupleIterable(Iterable<T> iter, int size, Comparator<? super T> c) {
        m_tuples = new ArrayList<T>(size);
        init(iter, c);
    }

    /**
     * Initialize this iterator for the given source iterator and
     * comparator.
     * @param iterable the source iterator of tuples
     * @param c the comparator to use for sorting
     */
    public void init(Iterable<T> iterable, Comparator<? super T> c) {
    	m_tuples.clear();
        for(T t : iterable) {
        	m_tuples.add(t);
        }
        m_cmp = c;
        // sort tuple list
        Collections.sort(m_tuples, m_cmp);
    }

	public Iterator<T> iterator() {
		return m_tuples.iterator();
	}

} // end of class SortedTupleIterator
