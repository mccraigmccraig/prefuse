/**
 * Copyright (c) 2004-2006 Regents of the University of California.
 * See "license-prefuse.txt" for licensing terms.
 */
package prefuse.data.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Node;
import prefuse.data.tree.DeclarativeTree;
import prefuse.data.tree.NodeBasedDeclarativeTree;

/**
 * A depth-first iterator over the subtree rooted at given node.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TreeNodeIterator<N extends Node<N,?>> implements Iterator<N> {

	public static enum TraversalMode {
		PRE_ORDER,
		/* TODO: add support for in-order traversal */
		POST_ORDER
	}

	private List<N> m_stack;
	private N m_root;
	private final TraversalMode traversalMode;

	private final DeclarativeTree<N,?> tree;

	/**
	 * Create a new TreeNodeIterator over the given subtree.
	 *
	 * @param root
	 *            the root of the subtree to traverse
	 */
	public TreeNodeIterator(N root) {
		this(new NodeBasedDeclarativeTree(root), TraversalMode.PRE_ORDER);
	}

	/**
	 * Create a new TreeNodeIterator over the given subtree.
	 *
	 * @param root
	 *            the root of the subtree to traverse
	 * @param traversal mode
	 * 			  the TraversalMode to use
	 */
	public TreeNodeIterator(N root, TraversalMode traversalMode) {
		this(new NodeBasedDeclarativeTree(root), traversalMode);
	}

	/**
	 * Create a new TreeNodeIterator over the given subtree.
	 *
	 * @param root
	 *            the root of the subtree to traverse
	 */
	public TreeNodeIterator(DeclarativeTree<N,?> tree) {
		this(tree, TraversalMode.PRE_ORDER);
	}

	/**
	 * Create a new TreeNodeIterator over the given subtree.
	 *
	 * @param tree
	 *            the tree to traverse
	 * @param traversal mode
	 * 			  the TraversalMode to use
	 */
	public TreeNodeIterator(DeclarativeTree<N,?> tree, TraversalMode traversalMode) {
		this.traversalMode = traversalMode;
		this.tree = tree;
		m_stack = new ArrayList<N>();
		N root = tree.getRoot();
		m_stack.add(root);

		if (traversalMode == TraversalMode.POST_ORDER) {
			List<N> children = (List<N>) tree.children(root);
			while (!children.isEmpty()) {
				N n = children.get(0);
				m_stack.add(n);
				children = (List<N>) tree.children(n);
			}
		}

	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return !m_stack.isEmpty();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public N next() {
		N c, x = null;
		switch (traversalMode) {
		case PRE_ORDER:
			x = m_stack.get(m_stack.size() - 1);
			List<N> xChildren = (List<N>) tree.children(x);
			if (!xChildren.isEmpty()) {
				c = xChildren.get(0);
				m_stack.add(c);
			} else if ((c = (N) tree.getNextSibling(x)) != null) {
				m_stack.set(m_stack.size() - 1, c);
			} else {
				m_stack.remove(m_stack.size() - 1);
				while (!m_stack.isEmpty()) {
					c = m_stack.remove(m_stack.size() - 1);
					if (c == m_root) {
						break;
					} else if ((c = (N) tree.getNextSibling(c)) != null) {
						m_stack.add(c);
						break;
					}
				}
			}
			break;
		case POST_ORDER:
			x = m_stack.remove(m_stack.size() - 1);
			if (x != m_root && (c = (N) tree.getNextSibling(x)) != null) {
				m_stack.add(c);
				List<N> cChildren = (List<N>) tree.children(c);
				while (!cChildren.isEmpty()) {
					c = cChildren.get(0);
					m_stack.add(c);
					cChildren = (List<N>) tree.children(c);
				}
			}
			break;
		}

		return x;
	}

	/**
	 * Throws an UnsupportedOperationException
	 *
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException("Remove not supported");
	}

} // end of class TreeNodeIterator
