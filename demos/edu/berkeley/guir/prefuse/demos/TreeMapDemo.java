package edu.berkeley.guir.prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.action.ColorFunction;
import edu.berkeley.guir.prefuse.action.GraphNodeFilter;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.TreeEdgeFilter;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.io.HDirTreeReader;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.ShapeRenderer;
import edu.berkeley.guir.prefuse.util.ColorMap;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.layout.SquarifiedTreeMapLayout;

/**
 * Demonstration showcasing a TreeMap layout of a hierarchical data
 * set and the use of a ColorMap to assign colors to items.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class TreeMapDemo extends JFrame {

    public static final String TREE_CHI = "../prefuse/etc/chitest.hdir";
    
    private ItemRegistry registry;
    
    public TreeMapDemo() {
        super("prefuse TreeMap Demo");
        
        try {
            // load graph and initialize the item registry
            Tree tree = (new HDirTreeReader()).loadTree(TREE_CHI);
            registry = new ItemRegistry(tree);
            registry.setRendererFactory(new DefaultRendererFactory(
                new NodeRenderer(), null, null));
            // make sure we draw from larger->smaller to prevent
            // occlusion from parent node boxes
            registry.setItemComparator(new Comparator() {
                public int compare(Object o1, Object o2) {
                    double s1 = ((GraphItem)o1).getSize();
                    double s2 = ((GraphItem)o2).getSize();
                    return ( s1>s2 ? -1 : (s1<s2 ? 1 : 0));
                } //
            });
            
            // initialize our display
            Display display = new Display();
            display.setRegistry(registry);
            display.setUseCustomTooltips(true);
            PanControl  pH = new PanControl();
            ZoomControl zH = new ZoomControl();
            display.addMouseListener(pH);
            display.addMouseMotionListener(pH);
            display.addMouseListener(zH);
            display.addMouseMotionListener(zH);
            display.addControlListener(new ControlAdapter() {
               public void itemEntered(GraphItem item, MouseEvent e) {
                   Display d = (Display)e.getSource();
                   d.setToolTipText(item.getAttribute("label"));
               } //
               public void itemExited(GraphItem item, MouseEvent e) {
                   Display d = (Display)e.getSource();
                   d.setToolTipText(null);
               } //
            });
            display.setSize(700,700);
            
            // create the single filtering and layout pipeline
            ActionPipeline filter = new ActionPipeline(registry);
            filter.add(new GraphNodeFilter());
            filter.add(new TreeEdgeFilter(false));
            filter.add(new TreeMapSizeFunction());
            filter.add(new SquarifiedTreeMapLayout(4));
            filter.add(new TreeMapColorFunction());
            filter.add(new RepaintAction());
            
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
            
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    } //
    
    public static void main(String argv[]) {
        new TreeMapDemo();
    } //
    
    public class TreeMapColorFunction extends ColorFunction {
        Color c1 = new Color(0.5f,0.5f,0.f);
        Color c2 = new Color(0.5f,0.5f,1.f);
        ColorMap cmap = new ColorMap(ColorMap.getInterpolatedMap(10,c1,c2),0,9);
        public Paint getColor(GraphItem item) {
            return Color.WHITE;
        } //
        public Paint getFillColor(GraphItem item) {
            double v = (item instanceof NodeItem ? ((NodeItem)item).getDepth():0);
            return cmap.getColor(v);
        } //
    } // end of inner class TreeMapColorFunction
    
    public class TreeMapSizeFunction extends AbstractAction {
        public void run(ItemRegistry registry, double frac) {
            int leafCount = 0;
            Iterator iter = registry.getNodeItems();
            while ( iter.hasNext() ) {
                NodeItem n = (NodeItem)iter.next();
                if ( n.getChildCount() == 0 ) {
                    n.setSize(1.0);
                    for (NodeItem p=n.getParent(); p!=null; p=p.getParent())
                        p.setSize(1.0+p.getSize());
                    leafCount++;
                }
            }
            
            Dimension d = registry.getDisplay(0).getSize();
            double area = d.width*d.height;
            double divisor = ((double)leafCount)/area;
            iter = registry.getNodeItems();
            while ( iter.hasNext() ) {
                NodeItem n = (NodeItem)iter.next();
                n.setSize(n.getSize()/divisor);
            }
            
            System.out.println("leafCount = " + leafCount);
        } //
    } // end of inner class TreeMapSizeFunction
    
    public class NodeRenderer extends ShapeRenderer {
        private Rectangle2D bounds = new Rectangle2D.Double();
        protected Shape getRawShape(GraphItem item) {
            Point2D d = (Point2D)item.getVizAttribute("dimension");
            bounds.setRect(item.getX(),item.getY(),d.getX(),d.getY());
            return bounds;
        } //
    } // end of inner class NodeRenderer
    
} // end of class TreeMapDemo
