
/*
 * Created on Jul 3, 2004
 */
package edu.berkeley.guir.prefuse.demos;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineDemo extends JFrame {
	
	private static final String MUSIC_HISTORY = "etc/musichistory.xml";
	private static final String TITLE = "The History of Music";
	private static final int NUM_DIVISIONS = 0, NOTCH_LENGTH = 1;

	private static final String NOTCH = "notch";
	
	private static class TimelineDataRenderer extends TextItemRenderer {
	}

	public static void main(String[] args) {
		final int appWidth = 1000;
		final int appHeight = 400; // this should be more fixed than the width
		final int divisionSpecification = NOTCH_LENGTH; 
										  //NUM_DIVISIONS;

		final int timeline_start = 0;
		final int timeline_end = 2005;
		
		// load graph
		//graphReader.setNodeType(DefaultTreeNode.class);
		final Graph graph;
		try {
			graph = /*graphReader*/new XMLGraphReader().loadGraph(MUSIC_HISTORY);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// add mark nodes, which may allow dragging, or zooming
		final int notchLength;
		final int timeline_length = timeline_end - timeline_start;
		if (divisionSpecification == NUM_DIVISIONS) {
			final int numDivisions = 10; // entered desired division in this mode
			notchLength = timeline_length / numDivisions;
			for (int i = 0; i < numDivisions; i++) {
				final int nextNotch = (i * notchLength) + timeline_start;
				final DefaultNode nextNotchNode = new DefaultNode();
				nextNotchNode.setAttribute(
						XMLGraphReader.XMLGraphHandler.ID, NOTCH+i);
				nextNotchNode.setAttribute(
						XMLGraphReader.XMLGraphHandler.LABEL, ""+nextNotch);
				graph.addNode(nextNotchNode);
			}
		} else if (divisionSpecification == NOTCH_LENGTH) {
			notchLength = 311; // entered desired length in this mode
			for (int nextNotch = timeline_start; nextNotch < timeline_end; nextNotch += notchLength) {
				final int i = (nextNotch - timeline_start) / notchLength;
				final DefaultNode nextNotchNode = new DefaultNode();
				nextNotchNode.setAttribute(
						XMLGraphReader.XMLGraphHandler.ID, NOTCH+i);
				nextNotchNode.setAttribute(
						XMLGraphReader.XMLGraphHandler.LABEL, ""+nextNotch);
				graph.addNode(nextNotchNode);
			}
		}

		
		final ItemRegistry registry = new ItemRegistry(graph);
		registry.setRendererFactory(new DefaultRendererFactory(new TimelineDataRenderer(), null, null));
		
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
		actions.add(new RandomLayout());
		actions.add(new RepaintAction());
		actions.runNow();
	}
}
