package prefuse.data.tree;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.expression.Predicate;
import prefuse.data.util.FilteredList;

/**
 * Allows a tree subset to be defined.
 *
 * @author Anton Marsden
 *
 * @param <N>
 * @param <E>
 */
public class FilteredDeclarativeTree <N extends Node<N,E>, E extends Edge<N,E>> extends AbstractDeclarativeTree<N,E> {

	protected Predicate nodeFilter;
	protected boolean reverse;

	public FilteredDeclarativeTree(N root, Predicate nodeFilter) {
		super(root);
		this.nodeFilter = nodeFilter;
	}

	public FilteredDeclarativeTree(N root, Predicate nodeFilter, boolean reverse) {
		super(root);
		this.nodeFilter = nodeFilter;
		this.reverse = reverse;
	}

	public List<N> children(N parent) {
		return new FilteredList<N>(reverse ? parent.inNeighbors() : parent.outNeighbors(), nodeFilter);
	}

	public N getParent(N child) {
		if(child == getRoot()) {
			return null;
		}
		Iterator<N> ni = (reverse ? child.outNeighbors() : child.inNeighbors()).iterator();
		return (ni.hasNext() ? ni.next() : null);
	}

	public E getParentEdge(N child) {
		if(child == getRoot()) {
			return null;
		}
		Iterator<E> ne = (reverse ? child.outEdges() : child.inEdges()).iterator();
		return (ne.hasNext() ? ne.next() : null);
	}

	public List<E> childEdges(final N n) {
		return new AbstractSequentialList<E>() {
			private final List<N> children = children(n);
			@Override
			public ListIterator<E> listIterator(final int index) {
				return new ListIterator<E>() {

					private final ListIterator<N> nodeIter = children.listIterator(index);

					public void add(E e) {
						throw new UnsupportedOperationException();
					}

					public boolean hasNext() {
						return nodeIter.hasNext();
					}

					public boolean hasPrevious() {
						return nodeIter.hasPrevious();
					}

					public E next() {
						N n = nodeIter.next();
						return (reverse ? n.outEdges() : n.inEdges()).iterator().next();
					}

					public int nextIndex() {
						return nodeIter.nextIndex();
					}

					public E previous() {
						N p = nodeIter.previous();
						return (reverse ? p.outEdges() : p.inEdges()).iterator().next();
					}

					public int previousIndex() {
						return nodeIter.previousIndex();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}

					public void set(E e) {
						throw new UnsupportedOperationException();
					}

				};
			}
			@Override
			public int size() {
				return children.size();
			}

		};
	}

}
