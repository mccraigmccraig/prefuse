package edu.berkeley.guir.prefuse.samples;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.action.GraphNodeFilter;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * Creates a new graph and draws it on the screen.
 */
public class ShowGraph extends JFrame {
	
	private ItemRegistry registry;
	private ActionPipeline pipeline;
	
	public ShowGraph() {
		super("ShowGraph");
		
		// creates a new graph
		Graph g = GraphLib.getClique(5);
		
		// create a new item registry
		//  the item registry stores all the visual
		//  representations of different graph elements
		registry = new ItemRegistry(g);
		
		// create a new display component to show the data
		Display display = new Display(registry);
		display.setSize(400,400);
		// lets users drag nodes around on screen
		display.addControlListener(new DragControl());
		
		// create a new action pipeline that
		// (a) filters visual representations from the original graph
		// (b) performs a random layout of graph nodes
		// (c) calls repaint on displays so that we can see the result
		pipeline = new ActionPipeline(registry);
		pipeline.add(new GraphNodeFilter());
		pipeline.add(new GraphEdgeFilter());
		pipeline.add(new RandomLayout());
		pipeline.add(new RepaintAction());
		
		// set up this JFrame
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(display);
		pack();
		setVisible(true);
        
		// now execute the pipeline to visualize the graph
		pipeline.runNow();
	}

	public static void main(String[] args) {
		new ShowGraph();
	}
}
