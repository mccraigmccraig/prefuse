package edu.berkeley.guir.prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.Action;
import edu.berkeley.guir.prefuse.action.ActionMap;
import edu.berkeley.guir.prefuse.action.ActionSwitch;
import edu.berkeley.guir.prefuse.action.ColorFunction;
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
import edu.berkeley.guir.prefusex.distortion.BifocalDistortion;
import edu.berkeley.guir.prefusex.distortion.FisheyeDistortion;

/**
 * Demonstration illustrating the use of distortion transformations on
 *  a visualization.
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

        Display display = new Display();
        display.setRegistry(registry);
        display.setSize(600,600);
        display.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        display.addControlListener(new DragControl(false));
        
        registry.setRendererFactory(new DefaultRendererFactory(
            new DefaultNodeRenderer() {
                public int getRenderType() {
                    return RENDER_TYPE_FILL;
                } //
            }, 
            new DefaultEdgeRenderer(), 
            null));
        
        ActionPipeline filter = new ActionPipeline(registry);
        filter.add(new GraphNodeFilter());
        filter.add(new GraphEdgeFilter());
        filter.add(new ColorFunction()); // make everything black
        filter.add(new GridLayout());
        filter.add(new RepaintAction());
        
        ActionPipeline distort = new ActionPipeline(registry);
        Action[] acts = new Action[] {
            actionMap.put("distort1",new BifocalDistortion()),
            actionMap.put("distort2",new FisheyeDistortion())
        };
        distort.add(actionMap.put("switch",new ActionSwitch(acts, 0)));
        distort.add(new RepaintAction());
        activityMap.put("distortion",distort);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(display, BorderLayout.CENTER);
        getContentPane().add(new SwitchPanel(), BorderLayout.SOUTH);
        pack();
        setVisible(true);
        
        // wait until graphics are available
        while ( display.getGraphics() == null );
        ActivityManager.scheduleNow(filter);
        
        // enable distortion mouse-over
        DistortionController dc = new DistortionController();
        display.addMouseListener(dc);
        display.addMouseMotionListener(dc);
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
                if ( nd.getEdgeCount() == 2 )
                    break;
            }
            m = g.getNodeCount() / n;
            iter = g.getNodes();
            for ( int i=0; iter.hasNext(); i++ ) {
                Node nd = (Node)iter.next();
                NodeItem ni = registry.getNodeItem(nd);
                double x = bx + w*((i%n)/(double)(n-1));
                double y = by + h*((i/n)/(double)(m-1));
                ni.updateLocation(x,y);
                ni.setLocation(x,y);
            }
        } //
    } // end of inner class GridLayout
    
    class DistortionController extends MouseAdapter implements MouseMotionListener {
        Point2D tmp = new Point2D.Float();
        public void mouseExited(MouseEvent e) {
            ((Layout)actionMap.get("distort1")).setLayoutAnchor(null);
            ((Layout)actionMap.get("distort2")).setLayoutAnchor(null);
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
            ((Layout)actionMap.get("distort1")).setLayoutAnchor(tmp);
            ((Layout)actionMap.get("distort2")).setLayoutAnchor(tmp);
            activityMap.scheduleNow("distortion");
        } //
    } // end of inner class DistortionController
    
    class SwitchPanel extends JPanel implements ActionListener {
        public static final String BIFOCAL = "Bifocal";
        public static final String FISHEYE = "Fisheye";
        public SwitchPanel() {
            setBackground(Color.WHITE);
            initUI();
        } //
        private void initUI() {
            JRadioButton bb = new JRadioButton(BIFOCAL);
            JRadioButton fb = new JRadioButton(FISHEYE);
            bb.setActionCommand(BIFOCAL);
            fb.setActionCommand(FISHEYE);
            bb.setSelected(true);
            
            bb.setBackground(Color.WHITE);
            fb.setBackground(Color.WHITE);
            
            Font f = new Font("SanSerif",Font.PLAIN,24);
            bb.setFont(f);
            fb.setFont(f);
            
            bb.addActionListener(this);
            fb.addActionListener(this);
            
            ButtonGroup bg = new ButtonGroup();
            bg.add(bb); this.add(bb);
            this.add(Box.createHorizontalStrut(50));
            bg.add(fb); this.add(fb);
        } //
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if ( BIFOCAL == cmd ) {
                ((ActionSwitch)actionMap.get("switch")).setSwitchValue(0);
                activityMap.scheduleNow("distortion");
            } else if ( FISHEYE == cmd ) {
                ((ActionSwitch)actionMap.get("switch")).setSwitchValue(1);
                activityMap.scheduleNow("distortion");
            }
        } //
    } // end of inner class SwitchPanel
    
} // end of class DistortionDemo
