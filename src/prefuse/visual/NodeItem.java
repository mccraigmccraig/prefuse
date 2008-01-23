package prefuse.visual;

import prefuse.data.Node;

/**
 * VisualItem that represents a node in a graph. This interface combines
 * the {@link VisualItem} interface with the {@link prefuse.data.Node}
 * interface.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public interface NodeItem <N extends NodeItem<N,E>, E extends EdgeItem<N,E>> extends VisualItem<N>, Node<N,E>  {

} // end of interface NodeItem
