package edu.berkeley.guir.prefuse.pipeline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.Pipeline;
import edu.berkeley.guir.prefuse.pipeline.FisheyeTreeFilter;
import edu.berkeley.guir.prefuse.pipeline.RadialTreeLayout;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.TabDelimitedTreeReader;

import junit.framework.TestCase;

/**
 * 
 * Apr 24, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class RadialTreeLayoutTest extends TestCase {

	public static final String TREE_ORGCHART = "etc/orgchart-parc.txt";
	public static final String TREE_INTERNET = "etc/bfs_tree.txt";

	public static final String nameField = "FullName";

	public ItemRegistry m_registry;

	public void testRadialTreeLayout() {
		try {
			String inputFile = TREE_ORGCHART;
			Tree tree = new TabDelimitedTreeReader().loadTree(inputFile);

			Display display   = new MyDisplay();					
			Pipeline pipeline = new Pipeline(tree, display);

			display.setSize(600,600);	
			m_registry = pipeline.getItemRegistry();

			FisheyeTreeFilter fisheyeFilter = new FisheyeTreeFilter();
			RadialTreeLayout radialLayout   = new RadialTreeLayout();
			pipeline.setIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI, -100);
			
			pipeline.addComponent(fisheyeFilter);
			pipeline.addComponent(radialLayout);

			TreeNode focus = tree.getRoot();
			m_registry.addFocus(focus);
			
			pipeline.runPipeline();

			JFrame frame = new JFrame("JUnit Test Display");
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			frame.getContentPane().add(display, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			//display.createBufferStrategy(2);
			
			char c = 0;
			while ( c != 'q' ) {				
				try {
					Thread.sleep(250);
					c = (char)System.in.read();
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //

	public class MyDisplay extends Display {
		boolean drawEdges = true;
		boolean drawNodes = true;				
		
		public void paint(Graphics g) {
			//BufferStrategy strategy = getBufferStrategy();
			//g = strategy.getDrawGraphics();
			
			g.setColor(Color.WHITE);
			g.fillRect(0,0,600,600);

			Iterator nodeIter = m_registry.getNodeItems();
			while ( nodeIter.hasNext() ) {
				NodeItem item = (NodeItem)nodeIter.next();
				int doi = (int)item.getDOI(), x = (int)item.getX(), y = (int)item.getY();
				
				if ( drawEdges ) {
					TreeNode node = (TreeNode)m_registry.getEntity(item);
					if ( node.getParent() != null ) {
						NodeItem parent = m_registry.getNodeItem(node.getParent());
						g.setColor(Color.BLACK);
						g.drawLine(x,y,(int)parent.getX(), (int)parent.getY());
					}
				}
				
				if ( drawNodes ) {
					int r, d;
					if ( doi == 0 ) {
						r = 10; d = 20;
					} else if ( doi >= -1 ) {
						r = 5; d = 10;
					} else {
						r = 3; d = 6;
					}
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(x-r,y-r,d,d);
					g.setColor(Color.BLACK);
					g.drawOval(x-r,y-r,d,d);
				}
			}
			
			//strategy.show();
		} //
	} //

	/**
	 * Constructor for RadialTreeLayoutTest.
	 * @param arg0
	 */
	public RadialTreeLayoutTest(String arg0) {
		super(arg0);
	} //

} // end of class RadialTreeLayoutTest
