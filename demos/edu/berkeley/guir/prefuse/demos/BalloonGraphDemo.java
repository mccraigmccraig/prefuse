package edu.berkeley.guir.prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.ColorFunction;
import edu.berkeley.guir.prefuse.action.ColorInterpolator;
import edu.berkeley.guir.prefuse.action.FisheyeTreeFilter;
import edu.berkeley.guir.prefuse.action.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.action.LinearInterpolator;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.TreeEdgeFilter;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.collections.DOIItemComparator;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeLib;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.HDirTreeReader;
import edu.berkeley.guir.prefuse.graph.io.TreeReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultNodeRenderer;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.RendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.ColorMap;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;
import edu.berkeley.guir.prefusex.layout.BalloonTreeLayout;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.SubtreeDragControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;

/**
 * Visualizes a tree structure using a balloon tree layout.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class BalloonGraphDemo {

	public static final String TREE_CHI = "../prefuse/etc/chitest.hdir";

	public static final String nameField = "label";
		
	public static ItemRegistry registry;
	public static Tree tree;
	public static Display display;
    public static ActionPipeline filter, update, animate;
    //public static ActionPipeline pipeline, animate, animate2;
    
    private static Font frameCountFont = new Font("SansSerif", Font.PLAIN, 14);
		
	public static void main(String[] args) {
		try {
			// load graph
			String inputFile = TREE_CHI;
			TreeReader tr = new HDirTreeReader();
			tree = tr.loadTree(inputFile);
			
			// create display and filter
            registry = new ItemRegistry(tree);
            registry.setItemComparator(new DOIItemComparator());
            display = new DemoDisplay();

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
            Renderer nodeRenderer2 = new DefaultNodeRenderer();
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
            ((TextItemRenderer)nodeRenderer).setRoundedCorner(8,8);
			
			// initialize item registry
			registry.setRendererFactory(new DemoRendererFactory(
				nodeRenderer, nodeRenderer2, edgeRenderer));
			
            // initialize action pipelines
            filter  = new ActionPipeline(registry);
            filter.add(new FisheyeTreeFilter(-4));
            filter.add(new TreeEdgeFilter());
            filter.add(new BalloonTreeLayout());
            filter.add(new GraphEdgeFilter());
            filter.add(new DemoColorFunction(4));
            
            update = new ActionPipeline(registry);
            update.add(new DemoColorFunction(4));
            update.add(new RepaintAction());
            
            animate = new ActionPipeline(registry, 1500, 20);
            animate.setPacingFunction(new SlowInSlowOutPacer());
            animate.add(new LinearInterpolator());
            animate.add(new ColorInterpolator());
            animate.add(new RepaintAction());
            
            // initialize display
            display.setRegistry(registry);
            display.setSize(700,700);
            display.setBackground(Color.WHITE);
            display.addControlListener(new FocusControl());
            display.addControlListener(new SubtreeDragControl());
            display.addControlListener(new PanControl());
            display.addControlListener(new ZoomControl());
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
			JFrame frame = new JFrame("BalloonTree");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(display, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			
			// because awt doesn't always give us 
			// our graphics context right away...
			while ( display.getGraphics() == null );
            ActivityManager.scheduleNow(filter);
            ActivityManager.scheduleNow(animate);
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
	
    static class DemoRendererFactory implements RendererFactory {
        private Renderer nodeRenderer1;
        private Renderer nodeRenderer2;
        private Renderer edgeRenderer;
        public DemoRendererFactory(Renderer nr1, Renderer nr2, Renderer er) {
            nodeRenderer1 = nr1;
            nodeRenderer2 = nr2;
            edgeRenderer = er;
        } //
        public Renderer getRenderer(GraphItem item) {
            if ( item instanceof NodeItem ) {
                int d = ((NodeItem)item).getDepth();
                if ( d > 1 ) {
                    int r = (d == 2 ? 5 : 1);
                    ((DefaultNodeRenderer)nodeRenderer2).setRadius(r);
                    return nodeRenderer2;
                } else {
                    return nodeRenderer1;
                }
            } else if ( item instanceof EdgeItem ) {
                return edgeRenderer;
            } else {
                return null;
            }
        } //
    } // end of inner class DemoRendererFactory
    
	static class DemoDisplay extends Display {
		private Color ringColor = Color.LIGHT_GRAY;
		private int prevInc = -1;
		private int nextInc = -1;
		private int inc;
		
		protected void prePaint(Graphics2D g) {
            Dimension d = getSize();
            String fr = String.valueOf(frameRate) + "00";
            fr = fr.substring(0,fr.indexOf(".")+3);
            String s = "frame rate: " + fr + "fps";
            FontMetrics fm = g.getFontMetrics(frameCountFont);
            int h = fm.getHeight();
            int w = fm.stringWidth(s);
            g.setFont(frameCountFont);
            g.setColor(Color.BLACK);
            g.drawString(s, d.width-w-10, 5+h);
//			if ( layout == null ) return;
//			double animFrac = m_pipeline.getDoubleAttribute(AnimationManager.ATTR_ANIM_FRAC);
//			animFrac = ( animFrac >= 1.0 ? 1.0 : animFrac );
//			if ( animFrac == 0.0 ) {				
//				nextInc = layout.getRadiusIncrement();
//				if ( prevInc == -1 ) { prevInc = nextInc; }
//			}
//			
//			Dimension d = this.getSize();
//			inc = prevInc + (int)Math.round(animFrac * (nextInc - prevInc));
//			if ( inc < 1 ) { return; }
//			int w2  = d.width/2;
//			int h2  = d.height/2;
//			int hyp = (int)Math.round(Math.sqrt(w2*w2+h2*h2));
//			g.setColor(ringColor);
//			for ( int r = inc; r < hyp; r += inc ) {
//				g.drawOval(w2-r, h2-r, 2*r, 2*r);
//			}
//			if ( animFrac == 1.0 ) {
//				prevInc = nextInc;
//			}
		} //

	} // end of inner class DemoDisplay
	
    static public class DemoColorFunction extends ColorFunction {
        private Color graphEdgeColor = Color.LIGHT_GRAY;
        private Color highlightColor = Color.BLUE;
        private ColorMap cmap; 
        
        public DemoColorFunction(int thresh) {
            cmap = new ColorMap(
                ColorMap.getInterpolatedMap(Color.RED, Color.BLACK),0,thresh);
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
                return cmap.getColor(d);
            } else if (item instanceof EdgeItem) {
                EdgeItem e = (EdgeItem) item;
                if ( e.isTreeEdge() ) {
                    int d, d1, d2;
                    d1 = e.getFirstNode().getDepth();
                    d2 = e.getSecondNode().getDepth();
                    d = Math.max(d1, d2);
                    return cmap.getColor(d);
                } else {
                    return graphEdgeColor;
                }
            } else {
                return Color.BLACK;
            }
        } //
    } // end of inner class DemoColorFunction

} // end of classs RadialGraphDemo
