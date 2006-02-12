/**
 * 
 */
package prefuse.util;

import prefuse.data.Tuple;
import prefuse.data.expression.Expression;
import prefuse.data.expression.IfExpression;
import prefuse.data.expression.ObjectLiteral;
import prefuse.data.expression.Predicate;

/**
 * A chain of Predicates and associated values, maintain a large
 * if-statement structure for looking up values based on a Predicate
 * condition. 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class PredicateChain {
    
    private Expression m_head = new ObjectLiteral(null);
    private IfExpression m_tail = null;
    
    /**
     * Return the backing predicate chain as an Expression instance.
     * @return the predicate chain, either an IfExpression or
     * the single terminal default Expression instance.
     */
    public Expression getExpression() {
        return m_head;
    }
    
    /**
     * Evaluate the predicate chain for the given Tuple.
     * @param t the Tuple
     * @return the object associated with the first Predicate
     * that successfully matches the Tuple.
     */
    public Object get(Tuple t) {
        return m_head.get(t);
    }
    
    /**
     * Add a new rule to the end of the chain, associating a Predicate
     * condition with an Object value.
     * @param p the Predicate condition
     * @param val the associated Object value
     */
    public void add(Predicate p, Object val) {
        if ( m_tail == null ) {
            m_tail = new IfExpression(p, new ObjectLiteral(val), m_head);
            m_head = m_tail;
        } else {
            IfExpression ie = new IfExpression(p, new ObjectLiteral(val),
                                               m_tail.getElseExpression());
            m_tail.setElseExpression(ie);
            m_tail = ie;
        }
    }
    
} // end of class PredicateChain
