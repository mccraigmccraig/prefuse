package prefuse.data.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import prefuse.data.Tuple;
import prefuse.data.expression.Predicate;

/**
 * Iterator over tuples that filters the output by a given predicate.
 *
 * TODO: TIDY THIS CLASS UP!!!
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class FilterIterable <T extends Tuple<?>> implements Iterable<T> {

	public static <T extends Tuple<?>> FilterIterable<T> createFilterIterable(Iterable<T> tuples, Predicate p) {
		return new FilterIterable<T>(tuples, p);
	}

    private final Predicate predicate;
    private Iterator<T> tuples;
    private T next;

    /**
     * Create a new FilterIterator.
     * @param tuples an iterator over tuples
     * @param p the filter predicate to use
     */
    public FilterIterable(Iterable<T> tuples, Predicate p) {
        this.predicate = p;
        this.tuples = tuples.iterator();
    }

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			{
				next = advance();
			}

		    private T advance() {
		        while ( tuples.hasNext() ) {
		            T t = tuples.next();
		            if ( predicate.getBoolean(t) ) {
		                return t;
		            }
		        }
		        tuples = null;
		        next = null;
		        return null;
		    }

		    /**
		     * @see java.util.Iterator#next()
		     */
		    public T next() {
		        if ( !hasNext() ) {
		            throw new NoSuchElementException("No more elements");
		        }
		        T retval = next;
		        next = advance();
		        return retval;
		    }

		    /**
		     * @see java.util.Iterator#hasNext()
		     */
		    public boolean hasNext() {
		        return tuples != null;
		    }

		    /**
		     * Not supported.
		     * @see java.util.Iterator#remove()
		     */
		    public void remove() {
		        throw new UnsupportedOperationException();
		    }

		};
	}

} // end of class FilterIterator
