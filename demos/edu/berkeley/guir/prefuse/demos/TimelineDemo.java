
/*
 * Created on Jul 3, 2004
 */
package edu.berkeley.guir.prefuse.demos;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TimelineDataRenderer;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineDemo extends JFrame {
	// (( CONSTANTS )) \\
	private static final String MUSIC_HISTORY = "etc/musichistory.xml";
	private static final String TITLE = "The History of Music";
	private static final int NUM_DIVISIONS = 0, NOTCH_LENGTH = 1;
	private static final String NOTCH = "notch";
	private static final String START = "start";
	private static final String END = "end";
	public static final String NOTCH_START = NOTCH+START;
	public static final String NOTCH_END = NOTCH+END;
	public static final String NODE_TYPE = "nodetype";
	public static final String NOTCH_TYPE = "notch";
	public static final String PERIOD_TYPE = "period";
	public static final String EVENT_TYPE = "event";
	public static final String PERSON_TYPE = "person";
	public static final String PIECE_TYPE = "piece";
	
	
	// (( FIELDS )) \\
	private final int appWidth = 1000;
	private final int appHeight = 400; // this should be more fixed than the width
	private final int divisionSpecification = 
		//NOTCH_LENGTH; 
		NUM_DIVISIONS;
	private final int timeline_start = 0;
	private final int timeline_end = 2005;
	
	private Graph graph;

	
	// (( CONSTRUCTOR )) \\
	public TimelineDemo() {
		// load timeline data
		try {
			graph = new XMLGraphReader().loadGraph(MUSIC_HISTORY);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// add notch nodes, which may allow dragging, or zooming
		final int notchLength;
		Node prevNotchNode = null;
		final int timeline_length = timeline_end - timeline_start;
		int nextNotchNum = timeline_start;
		prevNotchNode = connectNewNotchNode(START, prevNotchNode, nextNotchNum); // start node
		int i = 1; // 0 is time_start
		if (divisionSpecification == NUM_DIVISIONS) {
			final int numDivisions = 3; // entered desired division in this mode
			notchLength = timeline_length / numDivisions;
			for ( ; i < numDivisions; i++) {
				nextNotchNum = (i * notchLength) + timeline_start;
				prevNotchNode = connectNewNotchNode(""+i, prevNotchNode, nextNotchNum);
			}
		} else if (divisionSpecification == NOTCH_LENGTH) {
			notchLength = 311; // entered desired length in this mode
			for ( ; nextNotchNum < timeline_end; nextNotchNum += notchLength) {
				i = (nextNotchNum - timeline_start) / notchLength;
				prevNotchNode = connectNewNotchNode(""+i, prevNotchNode, nextNotchNum);
			}
		}
		connectNewNotchNode(END, prevNotchNode, timeline_end); // end node
		
		// create a new item registry
		final ItemRegistry registry = new ItemRegistry(graph);
		registry.setRendererFactory(new DefaultRendererFactory(
				new TimelineDataRenderer(), new DefaultEdgeRenderer(), null));
		
		final Display display = new Display(registry);
		display.setSize(appWidth, appHeight);
		display.addControlListener(new DragControl());
		
		// set up this JFrame
		final JFrame frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(display);
		frame.pack();
		frame.setVisible(true);
		
		final ActionList actions = new ActionList(registry);
		actions.add(new GraphFilter());
		actions.add(new MusicHistoryColorFunction());
		actions.add(new RandomLayout());
		actions.add(new RepaintAction());
		actions.runNow();
	}


	private Node connectNewNotchNode(final String i, final Node prevNotchNode, final int nextNotchNum) {
		final DefaultNode nextNotchNode = new DefaultNode();
		nextNotchNode.setAttribute(
				XMLGraphReader.XMLGraphHandler.ID, NOTCH+i);
		nextNotchNode.setAttribute(
				XMLGraphReader.XMLGraphHandler.LABEL, ""+nextNotchNum);
		nextNotchNode.setAttribute(
				NODE_TYPE, NOTCH_TYPE);
		graph.addNode(nextNotchNode);
		
		if (prevNotchNode != null) { // the first node doesn't have an in-edge
			graph.addEdge(new DefaultEdge(prevNotchNode, nextNotchNode));
		}
		
		return nextNotchNode;
	}
	
	
	// (( INNER CLASS )) \\
	private static class MusicHistoryColorFunction extends ColorFunction {

		/*
		 * (non-Javadoc)
		 * @see edu.berkeley.guir.prefuse.action.assignment.ColorFunction#getFillColor(edu.berkeley.guir.prefuse.VisualItem)
		 */
		public Paint getFillColor(VisualItem item) {
            if ( item instanceof NodeItem ) {
                final String nodeType = item.getAttribute(NODE_TYPE);
                if (nodeType.equals(PERIOD_TYPE)) {
                	return Color.BLUE;
                } else if (nodeType.equals(EVENT_TYPE)) {
                	return Color.RED;
                } else if (nodeType.equals(PERSON_TYPE)) {
                	return Color.GRAY;
                } else if (nodeType.equals(PIECE_TYPE)) {
                	return Color.DARK_GRAY;
                } else {
                	return Color.MAGENTA;
                }
            }
			return super.getFillColor(item);
		}
	}
	
	private static class TimelineLayout extends Layout {

		/* (non-Javadoc)
		 * @see edu.berkeley.guir.prefuse.action.assignment.Layout#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
		 */
		public void run(ItemRegistry registry, double frac) {
			final Iterator nodeItems = registry.getNodeItems();
			
		}
		
	}
	
	// (( MAIN )) \\
	public static void main(String[] args) {
		new TimelineDemo();
	}
}
