package edu.berkeley.guir.prefuse.demos;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.ActionMap;
import edu.berkeley.guir.prefuse.action.ColorFunction;
import edu.berkeley.guir.prefuse.action.DistortionLayout;
import edu.berkeley.guir.prefuse.action.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.action.GraphNodeFilter;
import edu.berkeley.guir.prefuse.action.Layout;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.activity.ActivityMap;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultNodeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefusex.controls.DragControl;

/**
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DistortionDemo extends JFrame {

    private ItemRegistry registry;
    private ActivityMap  activityMap = new ActivityMap();
    private ActionMap    actionMap   = new ActionMap();
    
    public static void main(String argv[]) {
        new DistortionDemo();
    } //
    
    public DistortionDemo() {
        super("Distortion Demo");
        
        Graph g = GraphLib.getGrid(20,20);
        registry = new ItemRegistry(g);
        
        DistortionController dc = new DistortionController();
        
        Display display = new Display();
        display.setRegistry(registry);
        display.setSize(600,600);
        display.addControlListener(new DragControl());
        display.addMouseListener(dc);
        display.addMouseMotionListener(dc);
        
        registry.setRendererFactory(new DefaultRendererFactory(
            new DefaultNodeRenderer() {
                public int getRenderType() {
                    return RENDER_TYPE_FILL;
                }
            }, 
            new DefaultEdgeRenderer(), 
            null));
        
        ActionPipeline filter = new ActionPipeline(registry);
        filter.add(new GraphNodeFilter());
        filter.add(new GraphEdgeFilter());
        filter.add(new ColorFunction());
        filter.add(actionMap.put("grid",new GridLayout()));
        filter.add(new RepaintAction());
        
        ((Layout)actionMap.get("grid"))
            .setLayoutBounds(new Rectangle(100,100,400,400));
        
        ActionPipeline bifocal = new ActionPipeline(registry);
        bifocal.add(actionMap.put("distort",new DistortionLayout()));
        bifocal.add(new RepaintAction());
        activityMap.put("bifocal",bifocal);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        getContentPane().add(display);
        pack();
        setVisible(true);
        
        // wait until graphics are available
        while ( display.getGraphics() == null );
        ActivityManager.scheduleNow(filter);
    } //
    
    class GridLayout extends Layout {
        public void run(ItemRegistry registry, double frac) {
            Rectangle2D b = getLayoutBounds(registry);
            double bx = b.getMinX(), by = b.getMinY();
            double w = b.getWidth(), h = b.getHeight();
            int m, n;
            Graph g = (Graph)registry.getGraph();
            Iterator iter = g.getNodes(); iter.next();
            for ( n=2; iter.hasNext(); n++ ) {
                Node nd = (Node)iter.next();
                if ( nd.getNumEdges() == 2 )
                    break;
            }
            m = g.getNumNodes() / n;
            iter = g.getNodes();
            for ( int i=0; iter.hasNext(); i++ ) {
                Node nd = (Node)iter.next();
                NodeItem ni = registry.getNodeItem(nd);
                double x = bx + w*((i%n)+0.5)/(double)n;
                double y = by + h*((i/n)+0.5)/(double)m;
                ni.updateLocation(x,y);
                ni.setLocation(x,y);
            }
        } //
    } // end of inner class GridLayout
    
    class DistortionController extends MouseAdapter implements MouseMotionListener {
        Point2D tmp = new Point2D.Float();
        public void mouseExited(MouseEvent e) {
            Layout distort = (Layout)actionMap.get("distort");
            distort.setLayoutAnchor(null);
            activityMap.scheduleNow("bifocal");
        } //
        public void mouseMoved(MouseEvent e) {
            moveEvent(e);
        } //
        public void mouseDragged(MouseEvent e) {
            moveEvent(e);
        } //
        public void moveEvent(MouseEvent e) {
            Display d = (Display)e.getSource();
            d.getAbsoluteCoordinate(e.getPoint(), tmp);
            Layout distort = (Layout)actionMap.get("distort");
            distort.setLayoutAnchor(tmp);
            activityMap.scheduleNow("bifocal");
        } //
    } // end of inner class DistortionController
    
} // end of class DistortionDemo
