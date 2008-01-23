/**
 * Copyright (c) 2004-2006 Regents of the University of California.
 * See "license-prefuse.txt" for licensing terms.
 */
package prefuse.data.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.util.io.XMLWriter;

/**
 * GraphWriter instance that writes a graph file formatted using the
 * GraphML file format. GraphML is an XML format supporting graph
 * structure and typed data schemas for both nodes and edges. For more
 * information about the format, please see the
 * <a href="http://graphml.graphdrawing.org/">GraphML home page</a>.
 *
 * <p>The GraphML spec only supports the data types <code>int</code>,
 * <code>long</code>, <code>float</code>, <code>double</code>,
 * <code>boolean</code>, and <code>string</code>. An exception will
 * be thrown if a data type outside these allowed types is
 * encountered.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class GraphMLWriter extends AbstractGraphWriter {

    /**
     * String tokens used in the GraphML format.
     */
    public interface Tokens extends GraphMLReader.Tokens  {
        public static final String GRAPHML = "graphml";

        public static final String GRAPHML_HEADER =
            "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
            +"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            +"  xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n"
            +"  http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n\n";
    }

    /**
     * Map containing legal data types and their names in the GraphML spec
     */
    private static final Map<Class<?>, String> TYPES = new HashMap<Class<?>, String>();
    static {
        TYPES.put(int.class, prefuse.data.io.GraphMLReader.Tokens.INT);
        TYPES.put(long.class, prefuse.data.io.GraphMLReader.Tokens.LONG);
        TYPES.put(float.class, prefuse.data.io.GraphMLReader.Tokens.FLOAT);
        TYPES.put(double.class, prefuse.data.io.GraphMLReader.Tokens.DOUBLE);
        TYPES.put(boolean.class, prefuse.data.io.GraphMLReader.Tokens.BOOLEAN);
        TYPES.put(String.class, prefuse.data.io.GraphMLReader.Tokens.STRING);
    }

    /**
     * @see prefuse.data.io.GraphWriter#writeGraph(prefuse.data.Graph, java.io.OutputStream)
     */
    public void writeGraph(Graph<?,?,?> graph, OutputStream os) throws DataIOException
    {
        // first, check the schemas to ensure GraphML compatibility
        Schema ns = graph.getNodeTable().getSchema();
        Schema es = graph.getEdgeTable().getSchema();
        checkGraphMLSchema(ns);
        checkGraphMLSchema(es);

        XMLWriter xml = new XMLWriter(new PrintWriter(os));
        xml.begin(Tokens.GRAPHML_HEADER, 2);

        xml.comment("prefuse GraphML Writer | "
                + new Date(System.currentTimeMillis()));

        // print the graph schema
        printSchema(xml, prefuse.data.io.GraphMLReader.Tokens.NODE, ns, null);
        printSchema(xml, prefuse.data.io.GraphMLReader.Tokens.EDGE, es, new String[] {
            graph.getEdgeSourceField(), graph.getEdgeTargetField()
        });
        xml.println();

        // print graph contents
        xml.start(prefuse.data.io.GraphMLReader.Tokens.GRAPH, prefuse.data.io.GraphMLReader.Tokens.EDGEDEF,
            graph.isDirected() ? prefuse.data.io.GraphMLReader.Tokens.DIRECTED : prefuse.data.io.GraphMLReader.Tokens.UNDIRECTED);

        // print the nodes
        xml.comment("nodes");
        for (Node<?,?> n : graph.nodes()) {

            if ( ns.getColumnCount() > 0 ) {
                xml.start(prefuse.data.io.GraphMLReader.Tokens.NODE, prefuse.data.io.GraphMLReader.Tokens.ID, String.valueOf(n.getRow()));
                for ( int i=0; i<ns.getColumnCount(); ++i ) {
                    String field = ns.getColumnName(i);
                    xml.contentTag(prefuse.data.io.GraphMLReader.Tokens.DATA, prefuse.data.io.GraphMLReader.Tokens.KEY, field,
                                   n.getString(field));
                }
                xml.end();
            } else {
                xml.tag(prefuse.data.io.GraphMLReader.Tokens.NODE, prefuse.data.io.GraphMLReader.Tokens.ID, String.valueOf(n.getRow()));
            }
        }

        // add a blank line
        xml.println();

        // print the edges
        String[] attr = new String[]{prefuse.data.io.GraphMLReader.Tokens.ID, prefuse.data.io.GraphMLReader.Tokens.SOURCE, prefuse.data.io.GraphMLReader.Tokens.TARGET};
        String[] vals = new String[3];

        xml.comment("edges");
        for (Edge<?,?> e : graph.edges()) {
            vals[0] = String.valueOf(e.getRow());
            vals[1] = String.valueOf(e.getSourceNode().getRow());
            vals[2] = String.valueOf(e.getTargetNode().getRow());

            if ( es.getColumnCount() > 2 ) {
                xml.start(prefuse.data.io.GraphMLReader.Tokens.EDGE, attr, vals, 3);
                for ( int i=0; i<es.getColumnCount(); ++i ) {
                    String field = es.getColumnName(i);
                    if ( field.equals(graph.getEdgeSourceField()) ||
                         field.equals(graph.getEdgeTargetField()) ) {
						continue;
					}

                    xml.contentTag(prefuse.data.io.GraphMLReader.Tokens.DATA, prefuse.data.io.GraphMLReader.Tokens.KEY, field,
                                   e.getString(field));
                }
                xml.end();
            } else {
                xml.tag(prefuse.data.io.GraphMLReader.Tokens.EDGE, attr, vals, 3);
            }
        }
        xml.end();

        // finish writing file
        xml.finish("</"+Tokens.GRAPHML+">\n");
    }

    /**
     * Print a table schema to a GraphML file
     * @param xml the XMLWriter to write to
     * @param group the data group (node or edge) for the schema
     * @param s the schema
     */
    private void printSchema(XMLWriter xml, String group, Schema s,
                             String[] ignore)
    {
        String[] attr = new String[] {prefuse.data.io.GraphMLReader.Tokens.ID, prefuse.data.io.GraphMLReader.Tokens.FOR,
                prefuse.data.io.GraphMLReader.Tokens.ATTRNAME, prefuse.data.io.GraphMLReader.Tokens.ATTRTYPE };
        String[] vals = new String[4];

OUTER:
        for ( int i=0; i<s.getColumnCount(); ++i ) {
            vals[0] = s.getColumnName(i);

            for ( int j=0; ignore!=null && j<ignore.length; ++j ) {
                if ( vals[0].equals(ignore[j]) ) {
					continue OUTER;
				}
            }

            vals[1] = group;
            vals[2] = vals[0];
            vals[3] = TYPES.get(s.getColumnType(i));
            Object dflt = s.getDefault(i);

            if ( dflt == null ) {
                xml.tag(prefuse.data.io.GraphMLReader.Tokens.KEY, attr, vals, 4);
            } else {
                xml.start(prefuse.data.io.GraphMLReader.Tokens.KEY, attr, vals, 4);
                xml.contentTag(prefuse.data.io.GraphMLReader.Tokens.DEFAULT, dflt.toString());
                xml.end();
            }
        }
    }

    /**
     * Checks if all Schema types are compatible with the GraphML specification.
     * The GraphML spec only allows the types <code>int</code>,
     * <code>long</code>, <code>float</code>, <code>double</code>,
     * <code>boolean</code>, and <code>string</code>.
     * @param s the Schema to check
     */
    private void checkGraphMLSchema(Schema s) throws DataIOException {
        for ( int i=0; i<s.getColumnCount(); ++i ) {
            Class<?> type = s.getColumnType(i);
            if ( TYPES.get(type) == null ) {
                throw new DataIOException("Data type unsupported by the "
                    + "GraphML format: " + type.getName());
            }
        }
    }

} // end of class GraphMLWriter