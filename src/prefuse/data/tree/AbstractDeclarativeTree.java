package prefuse.data.tree;

import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Node;

/**
 * This is a base implementation for a declarative tree. Subclasses should override some of the more inefficient
 * operations where possible.
 *
 * @author Anton Marsden
 *
 * @param <N>
 * @param <E>
 *
 */
public abstract class AbstractDeclarativeTree <N extends Node<N,E>, E extends Edge<N,E>> implements DeclarativeTree<N, E> {

	private N root;

	public AbstractDeclarativeTree() {
	}

	public AbstractDeclarativeTree(N root) {
		this.root = root;
	}

	public int getNodeCount() {
		return getNodeCountFrom(root);
	}

	protected int getNodeCountFrom(N n) {
		if(n == null) {
			return 0;
		}
		int count = 1;
		for(N c : children(n)) {
			count += getNodeCountFrom(c);
		}
		return count;
	}

	public N getRoot() {
		return root;
	}

	protected void setRoot(N root) {
		this.root = root;
	}

	public N getNextSibling(N node) {
		N parent = getParent(node);
		if(parent == null) {
			return null;
		}
		List<N> children = children(parent);
		int idx = children.indexOf(node);
		if(idx < 0 || idx + 1 >= children.size()) {
			return null;
		}
		return children.get(idx + 1);
	}

	public N getPreviousSibling(N node) {
		N parent = getParent(node);
		if(parent == null) {
			return null;
		}
		List<N> children = children(parent);
		int idx = children.indexOf(node);
		if(idx <= 0 || idx >= children.size()) {
			return null;
		}
		return children.get(idx - 1);
	}

    /**
     * Get the depth of the given node in the tree.
     * @param n a node in the tree
     * @return the depth of the node in tree. The root node
     * is at a depth level of 0, with each child at a greater
     * depth level. -1 is returned if the input node id is not
     * in the tree.
     */
    public int getDepth(N n) {
        int depth = 0;
        while(n != null && n != root) {
        	n = getParent(n);
        	depth++;
        }
        return n == null ? -1 : depth;
    }

}

