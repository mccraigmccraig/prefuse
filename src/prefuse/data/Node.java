package prefuse.data;

import java.util.List;


/**
 * Tuple sub-interface that represents a node in a graph or tree structure.
 * This interface supports both graph and tree methods, tree methods invoked
 * on a node in a general graph typically default to operations on the
 * graph's generated spanning tree.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public interface Node<N extends Node<N,E>,E extends Edge<N,E>> extends Tuple<N> {

    // ------------------------------------------------------------------------
    // Graph Methods

    /**
     * Get the in-degree of the node, the number of edges for which this node
     * is the target.
     * @return the in-degree of the node
     */
    public int getInDegree();

    /**
     * Get the out-degree of the node, the number of edges for which this node
     * is the source.
     * @return the out-degree of the node
     */
    public int getOutDegree();

    /**
     * Get the degree of the node, the number of edges for which this node
     * is either the source or the target.
     * @return the total degree of the node
     */
    public int getDegree();

    /**
     * Get an iterator over all incoming edges, those for which this node
     * is the target.
     * @return an Iterator over all incoming edges
     */
    public List<E> inEdges();

    /**
     * Get an iterator over all outgoing edges, those for which this node
     * is the source.
     * @return an Iterator over all outgoing edges
     */
    public List<E> outEdges();

    /**
     * Get an iterator over all incident edges, those for which this node
     * is either the source or the target.
     * @return an Iterator over all incident edges
     */
    public List<E> edges();

    /**
     * Get an iterator over all adjacent nodes connected to this node by an
     * incoming edge (i.e., all nodes that "point" at this one).
     * @return an Iterator over all neighbors with in-links on this node
     */
    public List<N> inNeighbors();

    /**
     * Get an iterator over all adjacent nodes connected to this node by an
     * outgoing edge (i.e., all nodes "pointed" to by this one).
     * @return an Iterator over all neighbors with out-links from this node
     */
    public List<N> outNeighbors();

    /**
     * Get an iterator over all nodes connected to this node.
     * @return an Iterator over all neighbors of this node
     */
    public List<N> neighbors();

    // ------------------------------------------------------------------------
    // Tree Methods

    /**
     * Get the parent node of this node in a tree structure.
     * @return this node's parent node, or null if there is none.
     */
    public N getParent();

    /**
     * Get the edge between this node and its parent node in a tree
     * structure.
     * @return the edge between this node and its parent
     */
    public E getParentEdge();

    /**
     * Get the tree depth of this node.
     * @return the tree depth of this node. The root's tree depth is
     * zero, and each level of the tree is one depth level greater.
     */
    public int getDepth();

    /**
     * Get this node's previous tree sibling.
     * @return the previous sibling, or null if none
     */
    public N getPreviousSibling();

    /**
     * Get this node's next tree sibling.
     * @return the next sibling, or null if none
     */
    public N getNextSibling();

    /**
     * Get an iterator over this node's tree children.
     * @return an iterator over this node's children
     */
    public List<N> children();

    /**
     * Get an iterator over the edges from this node to its tree children.
     * @return an iterator over the edges to the child nodes
     */
    public List<E> childEdges();

} // end of interface Node
