package prefuse.data.expression;

import prefuse.data.Tuple;

/**
 * Predicate representing an "or" clause of sub-predicates.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class OrPredicate extends CompositePredicate {

    /**
     * Create a new OrPredicate.
     * @param predicates the clauses of this predicate
     */
    public OrPredicate(Predicate ... predicates) {
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
        for(Predicate p : m_clauses) {
        	if(p.getBoolean(t)) {
        		return true;
        	}
        }
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return size() == 0 ? "FALSE" : toString("OR");
    }

} // end of class OrPredicate
