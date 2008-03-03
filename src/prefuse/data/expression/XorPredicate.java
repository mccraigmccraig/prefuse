package prefuse.data.expression;

import java.util.Iterator;

import prefuse.data.Tuple;

/**
 * Predicate representing an "xor" clause of sub-predicates.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class XorPredicate extends CompositePredicate {

    /**
     * Create a new XorPredicate.
     * @param predicates the clauses of this predicate
     */
    public XorPredicate(Predicate ... predicates) {
        super(predicates);
    }

    /**
     * @see prefuse.data.expression.Expression#getBoolean(prefuse.data.Tuple)
     */
    @Override
	public boolean getBoolean(Tuple<?> t) {
        if ( m_clauses.isEmpty() ) {
			return false;
		}

        boolean val = false;
        Iterator<?> iter = m_clauses.iterator();
        if ( iter.hasNext() ) {
            val = ((Predicate)iter.next()).getBoolean(t);
        }
        while ( iter.hasNext() ) {
            val ^= ((Predicate)iter.next()).getBoolean(t);
        }
        return val;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return size() == 0 ? "FALSE" : toString("XOR");
    }

} // end of class XorPredicate
