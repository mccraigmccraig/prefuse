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

} // end of interface EdgeItem
