package prefuse.data.tree;

import java.util.Iterator;
import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Node;

public class NodeBasedDeclarativeTree <N extends Node<N,E>, E extends Edge<N,E>> extends AbstractDeclarativeTree<N,E> {

	public NodeBasedDeclarativeTree(N root) {
		super(root);
	}

	public NodeBasedDeclarativeTree() {
	}

	public List<N> children(N parent) {
		return parent.children();
	}

	public N getParent(N child) {
		if(child == getRoot()) {
			return null;
		}
		Iterator<N> ni = child.inNeighbors().iterator();
		return (ni.hasNext() ? ni.next() : null);
	}

	public E getParentEdge(N child) {
		if(child == getRoot()) {
			return null;
		}
		Iterator<E> ne = child.inEdges().iterator();
		return (ne.hasNext() ? ne.next() : null);
	}

	public List<E> childEdges(N n) {
		return n.outEdges();
	}

}
