/*
 * Created on Jun 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.berkeley.guir.prefuse.samples;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * Creates a new graph and draws it on the screen.
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class HelloWorld {
	private static class SimpleDisplay extends Display {
		/**
		 * If interactive, let users drag nodes around on screen
		 * @param registry
		 * @param interactive
		 */
		public SimpleDisplay(final ItemRegistry registry, final boolean interactive) {
			super(registry);
			if (interactive) { 
				addControlListener(new DragControl());
			}
		}
	}
	
	private static class RandomArrangement extends ActionList {
		/**
		 * This ActionList:
		 * (a) filters visual representations from the original graph
		 * (b) performs a random layout of graph nodes
		 * (c) calls repaint on displays so that we can see the result
		 * @param registry
		 * @param repaint
		 */
		public RandomArrangement(final ItemRegistry registry, final boolean repaint) {
			super(registry);
			add(new GraphFilter());
			add(new RandomLayout());
			if (repaint) {
				add(new RepaintAction());
			}
		}
	}

	public static void main(String[] args) {
		// 1. Create a new graph
		final Graph g = GraphLib.getClique(5); //line1
		
		// 2. Create a new item registry to store all the visual
		//  representations of different graph elements
		final ItemRegistry registry = new ItemRegistry(g); //line2
		
		// 3. Create a new display component to show the data
		final Display display = new SimpleDisplay(registry, true); //line3
		
		// set up this JFrame
		final JFrame frame = new JFrame("HelloWorld!"); //line4
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //line5
		frame.getContentPane().add(display); //line6
		frame.pack(); //line7
		frame.setVisible(true);  //line8
		
		// 4. Finally, create a new action list and 
		//  execute the actions to visualize the graph
		new RandomArrangement(registry, true).runNow();  //line9
	}
}
