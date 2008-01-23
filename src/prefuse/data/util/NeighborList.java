package prefuse.data.util;

import java.util.AbstractList;
import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Node;

/**
 * Iterator over neighbors of a given Node. Resolves Edge instances to
 * provide direct iteration over the Node instances.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class NeighborList<N extends Node<N,E>, E extends Edge<N,E>> extends AbstractList<N> {

    private final List<E> m_edges;
    private final N m_node;


    /**
     * Create a new NeighborIterator.
     * @param n the source node
     * @param edges the node edges to iterate over
     */
    public NeighborList(N n, List<E> edges) {
        m_node = n;
        m_edges = edges;
    }

	@Override
	public N get(int index) {
		return m_edges.get(index).getAdjacentNode(m_node);
	}

	@Override
	public int size() {
		return m_edges.size();
	}

}
