package edu.berkeley.guir.prefuse.graph.external;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.ColorFunction;
import edu.berkeley.guir.prefuse.action.FisheyeGraphFilter;
import edu.berkeley.guir.prefuse.action.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderListener;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanHandler;
import edu.berkeley.guir.prefusex.controls.ZoomHandler;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;

/**
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FileSystemDemo extends JFrame {

    private ItemRegistry   registry;
    private ActionPipeline forces, filter;
    private FileSystemLoader loader;
    
    public static void main(String[] argv) {
        new FileSystemDemo();
    } //
    
    public FileSystemDemo() {
        super("File System Demo");
        
        Graph g = new DefaultGraph();
        registry = new ItemRegistry(g);
        
        loader = new FileSystemLoader(registry);
        Node root = loader.loadNode(GraphLoader.LOAD_NEIGHBORS, 
                                    null, new File("."));
        
        loader.addGraphLoaderListener(new GraphLoaderListener() {
            public void entityLoaded(GraphLoader loader, Entity e) {
                ActivityManager.schedule(filter);
            } //
            public void entityUnloaded(GraphLoader loader, Entity e) {
                ActivityManager.schedule(filter);
            } //
        });
        
        ForceSimulator fsim = new ForceSimulator();
        fsim.addForce(new NBodyForce(-0.4f, 0.9f));
        fsim.addForce(new SpringForce(1E-5f, 150f));
        fsim.addForce(new DragForce(-0.005f));
        
        TextItemRenderer    nodeRenderer = new TextItemRenderer() {
            protected int getRenderType() {
                return RENDER_TYPE_FILL; 
            } //
        };
        nodeRenderer.setRoundedCorner(8,8);
        nodeRenderer.setTextAttributeName("label");
        DefaultEdgeRenderer edgeRenderer = new DefaultEdgeRenderer();    
        registry.setRendererFactory(new DefaultRendererFactory(
                nodeRenderer, edgeRenderer, null));
        
        filter = new ActionPipeline(registry);
        filter.add(new FisheyeGraphFilter(-2));
        filter.add(new GraphEdgeFilter());
        
        forces = new ActionPipeline(registry,-1,20);
        forces.add(new ForceDirectedLayout(fsim, false) {
            protected float getSpringLength(NodeItem n1, NodeItem n2) {
                double doi = Math.max(n1.getDOI(), n2.getDOI());
                return 200.f/Math.abs((float)doi-1);
            } //
        });
        forces.add(new DemoColorFunction());
        forces.add(new RepaintAction());
        
        Display display = new Display();
        display.setRegistry(registry);
        display.setSize(800,700);
        display.pan(350,350);
        display.addControlListener(new FocusControl(2));
        display.addControlListener(new NeighborHighlightControl());
        display.addControlListener(new DragControl(false));
        display.addControlListener(new MouseOverControl());
        display.addControlListener(new PanHandler(false));
        display.addControlListener(new ZoomHandler(false));
        registry.getDefaultFocusSet().addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                ActivityManager.scheduleNow(filter);
            } //
        });
        registry.getDefaultFocusSet().set(root);
        
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(display);
        pack();
        setVisible(true);
        
        // wait until graphics are available
        while ( display.getGraphics() == null );
        ActivityManager.scheduleNow(filter);
        ActivityManager.scheduleNow(forces);
    }
    
    public class DemoColorFunction extends ColorFunction {
        private Color pastelRed = new Color(255,125,125);
        private Color pastelOrange = new Color(255,200,125);
        private Color lightGray = new Color(220,220,255);
        public Paint getColor(GraphItem item) {
            if ( item instanceof EdgeItem ) {
                Boolean h = (Boolean)item.getVizAttribute("highlight");
                if ( h != null && h.booleanValue() )
                    return pastelOrange;
                else
                    return Color.LIGHT_GRAY;
            } else {
                return Color.BLACK;
            }
        } //
        public Paint getFillColor(GraphItem item) {
            Boolean h = (Boolean)item.getVizAttribute("highlight");
            if ( h != null && h.booleanValue() )
                return pastelOrange;
            else if ( item instanceof NodeItem ) {
                if ( item.isFixed() )
                    return pastelRed;
                else
                    return lightGray;
            } else {
                return Color.BLACK;
            }
        } //        
    } // end of inner class DemoColorFunction
    
    /**
     * Tags and fixes the node under the mouse pointer.
     */
    public class MouseOverControl extends ControlAdapter {
        
        public void itemEntered(GraphItem item, MouseEvent e) {
            ((Display)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            item.setFixed(true);
        } //
        
        public void itemExited(GraphItem item, MouseEvent e) {
            ((Display)e.getSource()).setCursor(Cursor.getDefaultCursor());
            item.setFixed(false);
        } //
        
        public void itemReleased(GraphItem item, MouseEvent e) {
            item.setFixed(false);
        } //
        
    } // end of inner class FocusControl
    
} // end of class FileSystemDemo
