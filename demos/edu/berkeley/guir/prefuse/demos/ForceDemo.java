package edu.berkeley.guir.prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.ColorFunction;
import edu.berkeley.guir.prefuse.action.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.action.GraphNodeFilter;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.ActivityManager;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultNodeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanHandler;
import edu.berkeley.guir.prefusex.controls.ZoomHandler;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForcePanel;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;

/**
 * Application demo of a graph visualization using an interactive
 * force-based layout.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ForceDemo extends Display {

    private JFrame     frame;
    private ForcePanel fpanel;
    
    private ForceSimulator m_fsim;
    private String         m_textField;
    private ItemRegistry   m_registry;
    private Activity       m_pipeline;
    
    private Font frameCountFont = new Font("SansSerif", Font.PLAIN, 14);
    
    public ForceDemo(Graph g, ForceSimulator fsim) {
        this(g, fsim, "label");
    } //
    
    public ForceDemo(Graph g, ForceSimulator fsim, String textField) {
        // set up component first
        m_fsim = fsim;
        m_textField = textField;
        m_registry = new ItemRegistry(g);
        this.setRegistry(m_registry);
        initRenderers();
        m_pipeline = initPipeline();
        setSize(700,700);
        pan(350,350);
        this.addControlListener(new MouseOverControl());
        this.addControlListener(new NeighborHighlightControl());
        this.addControlListener(new DragControl(false));
        this.addControlListener(new PanHandler(false));
        this.addControlListener(new ZoomHandler(false));
    } //
    
    public void runDemo() {
        // now set up application window
        fpanel = new ForcePanel(m_fsim);
        
        frame = new JFrame("Force Simulator Demo");
        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(this, BorderLayout.CENTER);
        c.add(fpanel, BorderLayout.EAST);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension d = frame.getSize();
                Dimension p = fpanel.getSize();
                Insets in = frame.getInsets();
                ForceDemo.this.setSize(d.width-in.left-in.right-p.width,
                        d.height-in.top-in.bottom);
            } //
            
        });
        frame.pack();
        frame.setVisible(true);
        
        while ( getGraphics() == null ); // cycle until we can draw!
        ActivityManager.scheduleNow(m_pipeline);
    } //
    
    private void initRenderers() {
        TextItemRenderer    nodeRenderer = new TextItemRenderer() {
            protected int getRenderType() {
                return RENDER_TYPE_FILL; 
            } //
        };
        nodeRenderer.setRoundedCorner(8,8);
        nodeRenderer.setTextAttributeName(m_textField);
        DefaultNodeRenderer nRenderer = new DefaultNodeRenderer();
        DefaultEdgeRenderer edgeRenderer = new DefaultEdgeRenderer();    
        m_registry.setRendererFactory(new DefaultRendererFactory(
                nodeRenderer, edgeRenderer, null));
    } //
    
    private ActionPipeline initPipeline() {
        ActionPipeline pipeline = new ActionPipeline(m_registry,-1,20);
        pipeline.add(new GraphNodeFilter());
        pipeline.add(new GraphEdgeFilter());
        pipeline.add(new ForceDirectedLayout(m_fsim, false));
        pipeline.add(new DemoColorFunction());
        pipeline.add(new RepaintAction());
        return pipeline;
    } //
    
    protected void prePaint(Graphics2D g) {
        Dimension d = getSize();
        String fr = String.valueOf(frameRate) + "00";
        fr = fr.substring(0,fr.indexOf(".")+3);
        String s = "frame rate: " + fr + "fps";
        g.setTransform(new AffineTransform());
        FontMetrics fm = g.getFontMetrics(frameCountFont);
        int h = fm.getHeight();
        int w = fm.stringWidth(s);
        g.setFont(frameCountFont);
        g.setColor(Color.BLACK);
        g.drawString(s, d.width-w-10, 5+h);
        g.setTransform(getTransform());
    } //
    
    public static void main(String argv[]) {
        String file = (argv.length==0 ? "etc/friendster.xml" : argv[0]);
        //String file = "../prefuse/etc/terror.xml";
        //Graph g;
        //try {
        //    g = (new XMLGraphReader()).loadGraph(file);
        //} catch ( Exception e ) { e.printStackTrace(); return; }
        
        Graph g = GraphLib.getStar(40);
        
        System.out.println("Visualizing Graph: "
            +g.getNumNodes()+" nodes, "+g.getNumEdges()+" edges");
        
        ForceSimulator fsim = new ForceSimulator();
        fsim.addForce(new NBodyForce(-0.4f, 0.9f));
        fsim.addForce(new SpringForce(4E-5f, 100f));
        fsim.addForce(new DragForce(-0.005f));
        
        ForceDemo fdemo = new ForceDemo(g, fsim);
        fdemo.runDemo();
    } //

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
                Boolean f = (Boolean)item.getVizAttribute("fixed");
                if ( f != null && f.booleanValue() )
                    return pastelRed;
                else
                    return lightGray;
            } else {
                return Color.BLACK;
            }
        } //        
    } //
    
    /**
     * Tags and fixes the node under the mouse pointer.
     */
    public class MouseOverControl extends ControlAdapter {
        
        public void itemEntered(GraphItem item, MouseEvent e) {
            ((Display)e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            item.setVizAttribute("fixed", Boolean.TRUE);
        } //
        
        public void itemExited(GraphItem item, MouseEvent e) {
            ((Display)e.getSource()).setCursor(Cursor.getDefaultCursor());
            item.setVizAttribute("fixed", null);
        } //
        
        public void itemReleased(GraphItem item, MouseEvent e) {
            item.setVizAttribute("fixed", null);
        } //
        
    } // end of inner class FocusControl
    
} // end of class ForceDemo
