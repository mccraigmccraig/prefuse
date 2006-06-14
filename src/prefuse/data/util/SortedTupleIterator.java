/**
 * 
 */
package prefuse.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import prefuse.data.Tuple;

/**
 * Iterator that provides a sorted iteration over a set of tuples.
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class SortedTupleIterator implements Iterator {

    private ArrayList m_tuples;
    private Comparator m_cmp;
    private Iterator m_iter;
    
    public SortedTupleIterator(Iterator iter, Comparator c) {
        this(iter, 128, c);
    }
    
    public SortedTupleIterator(Iterator iter, int size, Comparator c) {
        m_tuples = new ArrayList(size);
        init(iter, c);
    }
    
    public void init(Iterator iter, Comparator c) {
        m_tuples.clear();
        m_cmp = c;
        
        // populate tuple list
        while ( iter.hasNext() ) {
            Tuple t = (Tuple)iter.next();
            m_tuples.add(t);
        }
        // sort tuple list
        Collections.sort(m_tuples, m_cmp);
        // create sorted iterator
        m_iter = m_tuples.iterator();
    }
    
    public boolean hasNext() {
        return m_iter.hasNext();
    }

    public Object next() {
        return m_iter.next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

} // end of class SortedTupleIterator