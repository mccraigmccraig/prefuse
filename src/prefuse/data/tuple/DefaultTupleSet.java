package prefuse.data.tuple;

import java.util.LinkedHashSet;

import prefuse.data.Tuple;
import prefuse.data.event.EventConstants;

/**
 * <p>TupleSet implementation that maintains a set of heterogeneous Tuples
 * -- tuples that can come from any backing data source. This class supports
 * {@link #addTuple(Tuple)} and {@link #removeTuple(Tuple)} but does not
 * support adding new columns to contained tuples.</p>
 *
 * <p>This TupleSet uses a {@link java.util.LinkedHashSet} to support fast
 * lookup of contained tuples while maintaining Tuples in the order in which
 * they are added to the set.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DefaultTupleSet<T extends Tuple<?>> extends AbstractTupleSet<T> implements EventConstants
{
    protected LinkedHashSet<T> m_tuples;

    /**
     * Create a new, empty DefaultTupleSet.
     */
    public DefaultTupleSet() {
        m_tuples = new LinkedHashSet<T>();
    }

    /**
     * @see prefuse.data.tuple.TupleSet#getTupleCount()
     */
    public int getTupleCount() {
        return m_tuples.size();
    }

    /**
     * @see prefuse.data.tuple.TupleSet#addTuple(prefuse.data.Tuple)
     */
    public T addTuple(T t) {
        t = addInternal(t);
        if ( t != null ) {
			fireTupleEvent(t, INSERT);
		}
        return t;
    }

    /**
     * @see prefuse.data.tuple.TupleSet#setTuple(prefuse.data.Tuple)
     */
    public T setTuple(T t) {
        Tuple<?>[] rem = clearInternal();
        t = addInternal(t);
        Tuple<?>[] add = t==null ? null : new Tuple[] {t};
        fireTupleEvent(add, rem);
        return t;
    }

    /**
     * Adds a tuple without firing a notification.
     * @param t the Tuple to add
     * @return the added Tuple
     */
    protected final T addInternal(T t) {
        if ( m_tuples.add(t) ) {
            return t;
        } else {
            return null;
        }
    }

    /**
     * @see prefuse.data.tuple.TupleSet#containsTuple(prefuse.data.Tuple)
     */
    public boolean containsTuple(Tuple<?> t) {
        return m_tuples.contains(t);
    }

    /**
     * @see prefuse.data.tuple.TupleSet#removeTuple(prefuse.data.Tuple)
     */
    public boolean removeTuple(Tuple<?> t) {
        boolean b = removeInternal(t);
        if ( b ) {
			fireTupleEvent(t, DELETE);
		}
        return b;
    }

    /**
     * Removes a tuple without firing a notification.
     * @param t the tuple to remove
     * @return true if the tuple is removed successfully, false otherwise
     */
    protected final boolean removeInternal(Tuple<?> t) {
        return m_tuples.remove(t);
    }

    /**
     * @see prefuse.data.tuple.TupleSet#clear()
     */
    public void clear() {
        if ( getTupleCount() > 0 ) {
            Tuple<?>[] t = clearInternal();
            fireTupleEvent(null, t);
        }
    }

    /**
     * Clear the internal state without firing a notification.
     * @return an array of removed tuples
     */
    public Tuple<?>[] clearInternal() {
        Tuple<?>[] t = new Tuple[getTupleCount()];
        int i = 0;
        for(Tuple<?> tuple : tuples()) {
        	t[i++] = tuple;
        }
        m_tuples.clear();
        return t;
    }

    /**
     * @see prefuse.data.tuple.TupleSet#tuples()
     */
    public Iterable<T> tuples() {
        return m_tuples;
    }

    /**
     * Get the contents of this TupleSet as an array.
     * @return the contents of this TupleSet as an array
     */
    public Tuple<?>[] toArray() {
        Tuple<?>[] t = new Tuple[getTupleCount()];
        m_tuples.toArray(t);
        return t;
    }

} // end of class DefaultTupleSet
