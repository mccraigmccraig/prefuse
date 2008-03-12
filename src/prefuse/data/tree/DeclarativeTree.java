package prefuse.data.tree;

import java.util.List;

import prefuse.action.layout.graph.DeclarativeTreeLayout;
import prefuse.data.Edge;
import prefuse.data.Node;

/**
 * <p>This is intended to be a lightweight means of defining a Tree across an existing Graph.</p>
 *
 * <p>Implementors can define arbitrary tree structures within their Graphs. SpanningTree implements this interface, and
 * other interfaces can easily be defined.</p>
 *
 * <p>This interface could be used in user-defined layouts.</p>
 *
 * @author Anton Marsden
 * @param <N>
 * @param <E>
 * @see SpanningTree
 * @see NodeBasedDeclarativeTree
 * @see DeclarativeTreeLayout
 */
public interface DeclarativeTree<N extends Node<?,?>, E extends Edge<?,?>> {

    /**
     * Get the root node.
     * @return the root Node
     */
	N getRoot();

    /**
     * Get all the children of the parent node.
     * @param n the parent node
     * @return an list of the child nodes of the parent node
     */
	List<N> children(N parent);

    /**
     * Get all the edges connecting a child to the parent node.
     * @param n the parent node
     * @return an list of the child edge nodes of the parent node
     */
	List<E> childEdges(N n);

    /**
     * Get the depth of the given node in the tree.
     * @param n a node in the tree
     * @return the depth of the node in tree. The root node
     * is at a depth level of 0, with each child at a greater
     * depth level. -1 is returned if the input node id is not
     * in the tree.
     */
	int getDepth(N n);

	/**
     * Get a node's parent node
     * @param n the child node
     * @return the parent node, or null if there is no parent
     */
	N getParent(N child);

    /**
     * Get the edge to the given node's parent.
     * @param n a Node instance
     * @return the parent Edge connecting the given node to its parent
     */
	E getParentEdge(N child);

    /**
     * Get the previous sibling of the given node.
     * @param node a node
     * @return the previous sibling, or null if there is no previous sibling
     */
	N getPreviousSibling(N node);

    /**
     * Get the next sibling of the given node.
     * @param node a node
     * @return the next sibling, or null if there is no next sibling
     */
	N getNextSibling(N node);

    /**
     * Get the number of nodes in this graph.
     * @return the number of nodes
     */
	int getNodeCount();

}
