package edu.berkeley.guir.prefuse.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Library of useful operations on graphs.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class GraphLib {

    public static Graph getStar(int n) {
        Node n0 = new TreeNode();
        n0.setAttribute("label","n"+0);
        ArrayList al = new ArrayList(n+1);
        al.add(n0);
        for ( int i=1; i <= n; i++ ) {
            Node nn = new TreeNode();
            nn.setAttribute("label","n"+i);
            Edge e = new Edge(n0, nn);
            n0.addEdge(e);
            nn.addEdge(e);
            al.add(nn);
        }
        return new SimpleGraph(al);
    } //
    
    public static Graph getClique(int n) {
        Node nodes[] = new Node[n];
        ArrayList al = new ArrayList(n);
        for ( int i = 0; i < n; i++ ) {
            nodes[i] = new TreeNode();
            nodes[i].setAttribute("label", "n"+i);
            al.add(nodes[i]);
        }
        for ( int i = 0; i < n; i++ ) {
            for ( int j = 0; j < n; j++ )
                if ( i != j ) nodes[i].addNeighbor(nodes[j]);
        }
        return new SimpleGraph(al);
    } //
    
	public static final int SEARCH_NODES = 0;
	public static final int SEARCH_EDGES = 1;
	public static final int SEARCH_ALL   = 2;

	/**
	 * Performs a simple search, returning all nodes that exactly match the provided attribute. 
	 * @param g the graph to search over
	 * @param attrName the attribute name to look up
	 * @param attrValue the attribute value to match
	 * @return a <code>List</code> of the matching <code>Node</code> objects
	 */
	public static List searchNodes(Graph g, String attrName, String attrValue) {
		return search(g, attrName, attrValue, SEARCH_NODES);
	} //
	
	public static void searchNodes(Graph g, Collection result, String query) {
		Iterator nodeIter = g.getNodes();
		while ( nodeIter.hasNext() ) {
			Node n = (Node)nodeIter.next();
			Iterator attIter = n.getAttributes().values().iterator();
			while ( attIter.hasNext() ) {
				String value = (String)attIter.next();
				if ( value.indexOf(query) > -1 ) {
					result.add(n);
					break;
				}
			}
		}
	} //

	/**
	 * Performs a simple search, returning all Entities that exactly match the provided attribute. 
	 * @param g the graph to search over
	 * @param attrName the attribute name to look up
	 * @param attrValue the attribute value to match
	 * @param type determines if nodes, edges, or both are searched. The legal values are
	 *  <code>SEARCH_NODES</code>, <code>SEARCH_EDGES</code>, <code>SEARCH_ALL</code>.
	 * @return a <code>List</code> of the matching <code>Entity</code> objects
	 */
	public static List search(Graph g, String attrName, String attrValue, int type) {
		ArrayList result = new ArrayList();
		if ( type == SEARCH_NODES || type == SEARCH_ALL ) {
			search(result, g.getNodes(), attrName, attrValue);
		}
		if ( type == SEARCH_EDGES || type == SEARCH_ALL ) {
			search(result, g.getEdges(), attrName, attrValue);
		}
		return result;
	} //
	
	private static void search(Collection result, Iterator iter, String attrName, String attrValue) {
		while ( iter.hasNext() ) {
			Entity e = (Entity)iter.next();
			String val = e.getAttribute(attrName);
			if ( val != null && val.equals(attrValue) )
				result.add(e);
		}
	} //
	
//	/**
//	 * Filters a graph of unwanted entities, creating a new subgraph.
//	 * @param og the original <code>Graph</code>. It will be not bechanged.
//	 * @param func a FilterFunction that determines what entities are filtered.
//	 * @return a new, filtered <code>Graph</code> instance.
//	 */
//	public static Graph getFilteredGraph(final Graph og, FilterFunction func)
//	{
//		SimpleGraph g = new SimpleGraph();
//		HashMap nodeMap = new HashMap();
//		Iterator iter = og.getNodes();
//		while ( iter.hasNext() ) {
//			Node u = (Node)iter.next();
//			if ( func.filter(u) ) {
//				Node v = null;
//				try {
//					v = (Node)u.getClass().newInstance();
//				} catch ( Exception e ) {
//					e.printStackTrace();
//				}
//				v.setAttributes(u.getAttributes());
//				nodeMap.put(u,v);
//				g.addNode(v);
//			}
//		}
//		iter = og.getEdges();
//		while ( iter.hasNext() ) {
//			Edge e = (Edge)iter.next();
//			Node v1 = (Node)nodeMap.get(e.getFirstNode());
//			Node v2 = (Node)nodeMap.get(e.getSecondNode());
//			if ( v1 != null && v2 != null && func.filter(e) ) {
//				Edge e2 = new Edge(v1, v2);
//				e2.setAttributes(e.getAttributes());
//				g.addEdge(e2);
//			}
//		}
//		return g;
//	} //
//	
//	/**
//	 * Filters a graph of unwanted entities.
//	 * @param g the graph to filter
//	 * @param func a FilterFunction that determines what entities are filtered.
//	 */
//	public static void filterGraph(SimpleGraph g, FilterFunction func) {
//		Iterator iter = g.getNodes();
//		List removeList = new ArrayList();
//		while ( iter.hasNext() ) {
//			Node u = (Node)iter.next();
//			if ( !func.filter(u) ) {
//				removeList.add(u);
//			}
//		}
//		iter = removeList.iterator();
//		while ( iter.hasNext() )
//			g.removeNode((Node)iter.next());
//		removeList.clear();
//		iter = g.getEdges();
//		while ( iter.hasNext() ) {
//			Edge e = (Edge)iter.next();
//			if ( !func.filter(e) ) {
//				removeList.add(e);
//			}
//		}
//		iter = removeList.iterator();
//		while ( iter.hasNext() )
//			g.removeEdge((Edge)iter.next());
//		removeList.clear();
//	} //

} // end of class GraphLib
