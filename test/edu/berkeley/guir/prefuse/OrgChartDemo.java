package edu.berkeley.guir.prefuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.TabDelimitedTreeReader;
import edu.berkeley.guir.prefuse.pipeline.AnimationManager;
import edu.berkeley.guir.prefuse.pipeline.ColorFunction;
import edu.berkeley.guir.prefuse.pipeline.ColorInterpolator;
import edu.berkeley.guir.prefuse.pipeline.DefaultColorFunction;
import edu.berkeley.guir.prefuse.pipeline.Filter;
import edu.berkeley.guir.prefuse.pipeline.FisheyeTreeFilter;
import edu.berkeley.guir.prefuse.pipeline.Interpolator;
import edu.berkeley.guir.prefuse.pipeline.PolarInterpolator;
import edu.berkeley.guir.prefuse.pipeline.RadialTreeLayout;
import edu.berkeley.guir.prefuse.pipeline.TreeAggregateFilter;
import edu.berkeley.guir.prefuse.pipeline.TreeEdgeFilter;

/**
 * Prefuse Demo Application
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class OrgChartDemo {

	public static final String TREE_ORGCHART = "etc/orgchart-parc.txt";
	public static final String TREE_INTERNET = "etc/bfs_tree.txt";

	public static final String nameField = "FullName";
		
	public static ItemRegistry registry;
	public static Tree tree;
	public static Pipeline pipeline;
	public static RadialTreeLayout layout;
		
	public static void main(String[] args) {
		try {
			String inputFile = TREE_ORGCHART;
			tree = new TabDelimitedTreeReader().loadTree(inputFile);

			Display display = new DemoDisplay();				
			pipeline = new Pipeline(tree, display);
			pipeline.setPipelineManager(new AnimationManager());

			registry = pipeline.getItemRegistry();
			display.setPipeline(pipeline);
			display.setSize(600,600);
			display.setBackground(Color.WHITE);
			display.addControlListener(new ControlAdapter() {
				public void itemEntered(VisualItem item, MouseEvent e) {
					if ( item instanceof NodeItem ) {
						System.out.println(((NodeItem)item).getDOI() + ": Entered: " + item.getAttribute(nameField));
					}
          			e.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
				} //
				public void itemExited(VisualItem item, MouseEvent e) {
					if ( item instanceof NodeItem ) {
						//System.out.println(((NodeItem)item).getDOI() + ": Exited: " + item.getAttribute(nameField));
					}
          			e.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				} //
				public void itemClicked(VisualItem item, MouseEvent e) {
					System.out.println("Clicked: " + item.getAttribute(nameField));
					if ( item instanceof NodeItem ) {
						TreeNode node = (TreeNode)registry.getEntity(item);
						if ( node != null && !node.equals(tree.getRoot()) ) {							
							tree.switchRoot(node);
							registry.setFocus(node);
							pipeline.runPipeline();						
						}
					}
				} //
			});
						
			Filter        nodeFilter    = new FisheyeTreeFilter();
			              layout        = new RadialTreeLayout();
			Filter        edgeFilter    = new TreeEdgeFilter();
			Filter	      aggrFilter    = new TreeAggregateFilter();
			ColorFunction colorFunction = new DemoColorFunction();	
			Interpolator  iInterpolator = new PolarInterpolator();
			Interpolator  cInterpolator = new ColorInterpolator();
			
			pipeline.addComponent(nodeFilter);
			pipeline.addComponent(layout);
			pipeline.addComponent(edgeFilter);
			pipeline.addComponent(aggrFilter);
			pipeline.addComponent(colorFunction);
			pipeline.addComponent(iInterpolator);
			pipeline.addComponent(cInterpolator);
			
			pipeline.setIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI, -9);
			pipeline.setIntegerAttribute(AnimationManager.ATTR_FRAME_RATE, 40);			
			pipeline.setIntegerAttribute(AnimationManager.ATTR_ANIM_TIME, 700);
			pipeline.setDoubleAttribute(AnimationManager.ATTR_ANIM_FRAC, 0.0);

			TreeNode focus = tree.getRoot();
			registry.addFocus(focus);			

			JFrame frame = new JFrame("PrefuseDemo");
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			frame.getContentPane().add(display, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			
			while ( display.getGraphics() == null );
			pipeline.runPipeline();
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //

	static class DemoDisplay extends Display implements MouseWheelListener {
		private int prevInc = -1;
		private int nextInc = -1;
		private int inc;
		
		public DemoDisplay() {
			addMouseWheelListener(this);
		} //
		
		protected void prePaint(Graphics2D g) {
			double animFrac = m_pipeline.getDoubleAttribute(AnimationManager.ATTR_ANIM_FRAC);
			animFrac = ( animFrac >= 1.0 ? 1.0 : animFrac );
			if ( animFrac == 0.0 ) {				
				nextInc = layout.getRadiusIncrement();
				if ( prevInc == -1 ) { prevInc = nextInc; }
			}
			
			Dimension d = this.getSize();
			inc = prevInc + (int)Math.round(animFrac * (nextInc - prevInc));
			int w2  = d.width/2;
			int h2  = d.height/2;
			int hyp = (int)Math.round(Math.sqrt(w2*w2+h2*h2));
			g.setColor(Color.LIGHT_GRAY);
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
	   
	   	public Paint getColor(VisualItem item) {
        	if ( item instanceof NodeItem ) {
		   		int doi = (int)(-1*((NodeItem)item).getDOI());
		   		if ( doi > thresh-1 ) { doi = thresh-1; }
		   		return nodeColors[doi];
		   } else if ( item instanceof EdgeItem ) {
			   EdgeItem e = (EdgeItem)item;
			   int doi, doi1, doi2;
			   doi1 = (int)e.getFirstNode().getDOI();
			   doi2 = (int)e.getSecondNode().getDOI();
			   doi = -1*( doi1 < doi2 ? doi1 : doi2 );
			   if ( doi > thresh-1) { doi = thresh-1; }
			   return edgeColors[doi];
		   } else {
			   return Color.BLACK;
		   }
	   } //
   } // end of inner class DemoColorFunction

} // end of classs PrefuseDemo
