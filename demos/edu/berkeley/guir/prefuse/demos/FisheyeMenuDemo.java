package edu.berkeley.guir.prefuse.demos;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.ActionMap;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.ActivityMap;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.distortion.BifocalDistortion;

/**
 * 
 * Mar 23, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FisheyeMenuDemo extends JFrame {

    private ActivityMap  activityMap = new ActivityMap();
    private ActionMap    actionMap   = new ActionMap();
    
    public FisheyeMenuDemo() {
        super("FisheyeMenuDemo");
        
        Graph g = GraphLib.getStar(100);
        
        ItemRegistry registry = new ItemRegistry(g);
        
        TextItemRenderer nodeRenderer = new TextItemRenderer();
        nodeRenderer.setRenderType(TextItemRenderer.RENDER_TYPE_NONE);
        nodeRenderer.setHorizontalAlignment(TextItemRenderer.ALIGNMENT_LEFT);
        registry.setRendererFactory(new DefaultRendererFactory(
            nodeRenderer, null, null));
        
        Display display = new Display(registry);
        display.setSize(200,800);
        display.addControlListener(new DragControl());
        
        ActionList init = new ActionList(registry);
        init.add(new GraphFilter(false));
        init.add(new VerticalLineLayout());
        init.add(new RepaintAction());
        
        ActionList distort = new ActionList(registry);
        distort.add(actionMap.put("distort",
            new BifocalDistortion(0.0,1.0,0.1,3)));
        distort.add(new RepaintAction());
        activityMap.put("distortion",distort);
        
        // create and display application window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(display);
        pack();
        setVisible(true);
        
        init.runNow();
        
        // enable distortion mouse-over
        DistortionController dc = new DistortionController();
        display.addMouseListener(dc);
        display.addMouseMotionListener(dc);
    } //
    
    public static void main(String[] args) {
        new FisheyeMenuDemo();
    } //
    
    class DistortionController extends MouseAdapter implements MouseMotionListener {
        Point2D tmp = new Point2D.Float();
        public void mouseExited(MouseEvent e) {
            ((Layout)actionMap.get("distort")).setLayoutAnchor(null);
            activityMap.scheduleNow("distortion");
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
            ((Layout)actionMap.get("distort")).setLayoutAnchor(tmp);
            activityMap.scheduleNow("distortion");
        } //
    } // end of inner class DistortionController
    
    public class VerticalLineLayout extends Layout {
        public void run(ItemRegistry registry, double frac) {
            // first pass
            double h = 0;
            Iterator iter = registry.getNodeItems();
            while ( iter.hasNext() ) {
                VisualItem item = (VisualItem)iter.next();
                h += item.getBounds().height;
            }
            
            Rectangle2D bounds = getLayoutBounds(registry);
            double scale = bounds.getHeight() / h;
            
            // second pass
            h = 0;
            double ih,x,y;
            iter = registry.getNodeItems();
            while ( iter.hasNext() ) {
                VisualItem item = (VisualItem)iter.next();
                item.updateSize(scale);
                item.setSize(scale);
                Rectangle b = item.getBounds();
                ih = b.height;
                System.out.println("ih = "+ih);
                x = 5;
                y = h+(ih/2);
                item.updateLocation(x,y);
                item.setLocation(x,y);
                h += ih;
            }
        }
    } //
    
} // end of class FisheyeMenuDemo
