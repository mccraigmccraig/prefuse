/*
 * Created on Jul 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.berkeley.guir.prefuse.demos;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * @author Jack Li jack(AT)cs_DOTberkeley_DOTedu
 */
public class TimelineDemo extends JFrame {
	private static final String MUSIC_HISTORY = "etc/musichistory.xml";
	private static final String TITLE = "The History of Music";
	
	private static class TimelineDataRenderer extends TextItemRenderer {
	}

	public static void main(String[] args) {
		// load graph
		final XMLGraphReader graphReader = new XMLGraphReader();
		graphReader.setNodeType(DefaultTreeNode.class);
		final Graph graph;
		try {
			graph = graphReader.loadGraph(MUSIC_HISTORY);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		final ItemRegistry registry = new ItemRegistry(graph);
		registry.setRendererFactory(new DefaultRendererFactory(new TimelineDataRenderer(), null, null));
		
		final Display display = new Display(registry);
		display.setSize(1000, 400);
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
