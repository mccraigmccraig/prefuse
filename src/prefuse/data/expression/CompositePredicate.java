package prefuse.data.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract base class for Predicate instances that maintain one or
 * more sub-predicates (clauses).
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public abstract class CompositePredicate extends AbstractPredicate implements Cloneable {

    protected List<Predicate> m_clauses;

    /**
     * Create a new CompositePredicate.
     * @param predicates the predicates
     */
    public CompositePredicate(Predicate ... predicates) {
    	m_clauses = new ArrayList<Predicate>(predicates.length);
    	for (Predicate element : predicates) {
    		m_clauses.add(element);
    	}
    }

    // ------------------------------------------------------------------------

    /**
     * Add a new clause.
     * @param p the Predicate clause to add
     */
    public void add(Predicate p) {
        if ( m_clauses.contains(p) ) {
            throw new IllegalArgumentException("Duplicate predicate.");
        }
        m_clauses.add(p);
        fireExpressionChange();
    }

    /**
     * Remove a new clause.
     * @param p the Predicate clause to remove
     * @return true if removed, false if not found
     */
    public boolean remove(Predicate p) {
        if ( m_clauses.remove(p) ) {
            fireExpressionChange();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove all clauses.
     */
    public void clear() {
        removeChildListeners();
        m_clauses.clear();
        fireExpressionChange();
    }

    /**
     * Get the number of sub-predicate clauses.
     * @return the number of clauses
     */
    public int size() {
        return m_clauses.size();
    }

    /**
     * Get the sub-predicate at the given index.
     * @param idx the index to lookup
     * @return the sub-predicate at the given index
     */
    public Predicate get(int idx) {
        return m_clauses.get(idx);
    }

    /**
     * Set the given predicate to be the only clause of this composite.
     * @param p the new sole sub-predicate clause
     */
    public void set(Predicate p) {
        removeChildListeners();
        m_clauses.clear();
        m_clauses.add(p);
        if ( hasListeners() ) {
			addChildListeners();
		}
        fireExpressionChange();
    }

    /**
     * Set the given predicates to be the clauses of this composite.
     * @param p the new sub-predicate clauses
     */
    public void set(Predicate ... p) {
        removeChildListeners();
        m_clauses.clear();
        for ( int i=0; i<p.length; ++i ) {
            if ( !m_clauses.contains(p) ) {
				m_clauses.add(p[i]);
			}
        }
        if ( hasListeners() ) {
			addChildListeners();
		}
        fireExpressionChange();
    }

    /**
     * Get a predicate instance just like this one but without
     * the given predicate as a clause.
     * @param p the predicate clause to ignore
     * @return a clone of this predicate, only without the input predicate
     */
    public Predicate getSubPredicate(Predicate p) {
        CompositePredicate cp = null;
        try {
            cp  = (CompositePredicate) clone();
        } catch (CloneNotSupportedException ex) {
      		throw new RuntimeException(ex);
        }
        cp.remove(p);
        return cp;
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.data.expression.Expression#visit(prefuse.data.expression.ExpressionVisitor)
     */
    @Override
	public void visit(ExpressionVisitor v) {
        v.visitExpression(this);
        Iterator<Predicate> iter = m_clauses.iterator();
        while ( iter.hasNext() ) {
            v.down();
            ((Expression)iter.next()).visit(v);
            v.up();
        }
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.data.expression.AbstractExpression#addChildListeners()
     */
    @Override
	protected void addChildListeners() {
        Iterator<Predicate> iter = m_clauses.iterator();
        while ( iter.hasNext() ) {
            ((Expression)iter.next()).addExpressionListener(this);
        }
    }

    /**
     * @see prefuse.data.expression.AbstractExpression#removeChildListeners()
     */
    @Override
	protected void removeChildListeners() {
        Iterator<Predicate> iter = m_clauses.iterator();
        while ( iter.hasNext() ) {
            ((Expression)iter.next()).removeExpressionListener(this);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Return a String representation of this predicate.
     * @param op a String describing the operation this Predicate performs
     * @return a String representing this Expression
     */
    protected String toString(String op) {
        if ( m_clauses.size() == 1 ) {
            return m_clauses.get(0).toString();
        }

        StringBuffer sbuf = new StringBuffer();
        sbuf.append('(');

        Iterator<Predicate> iter = m_clauses.iterator();
        while ( iter.hasNext() ) {
            sbuf.append(iter.next().toString());
            if ( iter.hasNext() ) {
                sbuf.append(" ");
                sbuf.append(op);
                sbuf.append(" ");
            }
        }

        sbuf.append(')');
        return sbuf.toString();
    }

    protected Object clone() throws CloneNotSupportedException {
    	CompositePredicate clone = (CompositePredicate) super.clone();
    	// deep copy the clauses
    	clone.m_clauses = new ArrayList(clone.m_clauses);
    	return clone;
    }

} // end of abstract class CompositePredicate
