package prefuse.data.expression;

import prefuse.data.Tuple;

/**
 * Predicate representing the negation of another predicate.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class NotPredicate extends AbstractPredicate {

    private final Predicate m_predicate;

    /**
     * Create a new NotPredicate.
     * @param p the predicate to negate
     */
    public NotPredicate(Predicate p) {
        m_predicate = p;
    }

    /**
     * Get the negated predicate.
     * @return the negated predicate
     */
    public Predicate getPredicate() {
        return m_predicate;
    }

    /**
     * @see prefuse.data.expression.Expression#getBoolean(prefuse.data.Tuple)
     */
    @Override
	public boolean getBoolean(Tuple<?> t) {
        return !m_predicate.getBoolean(t);
    }

    /**
     * @see prefuse.data.expression.Expression#visit(prefuse.data.expression.ExpressionVisitor)
     */
    @Override
	public void visit(ExpressionVisitor v) {
        v.visitExpression(this);
        v.down();
        m_predicate.visit(v);
        v.up();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return "NOT "+m_predicate.toString();
    }

    /**
     * @see prefuse.data.expression.AbstractExpression#addChildListeners()
     */
    @Override
	protected void addChildListeners() {
        m_predicate.addExpressionListener(this);
    }

    /**
     * @see prefuse.data.expression.AbstractExpression#removeChildListeners()
     */
    @Override
	protected void removeChildListeners() {
        m_predicate.removeExpressionListener(this);
    }

} // end of class NotPredicate