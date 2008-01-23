package test.prefuse.data;

import junit.framework.TestCase;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.tuple.TableEdge;
import prefuse.data.tuple.TableNode;
import prefuse.util.GraphLib;
import test.prefuse.TestConfig;

public class GraphTest extends TestCase implements GraphTestData {

    public static Graph<Tuple<?>,TableNode,TableEdge> getTestCaseGraph() {
        Table<TableNode> nodes = new Table<TableNode>(NNODES, NNODECOLS) {
			@Override
			public TableNode createTupleInstance() {
				return new TableNode();
			}};
        for ( int c=0; c<NNODECOLS; ++c ) {
            nodes.addColumn(NHEADERS[c], NTYPES[c]);
            for ( int r=0; r<NNODES; ++r ) {
                nodes.set(r, NHEADERS[c], NODES[c][r]);
            }
        }

        Table<TableEdge> edges = new Table<TableEdge>(NEDGES, NEDGECOLS) {
			@Override
			public TableEdge createTupleInstance() {
				return new TableEdge();
			}};

		for ( int c=0; c<NEDGECOLS; ++c ) {
            edges.addColumn(EHEADERS[c], ETYPES[c]);
            for ( int r=0; r<NEDGES; ++r ) {
                edges.set(r, EHEADERS[c], EDGES[c][r]);
            }
        }

        return new Graph<Tuple<?>,TableNode,TableEdge>(nodes, edges, false,
                NHEADERS[0], EHEADERS[0], EHEADERS[1]);
    }

    private Graph<Tuple<?>,TableNode, TableEdge> graph;

    @Override
	protected void setUp() throws Exception {
        super.setUp();
        graph = getTestCaseGraph();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
	protected void tearDown() throws Exception {
        super.tearDown();
        graph = null;
    }

    public void testGraph() {
        boolean verbose = TestConfig.verbose();

        Table<?> nodes = graph.getNodeTable();
        Table<?> edges = graph.getEdgeTable();

        // check the basics
        assertEquals(NNODES, graph.getNodeCount());
        assertEquals(NEDGES, graph.getEdgeCount());
        assertEquals(NHEADERS[0], graph.getNodeKeyField());
        assertEquals(EHEADERS[0], graph.getEdgeSourceField());
        assertEquals(EHEADERS[1], graph.getEdgeTargetField());

        // check all nodes, basic data
        for(Node<?,?> node :  graph.nodes()) {
            int nrow = node.getRow();

            if ( verbose ) {
				System.out.print(nrow+"\t");
			}

            // check data members
            for ( int i=0; i<NNODECOLS; ++i ) {
                assertEquals(NODES[i][nrow], node.get(NHEADERS[i]));
                assertEquals(NODES[i][nrow], nodes.get(nrow, NHEADERS[i]));
                if ( verbose ) {
					System.out.print(NHEADERS[i]+":"+NODES[i][nrow]+"\t");
				}
            }

            if ( verbose ) {
                System.out.print("in:"+node.getInDegree());
                System.out.print("\t");
                System.out.print("out:"+node.getOutDegree());
                System.out.println();
            }

            // check degrees
            assertEquals(node.getInDegree(),      INDEGREE[nrow]);
            assertEquals(graph.getInDegree(nrow),  INDEGREE[nrow]);
            assertEquals(node.getOutDegree(),     OUTDEGREE[nrow]);
            assertEquals(graph.getOutDegree(nrow), OUTDEGREE[nrow]);

            // check edges
            for(Edge<?,?> edge : node.inEdges()) {
                int erow = edge.getRow();
                assertEquals(nrow, edge.getTargetNode().getRow());
                assertEquals(nrow, graph.getTargetNode(erow));
            }
            for(Edge<?,?> edge : node.outEdges()) {
                int erow = edge.getRow();
                assertEquals(nrow, edge.getSourceNode().getRow());
                assertEquals(nrow, graph.getSourceNode(erow));
            }
        }

        // check all edges, basic data
        for(Edge edge :  graph.edges()) {
            int erow = edge.getRow();

            // check data members
            for ( int i=0; i<NEDGECOLS; ++i ) {
                assertEquals(EDGES[i][erow], edge.get(EHEADERS[i]));
                assertEquals(EDGES[i][erow], edges.get(erow, EHEADERS[i]));
            }

            // check nodes
            Node<?,?> s = edge.getSourceNode();
            int srow = s.getRow();
            assertEquals(srow, graph.getSourceNode(erow));
            int sk = nodes.getInt(srow, NHEADERS[0]);
            assertEquals(sk, edges.getInt(erow, EHEADERS[0]));

            Node<?,?> t = edge.getTargetNode();
            int trow = t.getRow();
            assertEquals(trow, graph.getTargetNode(erow));
            int tk = nodes.getInt(trow, NHEADERS[0]);
            assertEquals(tk, edges.getInt(erow, EHEADERS[1]));

            assertEquals(srow, edge.getAdjacentNode(t).getRow());
            assertEquals(trow, edge.getAdjacentNode(s).getRow());
            assertEquals(srow, graph.getAdjacentNode(erow, trow));
            assertEquals(trow, graph.getAdjacentNode(erow, srow));
        }
    }

    public void testRemoveNode() {
        int cliqueSize = 5;
        Graph<?,?,?> g = GraphLib.getClique(cliqueSize);
        Edge[] edges = new Edge[4];

        Node<?,?> rem = g.nodes().iterator().next();
        int i = 0;
        for (Edge<?,?> e : rem.edges()) {
            edges[i++] = e;
        }

        assertEquals(true, g.removeNode(rem));
        assertEquals(false, rem.isValid());

        for(Node<?,?> node : g.nodes()) {
            assertEquals(cliqueSize-2, node.getDegree());
        }

        for ( i=0; i<edges.length; ++i ) {
            assertEquals(false, edges[i].isValid());
        }
    }
}
