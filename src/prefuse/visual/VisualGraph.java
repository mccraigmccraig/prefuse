package prefuse.visual;

import java.util.List;

import prefuse.Visualization;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.event.EventConstants;

/**
 * A visual abstraction of a graph data structure. NodeItem and EdgeItem tuples
 * provide the visual representations for the nodes and edges of the graph.
 * VisualGraphs should not be created directly, they are created automatically
 * by adding data to a Visualization, for example by using the
 * {@link Visualization#addGraph(String, Graph)} method.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class VisualGraph <T extends VisualItem<?>, N extends NodeItem<N,E>, E extends EdgeItem<N,E>> extends Graph<T,N,E> implements VisualTupleSet<T> {

    private Visualization m_vis;
    private String m_group;

    /**
     * Create a new VisualGraph
     * @param nodes the visual node table
     * @param edges the visual edge table
     * @param directed indicates if graph edges are directed or undirected
     * @param nodeKey the node table field by which to index the nodes.
     * This value can be null, indicating that just the row indices should be
     * used.
     * @param sourceKey the edge table field storing source node keys
     * @param targetKey the edge table field storing target node keys
     */
    public VisualGraph(VisualTable<? extends Node<?,?>, N> nodes, VisualTable<? extends Edge<?,?>, E> edges, boolean directed,
            String nodeKey, String sourceKey, String targetKey)
    {
        super(nodes, edges, directed, nodeKey, sourceKey, targetKey);
    }

    /**
     * Fire a graph event. Makes sure to invalidate all edges connected
     * to a node that has been updated.
     * @see prefuse.data.Graph#fireGraphEvent(prefuse.data.Table, int, int, int, int)
     */
    @Override
	protected void fireGraphEvent(Table<?> t,
            int first, int last, int col, int type)
    {
        // if a node is invalidated, invalidate the edges, too
        if ( type==EventConstants.UPDATE &&
             col==VisualItem.IDX_VALIDATED && t==getNodeTable() )
        {
            VisualTable<?,?> nodes = (VisualTable<?,?>)t;
            VisualTable<?,?> edges = (VisualTable<?,?>)getEdgeTable();

            for ( int i=first; i<=last; ++i ) {
                if ( nodes.isValidated(i) ) {
					continue; // look only for invalidations
				}

                if ( i < 0 ) {
                    System.err.println("catch me - VisualGraph fireGraphEvent");
                }
//                try {
                List<Integer> erows = edgeRows(i);
                for ( int erow : erows ) {
                    edges.setValidated(erow, false);
                }
//                } catch ( Exception ex ) {
//                  ex.printStackTrace();
//                }
            }
        }
        // fire the event off to listeners
        super.fireGraphEvent(t, first, last, col, type);
    }

    /**
     * Get the node row index value for the given key.
     * TODO: test this more thoroughly?
     */
    public int getNodeIndex(int key) {
        if ( m_nkey == null ) {
            return ((VisualTable<?,?>)getNodeTable()).getChildRow(key);
        } else {
            return super.getNodeIndex(key);
        }
    }

    // ------------------------------------------------------------------------
    // VisualGraph Methods

    /**
     * @see prefuse.visual.VisualTupleSet#getVisualization()
     */
    public Visualization getVisualization() {
        return m_vis;
    }

    /**
     * Set the visualization associated with this VisualGraph
     * @param vis the visualization to set
     */
    public void setVisualization(Visualization vis) {
        m_vis = vis;
    }

    /**
     * Get the visualization data group name for this graph
     * @return the data group name
     */
    public String getGroup() {
        return m_group;
    }

    /**
     * Set the visualization data group name for this graph
     */
    public void setGroup(String group) {
        m_group = group;
    }

} // end of class VisualGraph
