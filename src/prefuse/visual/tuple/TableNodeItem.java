package prefuse.visual.tuple;

import java.util.List;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.visual.NodeItem;

/**
 * NodeItem implementation that used data values from a backing
 * VisualTable of nodes.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TableNodeItem extends TableVisualItem<TableNodeItem> implements NodeItem<TableNodeItem,TableEdgeItem> {

    protected Graph<?,TableNodeItem,TableEdgeItem> m_graph;

    /**
     * Initialize a new TableNodeItem for the given graph, table, and row.
     * This method is used by the appropriate TupleManager instance, and
     * should not be called directly by client code, unless by a
     * client-supplied custom TupleManager.
     * @param table the backing VisualTable
     * @param graph the backing VisualGraph
     * @param row the row in the node table to which this Node instance
     *  corresponds.
     */
    @Override
	public void init(Table table, Graph graph, int row) {
        m_table = table;
        m_graph = graph;
        m_row = m_table.isValidRow(row) ? row : -1;
    }

    /**
     *
     */
    public Graph<?,TableNodeItem,TableEdgeItem> getGraph() {
        return m_graph;
    }

    // ------------------------------------------------------------------------
    // If only we had multiple inheritance or categories....
    // Instead we must re-implement the entire Node interface.

    /**
     * @see prefuse.data.Node#getInDegree()
     */
    public int getInDegree() {
        return m_graph.getInDegree(this);
    }

    /**
     * @see prefuse.data.Node#getOutDegree()
     */
    public int getOutDegree() {
        return m_graph.getOutDegree(this);
    }

    /**
     * @see prefuse.data.Node#getDegree()
     */
    public int getDegree() {
        return m_graph.getDegree(this);
    }

    /**
     * @see prefuse.data.Node#inEdges()
     */
    public List<TableEdgeItem> inEdges() {
        return m_graph.inEdges(this);
    }

    /**
     * @see prefuse.data.Node#outEdges()
     */
    public List<TableEdgeItem> outEdges() {
        return m_graph.outEdges(this);
    }

    /**
     * @see prefuse.data.Node#edges()
     */
    public List<TableEdgeItem> edges() {
        return m_graph.edges(this);
    }

    /**
     * @see prefuse.data.Node#inNeighbors()
     */
    public List<TableNodeItem> inNeighbors() {
        return m_graph.inNeighbors(this);
    }

    /**
     * @see prefuse.data.Node#outNeighbors()
     */
    public List<TableNodeItem> outNeighbors() {
        return m_graph.outNeighbors(this);
    }

    /**
     * @see prefuse.data.Node#neighbors()
     */
    public List<TableNodeItem> neighbors() {
        return m_graph.neighbors(this);
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.data.Node#getParent()
     */
    public TableNodeItem getParent() {
        return m_graph.getSpanningTree().getParent(this);
    }

    /**
     * @see prefuse.data.Node#getParentEdge()
     */
    public TableEdgeItem getParentEdge() {
        return m_graph.getSpanningTree().getParentEdge(this);
    }

    /**
     * @see prefuse.data.Node#getPreviousSibling()
     */
    public TableNodeItem getPreviousSibling() {
        return m_graph.getSpanningTree().getPreviousSibling(this);
    }

    /**
     * @see prefuse.data.Node#getNextSibling()
     */
    public TableNodeItem getNextSibling() {
        return m_graph.getSpanningTree().getNextSibling(this);
    }

    /**
     * @see prefuse.data.Node#children()
     */
    public List<TableNodeItem> children() {
        return m_graph.getSpanningTree().children(this);
    }

    /**
     * @see prefuse.data.Node#childEdges()
     */
    public List<TableEdgeItem> childEdges() {
        return m_graph.getSpanningTree().childEdges(this);
    }

    /**
     * @see prefuse.data.Node#getDepth()
     */
    public int getDepth() {
        return m_graph.getSpanningTree().getDepth(this);
    }

} // end of class TableNodeItem
