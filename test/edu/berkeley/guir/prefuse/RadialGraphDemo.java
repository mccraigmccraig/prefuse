package edu.berkeley.guir.prefuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.lib.StringAbbreviator;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.TreeLib;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.pipeline.AnimationManager;
import edu.berkeley.guir.prefuse.pipeline.ColorFunction;
import edu.berkeley.guir.prefuse.pipeline.ColorInterpolator;
import edu.berkeley.guir.prefuse.pipeline.DefaultColorFunction;
import edu.berkeley.guir.prefuse.pipeline.Filter;
import edu.berkeley.guir.prefuse.pipeline.FisheyeTreeFilter;
import edu.berkeley.guir.prefuse.pipeline.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.pipeline.Interpolator;
import edu.berkeley.guir.prefuse.pipeline.PipelineComponent;
import edu.berkeley.guir.prefuse.pipeline.PolarInterpolator;
import edu.berkeley.guir.prefuse.pipeline.RadialNodePerturber;
import edu.berkeley.guir.prefuse.pipeline.RadialTreeLayout;
import edu.berkeley.guir.prefuse.pipeline.SlowInSlowOutAnimator;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;

/**
 * Prefuse Demo Application
 * 
 * Apr 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class RadialGraphDemo {

	public static final String GRAPH_GUIR       = "etc/guir.xml";
	public static final String GRAPH_FRIENDSTER = "etc/friendster.xml";
	public static final String GRAPH_TERROR     = "etc/terror.xml";

	public static final String nameField = "label";
		
	public static ItemRegistry registry;
	public static Graph graph;
	public static Tree tree;
	public static Pipeline pipeline;
	public static Display display;
	public static PrefuseContainer container;
	public static RadialTreeLayout layout;
		
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
		
	public static void main(String[] args) {
		try {
			// load graph
			String inputFile = GRAPH_FRIENDSTER;
			XMLGraphReader gr = new XMLGraphReader();
			gr.setNodeType(TreeNode.class);
			graph = gr.loadGraph(inputFile);
			tree = getInitialTree(graph);
			
			// create display and pipeline						
			display = new DemoDisplay();				
			pipeline = new Pipeline(tree, display);
			pipeline.setPipelineManager(new AnimationManager());

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
			//((DefaultEdgeRenderer)edgeRenderer).setEdgeType(DefaultEdgeRenderer.EDGE_TYPE_CURVE);

			// initialize item registry
			registry = pipeline.getItemRegistry();
			registry.setRendererFactory(new DefaultRendererFactory(
				nodeRenderer, edgeRenderer, aggrRenderer));
			
			// initialize display
			DemoController controller = new DemoController(); 
			display.setPipeline(pipeline);
			display.setSize(700,700);
			display.setBackground(Color.WHITE);
			display.addControlListener(controller);
			
			// initialize pipeline	
			Filter            nodeFilter    = new FisheyeTreeFilter();
			                  layout        = new RadialTreeLayout();
			PipelineComponent perturber     = new RadialNodePerturber();
			Filter            edgeFilter    = new GraphEdgeFilter();
			ColorFunction     colorFunction = new DemoColorFunction();
			PipelineComponent slowInSlowOut = new SlowInSlowOutAnimator();
			Interpolator      iInterpolator = new PolarInterpolator();
			Interpolator      cInterpolator = new ColorInterpolator();
			
			pipeline.addComponent(nodeFilter);
			pipeline.addComponent(edgeFilter);
            pipeline.addComponent(layout);
            pipeline.addComponent(perturber);
			pipeline.addComponent(colorFunction);
			pipeline.addComponent(slowInSlowOut);
			pipeline.addComponent(iInterpolator);
			pipeline.addComponent(cInterpolator);
			
			layout.setRadiusIncrement(80);
			pipeline.setIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI, -10);
			pipeline.setIntegerAttribute(AnimationManager.ATTR_FRAME_RATE, 30);			
			pipeline.setIntegerAttribute(AnimationManager.ATTR_ANIM_TIME, 2000);
			pipeline.setDoubleAttribute(AnimationManager.ATTR_ANIM_FRAC, 0.0);

			// set initial focus
			TreeNode focus = tree.getRoot();
			registry.addFocus(focus);			

			// create and display application window
			container = new PrefuseContainer(display);
			container.setFont(new Font("SansSerif",Font.PLAIN,10));
			container.setBackground(Color.WHITE);
			container.getTextEditor().addKeyListener(controller);
			
			JFrame frame = new JFrame("PrefuseDemo");
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			frame.getContentPane().add(container, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			
			// because awt doesn't always give us 
			// our graphics context right away...
			while ( display.getGraphics() == null );
			
			pipeline.runPipeline();
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
	static class DemoController extends ControlAdapter implements KeyListener {
		private Color highlightColor = new Color(150,150,255);
		public void itemEntered(GraphItem item, MouseEvent e) {
			if ( item instanceof NodeItem ) {
				e.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
				
				TreeNode n = (TreeNode)registry.getEntity(item);
				Iterator iter = n.getNeighbors();
				while ( iter.hasNext() ) {
					TreeNode c = (TreeNode)iter.next();
					EdgeItem eitem = registry.getEdgeItem(n.getEdge(c));
					if ( eitem != null && eitem.isVisible() ) {
						eitem.setColor(highlightColor);
						eitem.setFillColor(highlightColor);
						display.drawItem(eitem);	
					}
				}
				iter = n.getNeighbors();
				while ( iter.hasNext() ) {
					TreeNode c = (TreeNode)iter.next();
					NodeItem citem = registry.getNodeItem(c);
					if ( citem != null && citem.isVisible() ) {
						citem.setColor(Color.BLUE);
						display.drawItem(citem);
					}
				}				
				item.setColor(Color.RED);
				display.drawItem(item);
				display.repaintImmediate();
				//container.setToolTipText(item.getAttribute(nameField));
				//container.showToolTip((int)item.getX()+10,(int)item.getY()+10);
			}
		} //
		public void itemExited(GraphItem item, MouseEvent e) {
			if ( item instanceof NodeItem ) {
				e.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				//container.hideToolTip();
				TreeNode n = (TreeNode)registry.getEntity(item);
				Iterator iter = n.getNeighbors();
				while ( iter.hasNext() ) {
					TreeNode c = (TreeNode)iter.next();
					EdgeItem eitem = registry.getEdgeItem(n.getEdge(c));
					if ( eitem != null && eitem.isVisible() ) {
						eitem.setColor(eitem.getEndColor());
						eitem.setFillColor(eitem.getEndFillColor());	
					}					
					NodeItem citem = registry.getNodeItem(c);
					if ( citem != null && citem.isVisible() ) {
						citem.setColor(citem.getEndColor());
					}
				}
				item.setColor(item.getEndColor());
				display.repaint();
			}
		} //
		public void itemClicked(GraphItem item, MouseEvent e) {
			int cc = e.getClickCount();
			if ( item instanceof NodeItem ) {
				if ( cc == 1 ) {
					TreeNode node = (TreeNode)registry.getEntity(item);
					if ( node != null && !node.equals(tree.getRoot()) ) {							
						tree = TreeLib.breadthFirstTree(node);
						pipeline.setGraph(tree);
						registry.setFocus(node);
						pipeline.runPipeline();						
					}
				} else if ( cc == 2 ) {
					container.editText(item, nameField);
				}
			}
		} //

		public void keyPressed(KeyEvent e) {
		} //
		public void keyReleased(KeyEvent e) {
			if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
				container.stopEditing();
				pipeline.runPipeline();
			}
		} //
		public void keyTyped(KeyEvent e) {
		} //
	} // end of inner class DemoController
	
	static class DemoDisplay extends Display implements MouseWheelListener {
		private Color ringColor = Color.LIGHT_GRAY;
		private int prevInc = -1;
		private int nextInc = -1;
		private int inc;
		
		public DemoDisplay() {
			addMouseWheelListener(this);
		} //
		
		protected void prePaint(Graphics2D g) {
			if ( layout == null ) return;
			double animFrac = m_pipeline.getDoubleAttribute(AnimationManager.ATTR_ANIM_FRAC);
			animFrac = ( animFrac >= 1.0 ? 1.0 : animFrac );
			if ( animFrac == 0.0 ) {				
				nextInc = layout.getRadiusIncrement();
				if ( prevInc == -1 ) { prevInc = nextInc; }
			}
			
			Dimension d = this.getSize();
			inc = prevInc + (int)Math.round(animFrac * (nextInc - prevInc));
			if ( inc < 1 ) { return; }
			int w2  = d.width/2;
			int h2  = d.height/2;
			int hyp = (int)Math.round(Math.sqrt(w2*w2+h2*h2));
			g.setColor(ringColor);
			for ( int r = inc; r < hyp; r += inc ) {
				g.drawOval(w2-r, h2-r, 2*r, 2*r);
			}
			if ( animFrac == 1.0 ) {
				prevInc = nextInc;
			}
		} //
		
		public void mouseWheelMoved(MouseWheelEvent e) {
			int rot = e.getWheelRotation();
			int pinc = layout.getRadiusIncrement();
			int ninc = pinc - rot*10;
			if ( ninc > 0 ) {
				prevInc = inc;
				nextInc = ninc;
				
				boolean as = layout.getAutoScale();
				layout.setAutoScale(false);
				layout.setRadiusIncrement(ninc);
				pipeline.runPipeline();
				try { Thread.sleep(100); } catch ( InterruptedException ie ) {}
				layout.setAutoScale(as);
			}
		} //
	} // end of inner class DemoDisplay
	
    static class DemoColorFunction extends DefaultColorFunction {
	    private int  thresh = 3;
	    private Color graphEdgeColor = Color.LIGHT_GRAY; //new Color(150,150,255);
	    private Color nodeColors[];
	   	private Color edgeColors[];
	   
	   	public DemoColorFunction() {
	   		nodeColors = new Color[thresh];
	   	    edgeColors = new Color[thresh];
	   	    for ( int i = 0; i < thresh; i++ ) {
	   	    	double frac = i / ((double)thresh);
	   	    	nodeColors[i] = calcIntermediateColor(Color.RED, Color.BLACK, frac);
	   	    	edgeColors[i] = calcIntermediateColor(Color.RED, Color.BLACK, frac);
	   	    }
	   	} //
	   
	   	public Color calcIntermediateColor(Color c1, Color c2, double frac) {
	   	   return new Color((int)Math.round(frac*c2.getRed()   + (1-frac)*c1.getRed()),
	   						 (int)Math.round(frac*c2.getGreen() + (1-frac)*c1.getGreen()),
	   						 (int)Math.round(frac*c2.getBlue()  + (1-frac)*c1.getBlue()));
	   	} //
	   
	   	public Color getFillColor(GraphItem item) {
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
	   
		public Color getColor(GraphItem item) {
			if (item instanceof NodeItem) {
				int doi = (int)(-1 * ((NodeItem) item).getDOI());
				if (doi > thresh - 1) {
					doi = thresh - 1;
				}
				return nodeColors[doi];
			} else if (item instanceof EdgeItem) {
				EdgeItem e = (EdgeItem) item;
				Edge edge = (Edge) registry.getEntity(e);
				if (edge.isTreeEdge()) {
					int doi, doi1, doi2;
					doi1 = (int)e.getFirstNode().getDOI();
					doi2 = (int)e.getSecondNode().getDOI();
					doi = -1 * (doi1 < doi2 ? doi1 : doi2);
					if (doi > thresh - 1) {
						doi = thresh - 1;
					}
					return edgeColors[doi];
				} else {
					return graphEdgeColor;
				}
			} else {
				return Color.BLACK;
			}
		} //
   } // end of inner class DemoColorFunction

} // end of classs PrefuseDemo
