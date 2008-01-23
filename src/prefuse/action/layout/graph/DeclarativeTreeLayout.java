package prefuse.action.layout.graph;

import prefuse.action.layout.Layout;
import prefuse.data.Graph;
import prefuse.data.tree.DeclarativeTree;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Abstract base class providing convenience methods for declarative tree layout algorithms.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public abstract class DeclarativeTreeLayout extends Layout {

    private DeclarativeTree<NodeItem<?,?>,EdgeItem<?,?>> tree;

    /**
     * Create a new DeclarativeTreeLayout.
     */
    public DeclarativeTreeLayout() {
        super();
    }

    /**
     * Create a new DeclarativeTreeLayout.
     */
    public DeclarativeTreeLayout(String group) {
        super(group);
    }

    // ------------------------------------------------------------------------

    public DeclarativeTree<NodeItem<?, ?>, EdgeItem<?, ?>> getTree() {
    	if(tree != null) {
    		return tree;
    	}
        TupleSet<? extends VisualItem<?>> ts = m_vis.getGroup(m_group);
        if ( ts instanceof Graph ) {
        	DeclarativeTree<NodeItem<?, ?>, EdgeItem<?, ?>> tree = (DeclarativeTree<NodeItem<?, ?>, EdgeItem<?, ?>>) (Object) ((Graph<?, ?, ?>)ts).getSpanningTree();
            return tree;
        } else {
            throw new IllegalStateException("This action's data group does " +
                    "not resolve to a Graph instance.");
        }
	}

	public void setTree(DeclarativeTree<NodeItem<?, ?>, EdgeItem<?, ?>> tree) {
		this.tree = tree;
	}

}
