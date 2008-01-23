package prefuse.data.tuple;

import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.SwingPropertyChangeSupport;

import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.EventConstants;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Expression;
import prefuse.data.expression.Predicate;
import prefuse.data.util.FilterIterableFactory;
import prefuse.data.util.Sort;
import prefuse.data.util.SortedTupleIterable;

/**
 * Abstract base class for TupleSet implementations. Provides mechanisms for
 * generating filtered tuple iterators, maintain listeners, and supporting
 * bound client properties.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public abstract class AbstractTupleSet <T extends Tuple<?>> implements TupleSet<T> {

    /**
     * @see prefuse.data.tuple.TupleSet#tuples(prefuse.data.expression.Predicate)
     */
    public Iterable<T> tuples(Predicate filter) {
        if ( filter == null ) {
            return tuples();
        } else {
            return FilterIterableFactory.tuples(this, filter);
        }
    }

    /**
     * @see prefuse.data.tuple.TupleSet#tuples(prefuse.data.expression.Predicate, prefuse.data.util.Sort)
     */
    public Iterable<T> tuples(Predicate filter, Sort sort) {
        if ( sort == null ) {
            return tuples(filter);
        } else {
            Comparator<T> c = sort.<T>getComparator(this);
            return new SortedTupleIterable<T>(tuples(filter),getTupleCount(),c);
        }
    }


    // -- TupleSet Methods ----------------------------------------------------

    private CopyOnWriteArrayList<TupleSetListener> m_tupleListeners;

    /**
     * @see prefuse.data.tuple.TupleSet#addTupleSetListener(prefuse.data.event.TupleSetListener)
     */
    public void addTupleSetListener(TupleSetListener tsl) {
        if ( m_tupleListeners == null ) {
			m_tupleListeners = new CopyOnWriteArrayList<TupleSetListener>();
		}
        if ( !m_tupleListeners.contains(tsl) ) {
			m_tupleListeners.add(tsl);
		}
    }

    /**
     * @see prefuse.data.tuple.TupleSet#removeTupleSetListener(prefuse.data.event.TupleSetListener)
     */
    public void removeTupleSetListener(TupleSetListener tsl) {
        if ( m_tupleListeners != null ) {
            m_tupleListeners.remove(tsl);
        }
    }

    /**
     * Fire a Tuple event.
     * @param t the Table on which the event has occurred
     * @param start the first row changed
     * @param end the last row changed
     * @param type the type of event, one of
     * {@link prefuse.data.event.EventConstants#INSERT} or
     * {@link prefuse.data.event.EventConstants#DELETE}.
     */
    protected void fireTupleEvent(Table<?> t, int start, int end, int type) {
        if ( m_tupleListeners != null && m_tupleListeners.size() > 0 ) {
            Tuple<?>[] tuples = new Tuple[end-start+1];
            for ( int i=0, r=start; r <= end; ++r, ++i ) {
                tuples[i] = t.getTuple(r);
            }
            for(TupleSetListener l : m_tupleListeners) {
                if ( type == EventConstants.INSERT ) {
                    l.tupleSetChanged(this, tuples, EMPTY_ARRAY);
                } else {
                    l.tupleSetChanged(this, EMPTY_ARRAY, tuples);
                }
            }
        }
    }

    /**
     * Fire a Tuple event.
     * @param t the tuple that has been added or removed
     * @param type the type of event, one of
     * {@link prefuse.data.event.EventConstants#INSERT} or
     * {@link prefuse.data.event.EventConstants#DELETE}.
     */
    protected void fireTupleEvent(Tuple<?> t, int type) {
        if ( m_tupleListeners != null && m_tupleListeners.size() > 0 ) {
            Tuple<?>[] ts = new Tuple[]       {t};
            for(TupleSetListener l : m_tupleListeners) {
                if ( type == EventConstants.INSERT ) {
                    l.tupleSetChanged(this, ts, EMPTY_ARRAY);
                } else {
                    l.tupleSetChanged(this, EMPTY_ARRAY, ts);
                }
            }
        }
    }

    /**
     * Fire a Tuple event.
     * @param added array of Tuples that have been added, can be null
     * @param removed array of Tuples that have been removed, can be null
     */
    @SuppressWarnings("unchecked")
	protected void fireTupleEvent(Tuple[] added, Tuple[] removed) {
        if ( m_tupleListeners != null && m_tupleListeners.size() > 0 ) {
            added = added==null ? (T[]) EMPTY_ARRAY : added;
            removed = removed==null ? (T[]) EMPTY_ARRAY : removed;
            for(TupleSetListener l : m_tupleListeners) {
                l.tupleSetChanged(this, added, removed);
            }
        }
    }

    // -- Data Field Methods --------------------------------------------------

    /**
     * False by default.
     * @see prefuse.data.tuple.TupleSet#isAddColumnSupported()
     */
    public boolean isAddColumnSupported() {
        return false;
    }

    /**
     * @see prefuse.data.tuple.TupleSet#addColumns(prefuse.data.Schema)
     */
    public void addColumns(Schema schema) {
        if ( isAddColumnSupported() ) {
            for ( int i=0; i<schema.getColumnCount(); ++i ) {
                try {
                    addColumn(schema.getColumnName(i),
                              schema.getColumnType(i),
                              schema.getDefault(i));
                } catch ( IllegalArgumentException iae ) {
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Unsupported by default.
     * @see prefuse.data.tuple.TupleSet#addColumn(java.lang.String, java.lang.Class, java.lang.Object)
     */
    public void addColumn(String name, Class<?> type, Object defaultValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported by default.
     * @see prefuse.data.tuple.TupleSet#addColumn(java.lang.String, java.lang.Class)
     */
    public void addColumn(String name, Class<?> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported by default.
     * @see prefuse.data.tuple.TupleSet#addColumn(java.lang.String, prefuse.data.expression.Expression)
     */
    public void addColumn(String name, Expression expr) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported by default.
     * @see prefuse.data.tuple.TupleSet#addColumn(java.lang.String, java.lang.String)
     */
    public void addColumn(String name, String expr) {
        throw new UnsupportedOperationException();
    }

    // -- Client Properties ---------------------------------------------------

    private Map<String, Object> m_props;
    private SwingPropertyChangeSupport m_propSupport;

    /**
     * @see prefuse.data.tuple.TupleSet#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener lstnr) {
        if ( lstnr == null ) {
			return;
		}
        if ( m_propSupport == null ) {
			m_propSupport = new SwingPropertyChangeSupport(this);
		}
        m_propSupport.addPropertyChangeListener(lstnr);
    }

    /**
     * @see prefuse.data.tuple.TupleSet#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(String key,
                                          PropertyChangeListener lstnr)
    {
        if ( lstnr == null ) {
			return;
		}
        if ( m_propSupport == null ) {
			m_propSupport = new SwingPropertyChangeSupport(this);
		}
        m_propSupport.addPropertyChangeListener(key, lstnr);
    }

    /**
     * @see prefuse.data.tuple.TupleSet#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener lstnr) {
        if ( lstnr == null ) {
			return;
		}
        if ( m_propSupport == null ) {
			return;
		}
        m_propSupport.removePropertyChangeListener(lstnr);
    }

    /**
     * @see prefuse.data.tuple.TupleSet#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(String key,
                                             PropertyChangeListener lstnr)
    {
        if ( lstnr == null ) {
			return;
		}
        if ( m_propSupport == null ) {
			return;
		}
        m_propSupport.removePropertyChangeListener(key, lstnr);
    }

    /**
     * @see prefuse.data.tuple.TupleSet#putClientProperty(java.lang.String, java.lang.Object)
     */
    public void putClientProperty(String key, Object value) {
        Object prev = null;
        if ( m_props == null && value == null ) {
            // nothing to do
            return;
        } else if ( value == null ) {
            prev = m_props.remove(key);
        } else {
            if ( m_props == null ) {
				m_props = new HashMap<String,Object>(2);
			}
            prev = m_props.put(key, value);
        }
        if ( m_propSupport != null ) {
			m_propSupport.firePropertyChange(key, prev, value);
		}
    }

    /**
     * @see prefuse.data.tuple.TupleSet#getClientProperty(java.lang.String)
     */
    public Object getClientProperty(String key) {
        return m_props == null ? null : m_props.get(key);
    }

} // end of class AbstractTupleSet
