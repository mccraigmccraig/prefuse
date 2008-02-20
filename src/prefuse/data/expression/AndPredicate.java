package prefuse.data.expression;

import java.util.Iterator;

import prefuse.data.Tuple;

/**
 * Predicate representing an "and" clause of sub-predicates.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class AndPredicate extends CompositePredicate {

    /**
     * Create an empty AndPredicate. Empty AndPredicates return false
     * by default.
     */
    public AndPredicate() {
    }

    /**
     * Create a new AndPredicate.
     * @param p1 the sole clause of this predicate
     */
    public AndPredicate(Predicate p1) {
        add(p1);
    }

    /**
     * Create a new AndPredicate.
     * @param predicates the clauses of this predicate
     */
    public AndPredicate(Predicate ... predicates) {
        super(predicates);
    }

    /**
     * @see prefuse.data.expression.Expression#getBoolean(prefuse.data.Tuple)
     */
    @Override
	public boolean getBoolean(Tuple<?> t) {
        if ( m_clauses.size() == 0 ) {
			return false;
		}

        Iterator<Predicate> iter = m_clauses.iterator();
        while ( iter.hasNext() ) {
            Predicate p = iter.next();
            if ( !p.getBoolean(t) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return size() == 0 ? "FALSE" : toString("AND");
    }

} // end of class AndPredicate
