package prefuse.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator implementation that combines the results of multiple iterators.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class CompositeIterable<X> implements Iterable<X> {

    private Iterable<? extends X>[] m_iters;

    public CompositeIterable(int size) {
        m_iters = new Iterable[size];
    }

    public CompositeIterable(Iterable<? extends X> ... iters) {
        m_iters = iters;
    }

    public void setIterator(int idx, Iterable<? extends X> iter) {
        m_iters[idx] = iter;
    }

    public Iterator<X> iterator() {
    	return new Iterator<X>() {
    		private Iterator<? extends X> curIter;
    		private int m_cur = 0;
    	    /**
    	     * Not supported.
    	     * @see java.util.Iterator#remove()
    	     */
    	    public void remove() {
    	        throw new UnsupportedOperationException();
    	    }

    	    /**
    	     * @see java.util.Iterator#next()
    	     */
    	    public X next() {
    	        if ( hasNext() ) {
    	            return curIter.next();
    	        } else {
    	            throw new NoSuchElementException();
    	        }
    	    }

    	    /**
    	     * @see java.util.Iterator#hasNext()
    	     */
    	    public boolean hasNext() {
    	        if ( m_iters == null ) {
    				return false;
    			}

    	        while ( true ) {
    	            if ( m_cur >= m_iters.length ) {
    	                m_iters = null;
    	                return false;
    	            }
    	            if(curIter == null && m_iters[m_cur] != null) {
	            		curIter = m_iters[m_cur].iterator();
    	            }
    	            if(curIter != null) {
    	            	if(curIter.hasNext()) {
    	            		return true;
    	            	}
    	            	curIter = null;
    	            	m_cur++;
    	            } else {
    	            	m_cur++;
    	            }
    	        }
    	    }

    	};
    }


} // end of class CompositeIterator
