package prefuse.visual;

import prefuse.data.Edge;

/**
 * VisualItem that represents an edge in a graph. This interface combines
 * the {@link VisualItem} interface with the {@link prefuse.data.Edge}
 * interface.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public interface EdgeItem <N extends NodeItem<N,E>, E extends EdgeItem<N,E>> extends VisualItem<E>, Edge<N,E> {

    /**
     * Get the first, or source, NodeItem upon which this edge is incident.
     * @return the source NodeItem
     */
    public NodeItem<N,E> getSourceItem();

    /**
     * Get the second, or target, NodeItem upon which this edge is incident.
     * @return the target NodeItem
     */
    public NodeItem<N,E> getTargetItem();

} // end of interface EdgeItem
