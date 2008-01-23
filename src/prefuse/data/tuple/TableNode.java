package prefuse.data.tuple;

import java.util.List;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;

/**
 * Node implementation that reads Node data from a backing node table.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TableNode extends TableTuple<TableNode> implements Node<TableNode, TableEdge> {

    /**
     * The backing graph.
     */
    protected Graph<TableTuple<?>,TableNode,TableEdge> m_graph;

    /**
     * Initialize a new Node backed by a node table. This method is used by
     * the appropriate TupleManager instance, and should not be called
     * directly by client code, unless by a client-supplied custom
     * TupleManager.
     * @param table the node Table
     * @param graph the backing Graph
     * @param row the row in the node table to which this Node instance
     *  corresponds.
     */
    @Override
	public void init(Table<?> table, Graph<?,?,?> graph, int row) {
        m_table = (Table<TableNode>) table;
        m_graph = (Graph<TableTuple<?>,TableNode,TableEdge>) (Object) graph;
        m_row = m_table.isValidRow(row) ? row : -1;
    }

    /**
     *
     */
    public Graph<?,TableNode,TableEdge> getGraph() {
        return m_graph;
    }

    // ------------------------------------------------------------------------
    // Graph Methods

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
    public List<TableEdge> inEdges() {
        return m_graph.inEdges(this);
    }

    /**
     * @see prefuse.data.Node#outEdges()
     */
    public List<TableEdge> outEdges() {
        return m_graph.outEdges(this);
    }

    /**
     * @see prefuse.data.Node#edges()
     */
    public List<TableEdge> edges() {
        return m_graph.edges(this);
    }

    /**
     * @see prefuse.data.Node#inNeighbors()
     */
    public List<TableNode> inNeighbors() {
        return m_graph.inNeighbors(this);
    }

    /**
     * @see prefuse.data.Node#outNeighbors()
     */
    public List<TableNode> outNeighbors() {
        return m_graph.outNeighbors(this);
    }

    /**
     * @see prefuse.data.Node#neighbors()
     */
    public List<TableNode> neighbors() {
        return m_graph.neighbors(this);
    }


    // ------------------------------------------------------------------------
    // Tree Methods

    /**
     * @see prefuse.data.Node#getParent()
     */
    public TableNode getParent() {
        return m_graph.getSpanningTree().getParent(this);
    }

    /**
     * @see prefuse.data.Node#getParentEdge()
     */
    public TableEdge getParentEdge() {
        return m_graph.getSpanningTree().getParentEdge(this);
    }

    /**
     * @see prefuse.data.Node#getPreviousSibling()
     */
    public TableNode getPreviousSibling() {
        return m_graph.getSpanningTree().getPreviousSibling(this);
    }

    /**
     * @see prefuse.data.Node#getNextSibling()
     */
    public TableNode getNextSibling() {
        return m_graph.getSpanningTree().getNextSibling(this);
    }

    /**
     * @see prefuse.data.Node#children()
     */
    public List<TableNode> children() {
        return m_graph.getSpanningTree().children(this);
    }

    /**
     * @see prefuse.data.Node#childEdges()
     */
    public List<TableEdge> childEdges() {
        return m_graph.getSpanningTree().childEdges(this);
    }

    /**
     * @see prefuse.data.Node#getDepth()
     */
    public int getDepth() {
        return m_graph.getSpanningTree().getDepth(this);
    }

} // end of class TableNode
