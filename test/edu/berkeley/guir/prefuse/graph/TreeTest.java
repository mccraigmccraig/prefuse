package edu.berkeley.guir.prefuse.graph;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * 
 * Jun 13, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class TreeTest extends TestCase {

	public static final String LABEL = "label";

	private TreeNode root;

	/**
	 * Constructor for TreeTest.
	 * @param arg0
	 */
	public TreeTest(String arg0) {
		super(arg0);
	} //

	public void testTree() {
		TreeNode n1  = new TreeNode(); n1.setAttribute(LABEL, "n1");
		root = n1;
		sanityCheckTree(n1);
		TreeNode n2a = new TreeNode(); n2a.setAttribute(LABEL, "n2a"); n1.addChild(n2a);
		sanityCheckTree(n1);
		TreeNode n2b = new TreeNode(); n2b.setAttribute(LABEL, "n2b"); n1.addChild(n2b);
		sanityCheckTree(n1);
		TreeNode n2c = new TreeNode(); n2c.setAttribute(LABEL, "n2c"); n1.addChild(n2c);
		sanityCheckTree(n1);
		TreeNode n2d = new TreeNode(); n2d.setAttribute(LABEL, "n2d"); n1.addChild(n2d);
		sanityCheckTree(n1);
		TreeNode n2e = new TreeNode(); n2e.setAttribute(LABEL, "n2e"); n1.addChild(n2e);
		sanityCheckTree(n1);
		TreeNode n2f = new TreeNode(); n2f.setAttribute(LABEL, "n2f"); n1.addChild(n2f);
		sanityCheckTree(n1);
		TreeNode n3a = new TreeNode(); n3a.setAttribute(LABEL, "n3a"); n2c.addChild(n3a);
		sanityCheckTree(n1);
		
		n1.removeChild(n2d); sanityCheckTree(n1);
		n1.removeChild(n2f); sanityCheckTree(n1);
		
		TreeNode s1 = new TreeNode(); s1.setAttribute(LABEL, "s1");
		swapNodes(n1, s1);
		root = s1;
		sanityCheckTree(s1);
		
		TreeNode s2 = new TreeNode(); s2.setAttribute(LABEL, "s2");
		swapNodes(n2c, s2);
		sanityCheckTree(s1);
	} //

	private static void sanityCheckTree(TreeNode n) {
		Iterator childIter = n.getChildren();
		int i = 0;
		while ( childIter.hasNext() ) {
			TreeNode c1 = (TreeNode)childIter.next();
			TreeNode c2 = n.getChild(i++);
			if ( c1 != c2 ) {
				System.err.println("Children not equal!!");
			}
			if ( c1.getParent() != n ) {
				System.err.println("Parent not correct! -- (c,p,pp) = " 
					+ c1.getAttribute(LABEL) + ", " + n.getAttribute(LABEL)
					+ ", " + c1.getParent().getAttribute(LABEL));
			}
			sanityCheckTree(c1);
		}
	} //

	private void swapNodes(TreeNode o, TreeNode n) {		
		while ( o.getNumChildren() > 0 ) {
			TreeNode c = o.removeChild(0);
			sanityCheckTree(root);
			n.addChild(c);
			sanityCheckTree(root);
		}
		TreeNode p = o.getParent();
		sanityCheckTree(root);
		if ( p != null ) {
			int idx = p.getChildIndex(o);
			sanityCheckTree(root);
			p.addChild(idx,n);
			sanityCheckTree(root);
			p.removeChild(o);
			sanityCheckTree(root);
		}
	} //

} // end of class TreeTest
