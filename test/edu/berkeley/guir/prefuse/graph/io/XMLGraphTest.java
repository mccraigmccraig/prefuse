package edu.berkeley.guir.prefuse.graph.io;

import java.io.PrintStream;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.TreeLib;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

import junit.framework.TestCase;

/**
 * 
 * May 21, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class XMLGraphTest extends TestCase {

	public static final String GRAPH_GUIR = "etc/guir.xml";

	public static final String nameField = "label";

	public void testTreeInput() {
		try {
			String inputFile = GRAPH_GUIR;
			XMLGraphReader gr = new XMLGraphReader();
			gr.setNodeType(TreeNode.class);
			Graph graph = gr.loadGraph(inputFile);		
			outputGraph(System.out, graph);
			
			Tree t = getInitialTree(graph);
			outputTree(System.out, t.getRoot(), 0);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //

	private static Tree getInitialTree(Graph g) {		
		Iterator nodeIter = g.getNodes();
		TreeNode r = (TreeNode)nodeIter.next();
		while ( nodeIter.hasNext() ) {
			TreeNode n = (TreeNode)nodeIter.next();
			if ( n.getNumNeighbors() > r.getNumNeighbors() ) {
				r = n;
			}
		}
		return TreeLib.breadthFirstTree(r);
	} //

	private void outputGraph(PrintStream out, Graph g) {
		System.out.println("------<< graph >>------");
		Iterator iter = g.getNodes();
		while ( iter.hasNext() ) {
			Node n = (Node)iter.next();
			System.out.print(n.getAttribute(nameField) + " :: ");
			Iterator niter = n.getNeighbors();
			while ( niter.hasNext() ) {
				Node c = (Node)niter.next();
				System.out.print(c.getAttribute(nameField) + ", ");
			}
			System.out.println();
		}
		System.out.println("------<< graph >>------");		
	} //
	
	private void outputTree(PrintStream out, TreeNode n, int depth) {
		out.println(getTabs(depth) + n.getAttribute(nameField));
		Iterator childIter = n.getChildren();
		while ( childIter.hasNext() ) {
			TreeNode c = (TreeNode)childIter.next();
			outputTree(out, c, depth+1);
		}			
	} //
	
	private String getTabs(int t) {
		StringBuffer buf = new StringBuffer();
		for ( int i = 0; i < t; i++ ) {
			buf.append("\t");
		}
		return buf.toString();
	} //

	/**
	 * Constructor for XMLGraphTest.
	 * @param arg0
	 */
	public XMLGraphTest(String arg0) {
		super(arg0);
	}	

} // end of class XMLGraphTest
