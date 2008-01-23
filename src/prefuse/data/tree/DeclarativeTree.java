package prefuse.data.tree;

import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Node;

public interface DeclarativeTree<N extends Node<?,?>, E extends Edge<?,?>> {

	N getRoot();

	List<N> children(N parent);

	List<E> childEdges(N n);

	int getDepth(N n);

	N getParent(N child);

	E getParentEdge(N child);

	N getPreviousSibling(N node);

	N getNextSibling(N node);

	int getNodeCount();

}
