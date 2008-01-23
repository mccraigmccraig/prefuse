package prefuse.data.tuple;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.util.StringLib;
import prefuse.util.collections.IntIterator;

/**
 * Manager class for Tuples. There is a unique Tuple for each row of a table.
 * All data structures and Tuples are created lazily, on an as-needed basis.
 * When a row is deleted from the table, it's corresponding Tuple (if created)
 * is invalidated before being removed from this data structure, ensuring that
 * any other live references to the Tuple can't be used to corrupt the table.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public abstract class TupleManager <T extends Tuple<?>> {

    protected Graph<T,?,?>        m_graph;
    protected Table<T>        m_table;

    private T[] m_tuples;

    /**
     * Create a new TupleManager for the given Table.
     * @param t the data Table to generate Tuples for
     */
    public TupleManager(Table<T> t, Graph<?,?,?> g) {
        init(t, g);
    }

    public abstract T createTupleInstance();

    /**
     * Initialize this TupleManager for use with a given Table.
     * @param t the data Table to generate Tuples for
     */
    public void init(Table<?> t, Graph<?,?,?> g) {
        if ( m_table != null ) {
            throw new IllegalStateException(
                "This TupleManager has already been initialized");
        }
        m_table = (Table<T>) t;
        m_graph = (Graph<T,?,?>) g;
        m_tuples = null;
    }

    /**
     * Ensure the tuple array exists.
     */
	private void ensureTupleArray(int row) {
        int nrows = Math.max(m_table.getRowCount(), row+1);
        if ( m_tuples == null ) {
            m_tuples = (T[]) Array.newInstance(Tuple.class, nrows);
        } else if ( m_tuples.length < nrows ) {
            int capacity = Math.max(3*m_tuples.length/2 + 1, nrows);
            T[] tuples = (T[]) Array.newInstance(Tuple.class, capacity);
            System.arraycopy(m_tuples, 0, tuples, 0, m_tuples.length);
            m_tuples = tuples;
        }
    }

    /**
     * Get a Tuple corresponding to the given row index.
     * @param row the row index
     * @return the Tuple corresponding to the given row
     */
    public T getTuple(int row) {
        if ( m_table.isValidRow(row) ) {
            ensureTupleArray(row);
            if ( m_tuples[row] == null ) {
                return m_tuples[row] = newTuple(row);
            } else {
                return m_tuples[row];
            }
        } else {
            // TODO: return null instead?
            throw new IllegalArgumentException("Invalid row index: "+row);
        }
    }

    /**
     * Instantiate a new Tuple instance for the given row index.
     * @param row the row index of the tuple
     * @return the newly created Tuple
     */
    protected T newTuple(int row) {
        try {
            T t = createTupleInstance();
            t.init(m_table, m_graph, row);
            return t;
        } catch ( Exception e ) {
            Logger.getLogger(getClass().getName()).warning(
                e.getMessage()+"\n"+StringLib.getStackTrace(e));
            return null;
        }
    }

    /**
     * Invalidate the tuple at the given row.
     * @param row the row index to invalidate
     */
    public void invalidate(int row) {
        if ( m_tuples == null || row < 0 || row >= m_tuples.length ) {
            return;
        } else if ( m_tuples[row] != null ) {
            m_tuples[row].invalidate();
            m_tuples[row] = null;
        }
    }

    /**
     * Invalidate all tuples managed by this TupleManager
     */
    public void invalidateAll() {
        if ( m_tuples == null ) {
			return;
		}
        for ( int i=0; i<m_tuples.length; ++i ) {
			invalidate(i);
		}
    }

    /**
     * Return an iterator over the tuples in this manager.
     * @param rows an iterator over table rows
     * @return an iterator over the tuples indicated by the input row iterator
     */
    public Iterable<T> iterable(IntIterator rows) {
        return new TupleManagerIterable(rows);
    }

    public List<T> list(List<Integer> rows) {
    	return new TupleManagerList(rows);
    }

    private class TupleManagerList extends AbstractList<T> {

    	private final List<Integer> intList;

    	public TupleManagerList(List<Integer> intList) {
    		this.intList = intList;
    	}

		@Override
		public T get(int index) {
            return getTuple(intList.get(index));
		}

		@Override
		public int size() {
			return intList.size();
		}

    }

    // ------------------------------------------------------------------------
    // TupleManagerIterator

    /**
     * Iterator instance for iterating over tuples managed in a TupleManager.
     */
    private class TupleManagerIterable implements Iterable<T> {

        private final IntIterator m_rows;

        /**
         * Create a new TupleManagerIterator.
         * @param tuples the TupleManager from which to get the tuples
         * @param rows the rows to iterate over
         */
        public TupleManagerIterable(IntIterator rows) {
            m_rows = rows;
        }

		public Iterator<T> iterator() {
			return new Iterator<T>() {
		        /**
		         * @see java.util.Iterator#hasNext()
		         */
		        public boolean hasNext() {
		            return m_rows.hasNext();
		        }

		        /**
		         * @see java.util.Iterator#next()
		         */
		        public T next() {
		            return getTuple(m_rows.nextInt());
		        }

		        /**
		         * @see java.util.Iterator#remove()
		         */
		        public void remove() {
		            // TODO: check to see if this is safe
		            m_rows.remove();
		        }
			};
		}

    } // end of inner class TupleManagerIterator

} // end of class TupleManager
