package edu.berkeley.guir.prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.ColorFunction;
import edu.berkeley.guir.prefuse.action.ColorInterpolator;
import edu.berkeley.guir.prefuse.action.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.action.GraphNodeFilter;
import edu.berkeley.guir.prefuse.action.PolarInterpolator;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.TreeEdgeFilter;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.TreeLib;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanHandler;
import edu.berkeley.guir.prefusex.controls.ZoomHandler;

/**
 * Demo application showcasing the use of an animated radial tree layout to
 * visualize a graph.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class RadialGraphDemo extends JFrame {

	public static final String GRAPH_GUIR       = "etc/guir.xml";
	public static final String GRAPH_FRIENDSTER = "../prefuse/etc/friendster.xml";
	public static final String GRAPH_TERROR     = "etc/terror.xml";
	public static final String nameField = "label";
		
	private ItemRegistry registry;
	private Graph graph;
	private Tree tree;
	private Display display;
    private ActionPipeline filter, update, animate;
		
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
    
    public static void main(String[] argv) {
        new RadialGraphDemo();
    } //
		
	public RadialGraphDemo() {
		try {
			// load graph
			String inputFile = GRAPH_FRIENDSTER;
			XMLGraphReader gr = new XMLGraphReader();
			gr.setNodeType(TreeNode.class);
			graph = gr.loadGraph(inputFile);
			tree = getInitialTree(graph);
			
			// create display and filter
            registry = new ItemRegistry(tree);
            display = new Display();
            
			// initialize renderers
			Renderer nodeRenderer = new TextItemRenderer() {
				private int maxWidth = 75;
				private StringAbbreviator abbrev = 
					new StringAbbreviator(null, null);
					
				protected String getText(GraphItem item) {
					String s = item.getAttribute(m_labelName);
					Font font = item.getFont();
					if ( font == null ) { font = m_font; }
					FontMetrics fm = m_g.getFontMetrics(font);
					if ( fm.stringWidth(s) > maxWidth ) {
						s = abbrev.abbreviate(s, 
							StringAbbreviator.NAME, 
							fm, maxWidth);			
					}
					return s;
				} //
			};
			Renderer edgeRenderer = new DefaultEdgeRenderer() {
				protected int getLineWidth(GraphItem item) {
					String w = item.getAttribute("weight");
					if ( w != null ) {
						try {
							return Integer.parseInt(w);
						} catch ( Exception e ) {}
					}
					return m_width;
				} //
			};
			Renderer aggrRenderer = null;
            ((TextItemRenderer)nodeRenderer).setRoundedCorner(8,8);
			
			// initialize item registry
			registry.setRendererFactory(new DefaultRendererFactory(
				nodeRenderer, edgeRenderer, aggrRenderer));
			
			// initialize action pipelines
            filter  = new ActionPipeline(registry);
            filter.add(new GraphNodeFilter());
            filter.add(new TreeEdgeFilter());
            filter.add(new RadialTreeLayout());
            filter.add(new GraphEdgeFilter());
            filter.add(new DemoColorFunction(3));
            
            update = new ActionPipeline(registry);
            update.add(new DemoColorFunction(3));
            update.add(new RepaintAction());
            
            animate = new ActionPipeline(registry, 1500, 20);
            animate.setPacingFunction(new SlowInSlowOutPacer());
            animate.add(new PolarInterpolator());
            animate.add(new ColorInterpolator());
            animate.add(new RepaintAction());
            
            // initialize display 
            display.setRegistry(registry);
            display.setSize(700,700);
            display.setBackground(Color.WHITE);
            display.addControlListener(new FocusControl());
            display.addControlListener(new DragControl());
            display.addControlListener(new PanHandler());
            display.addControlListener(new ZoomHandler());
            display.addControlListener(new NeighborHighlightControl(update));
            
			// set up initial focus and focus listener
            registry.getDefaultFocusSet().addFocusListener(new FocusListener() {
                public void focusChanged(FocusEvent e) {
                    if ( update.isScheduled() )
                        update.cancel();
                    TreeNode node = (TreeNode)e.getFirstAdded();
                    if ( node != null && !node.equals(tree.getRoot()) ) {                           
                        tree = TreeLib.breadthFirstTree(node);
                        registry.setGraph(tree);
                        ActivityManager.scheduleNow(filter);
                        ActivityManager.scheduleNow(animate);                      
                    }
                } //
            });
			registry.getDefaultFocusSet().set(tree.getRoot());			

			// create and display application window
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			getContentPane().add(display, BorderLayout.CENTER);
			pack();
			setVisible(true);
			
			// because awt doesn't always give us 
			// our graphics context right away...
			while ( display.getGraphics() == null );
            ActivityManager.scheduleNow(filter);
            ActivityManager.scheduleNow(animate);
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
    public class DemoColorFunction extends ColorFunction {
	    private Color graphEdgeColor = Color.LIGHT_GRAY;
        private Color highlightColor = Color.BLUE;
	    private Color nodeColors[];
	   	private Color edgeColors[];
	   
	   	public DemoColorFunction(int thresh) {
	   		nodeColors = new Color[thresh];
	   	    edgeColors = new Color[thresh];
	   	    for ( int i = 0; i < thresh; i++ ) {
	   	    	double frac = i / ((double)thresh);
	   	    	nodeColors[i] = calcIntermediateColor(Color.RED, Color.BLACK, frac);
	   	    	edgeColors[i] = calcIntermediateColor(Color.RED, Color.BLACK, frac);
	   	    }
	   	} //
	   
	   	public Paint getFillColor(GraphItem item) {
	   		if ( item instanceof NodeItem ) {
	   			return Color.WHITE;
	   		} else if ( item instanceof AggregateItem ) {
	   			return Color.LIGHT_GRAY;
	   		} else if ( item instanceof EdgeItem ) {
	   			return getColor(item);
	   		} else {
	   			return Color.BLACK;
	   		}
	   	} //
	   
		public Paint getColor(GraphItem item) {
            Boolean hl = (Boolean)item.getVizAttribute("highlight");
            if ( hl != null && hl.booleanValue() ) {
                return Color.BLUE;
            } else if (item instanceof NodeItem) {
                int d = ((NodeItem)item).getDepth();
				return nodeColors[Math.min(d, nodeColors.length-1)];
			} else if (item instanceof EdgeItem) {
				EdgeItem e = (EdgeItem) item;
				Edge edge = (Edge) registry.getEntity(e);
				if (edge.isTreeEdge()) {
					int d, d1, d2;
                    d1 = e.getFirstNode().getDepth();
                    d2 = e.getSecondNode().getDepth();
                    d = Math.max(d1, d2);
					return edgeColors[Math.min(d, edgeColors.length-1)];
				} else {
					return graphEdgeColor;
				}
			} else {
				return Color.BLACK;
			}
		} //
   } // end of inner class DemoColorFunction

} // end of classs RadialGraphDemo
