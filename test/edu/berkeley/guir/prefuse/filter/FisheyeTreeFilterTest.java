package edu.berkeley.guir.prefuse.filter;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.Pipeline;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.TabDelimitedTreeReader;
import edu.berkeley.guir.prefuse.pipeline.FisheyeTreeFilter;

import junit.framework.TestCase;

/**
 * 
 * Apr 24, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class FisheyeTreeFilterTest extends TestCase {

	public static final String TREE_ORGCHART = "etc/orgchart-parc.txt";
	public static final String TREE_INTERNET = "etc/bfs_mini.txt";

	public static final String nameField = "FullName";


	public void testFisheyeTreeFilter() {
		try {
			String inputFile = TREE_ORGCHART;
			Tree tree = new TabDelimitedTreeReader().loadTree(inputFile);
			
			Pipeline pipeline = new Pipeline(tree, null);
			ItemRegistry registry = pipeline.getItemRegistry();
			FisheyeTreeFilter fisheyeFilter = new FisheyeTreeFilter();
			pipeline.addComponent(fisheyeFilter);			
			pipeline.setIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI, -2);
			
			TreeNode focus = tree.getRoot().getChild(2).getChild(1);
			registry.addFocus(focus);

			Iterator nodeIter = registry.getNodeItems();
			while ( nodeIter.hasNext() ) {
				NodeItem item = (NodeItem)nodeIter.next();
				System.out.println(item.getDOI() + ": " + item.getAttribute(nameField));
			}		
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //

	/**
	 * Constructor for FisheyeTreeFilterTest.
	 * @param arg0
	 */
	public FisheyeTreeFilterTest(String arg0) {
		super(arg0);
	} //

} // end of class FisheyeTreeFilterTest
