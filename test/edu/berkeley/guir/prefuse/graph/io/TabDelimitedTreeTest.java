package edu.berkeley.guir.prefuse.graph.io;

import java.io.PrintStream;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

import junit.framework.TestCase;

/**
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class TabDelimitedTreeTest extends TestCase {

	public static final String TREE_ORGCHART = "etc/orgchart-parc.txt";
	public static final String TREE_INTERNET = "etc/bfs_mini.txt";

	public static final String nameField = "FullName";

	public void testTreeInput() {
		try {
			String inputFile = TREE_ORGCHART;
			Tree tree = new TabDelimitedTreeReader().loadTree(inputFile);		
			outputTree(System.out, tree.getRoot(), 0);
			
			TreeNode stuCard = tree.getRoot().getChild(10).getChild(7);
			System.out.println("\n -- new root: " 
								+ stuCard.getAttribute("FullName") + " --\n");
			tree.switchRoot(stuCard);
			outputTree(System.err, tree.getRoot(), 0);		
		} catch ( Exception e ) {
			e.printStackTrace();
		}
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
	 * Constructor for TabDelimitedTreeTest.
	 * @param arg0
	 */
	public TabDelimitedTreeTest(String arg0) {
		super(arg0);
	}	

} // end of class TabDelimitedTreeTest
