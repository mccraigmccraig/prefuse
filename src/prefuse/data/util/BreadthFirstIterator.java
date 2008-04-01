package prefuse.data.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.util.collections.Queue;

/**
 * Provides a distance-limited breadth first traversal over nodes, edges,
 * or both, using any number of traversal "roots".
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class BreadthFirstIterator implements Iterator<Tuple<?>> {

	protected Queue m_queue = new Queue();
    protected int   m_depth;
    protected TraversalType  m_traversal;

    /**
     * Create an uninitialized BreadthFirstIterator. Use the
     * {@link #init(Iterable, int, prefuse.data.util.BreadthFirstIterator.TraversalType)} method to initialize the iterator.
     */
    public BreadthFirstIterator() {
        // do nothing, requires init call
    }

    /**
     * Create a new BreadthFirstIterator starting from the given source node.
     * @param n the source node from which to begin the traversal
     * @param depth the maximum graph distance to traverse
     * @param traversal the traversal type
     */
    public BreadthFirstIterator(Node<?,?> n, int depth, TraversalType traversal) {
        init(Collections.singleton(n), depth, traversal);
    }

    /**
     * Create a new BreadthFirstIterator starting from the given source nodes.
     * @param it an Iterator over the source nodes from which to begin the
     * traversal
     * @param depth the maximum graph distance to traverse
     * @param traversal the traversal type
     */
    public BreadthFirstIterator(Iterable<Tuple<?>> it, int depth, TraversalType traversal) {
        init(it, depth, traversal);
    }

    /**
     * Initialize (or re-initialize) this iterator.
     * @param o Either a source node or iterator over source nodes
     * @param depth the maximum graph distance to traverse
     * @param traversal the traversal type
     */
    public void init(Iterable<? extends Tuple<?>> o, int depth, TraversalType traversal) {
        // initialize the member variables
        m_queue.clear();
        m_depth = depth;
        m_traversal = traversal;

        // seed the queue
        // TODO: clean this up? (use generalized iterator?)
        if (traversal.isTraverseNodes()) {
            for (Tuple<?> t : o) {
				m_queue.add(t, 0);
			}
        } else {
			for (Tuple<?> t : o) {
				// TODO: graceful error handling when non-node in set?
				Node<?, ?> n = (Node<?, ?>) t;
				m_queue.visit(n, 0);
				for (Edge e : getEdges(n)) {
					Node<?, ?> nn = e.getAdjacentNode(n);
					m_queue.visit(nn, 1);
					if (m_queue.getDepth(e) < 0) {
						m_queue.add(e, 1);
					}
				}
			}
		}
    }

    // ------------------------------------------------------------------------

    /**
	 * @see java.util.Iterator#remove()
	 */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return !m_queue.isEmpty();
    }

    /**
     * Determines which edges are traversed for a given node.
     * @param n a node
     * @return an iterator over edges incident on the node
     */
    protected List<Edge<?,?>> getEdges(Node<?,?> n) {
        return (List<Edge<?,?>>) (Object) n.edges(); // TODO: add support for all edges, in links only, out links only
    }

    /**
     * Get the traversal depth at which a particular tuple was encountered.
     * @param t the tuple to lookup
     * @return the traversal depth of the tuple, or -1 if the tuple has not
     * been visited by the traversal.
     */
    public int getDepth(Tuple<?> t) {
        return m_queue.getDepth(t);
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Tuple<?> next() {
        Tuple<?> t = (Tuple<?>)m_queue.removeFirst();

        switch ( m_traversal ) {

        case NODE_TRAVERSAL:
        case NODE_AND_EDGE_TRAVERSAL:
            for ( ; true; t = (Tuple<?>)m_queue.removeFirst() ) {
                if ( t instanceof Edge ) {
                    return t;
                } else {
                    Node<?,?> n = (Node<?,?>)t;
                    int d = m_queue.getDepth(n);

                    if ( d < m_depth ) {
                        int dd = d+1;
                        for(Edge e : getEdges(n)) {
                            Node<?,?> v = e.getAdjacentNode(n);

                            if ( m_traversal.isTraverseEdges() && m_queue.getDepth(e) < 0 ) {
								m_queue.add(e, dd);
							}
                            if ( m_queue.getDepth(v) < 0 ) {
								m_queue.add(v, dd);
							}
                        }
                    }
                    else if ( m_traversal.isTraverseEdges() && d == m_depth )
                    {
                        for(Edge e : getEdges(n) ) {
                            Node<?,?> v = e.getAdjacentNode(n);
                            int dv = m_queue.getDepth(v);
                            if ( dv > 0 && m_queue.getDepth(e) < 0 ) {
                                m_queue.add(e, Math.min(d,dv));
                            }
                        }
                    }
                    return n;
                }
            }

        case EDGE_TRAVERSAL:
            Edge<?,?> e = (Edge<?,?>)t;
            Node<?,?> u = e.getSourceNode();
            Node<?,?> v = e.getTargetNode();
            int du = m_queue.getDepth(u);
            int dv = m_queue.getDepth(v);

            if ( du != dv ) {
                Node<?,?> n = dv > du ? v : u;
                int  d = Math.max(du, dv);

                if ( d < m_depth ) {
                    int dd = d+1;
                    for (Edge ee : getEdges(n)) {
                        if ( m_queue.getDepth(ee) >= 0 ) {
							continue; // already visited
						}

                        Node<?,?> nn = ee.getAdjacentNode(n);
                        m_queue.visit(nn, dd);
                        m_queue.add(ee, dd);
                    }
                }
            }
            return e;

        default:
            throw new IllegalStateException();
        }
    }

} // end of class BreadthFirstIterator
