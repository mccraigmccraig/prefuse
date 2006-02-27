package prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
import prefuse.controls.ControlAdapter;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.AbstractShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.FontLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTree;
import prefuse.visual.sort.TreeDepthItemSorter;


/**
 * Demonstration showcasing a TreeMap layout of a hierarchical data
 * set and the use of dynamic query binding for text search. Animation
 * is used to highlight changing search results.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TreeMap extends Display {

    public static final String TREE_CHI = "/chi-ontology.xml.gz";
    
    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";

    private SearchQueryBinding searchQ;
    
    public TreeMap(Tree t, String label) {
        super(new Visualization());
        
        VisualTree vt = m_vis.addTree(tree, t);
        m_vis.setVisible(treeEdges, null, false);
            
        m_vis.setRendererFactory(
            new DefaultRendererFactory(new TreeMapRenderer(label)));
                       
        // border colors
        final ColorAction borderColor = new BorderColorAction(treeNodes);
        final ColorAction fillColor = new FillColorAction(treeNodes);
        
        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(fillColor);
        fullPaint.add(borderColor);
        m_vis.putAction("fullPaint", fullPaint);
        
        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator());
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);
        
        // create the single filtering and layout action list
        ActionList layout = new ActionList();
        layout.add(new SquarifiedTreeMapLayout(tree));
        layout.add(fillColor);
        layout.add(borderColor);
        layout.add(new RepaintAction());
        m_vis.putAction("layout", layout);
        
        // initialize our display
        setSize(700,600);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
                item.setStrokeColor(borderColor.getColor(item));
                item.getVisualization().repaint();
            }
            public void itemExited(VisualItem item, MouseEvent e) {
                item.setStrokeColor(item.getEndStrokeColor());
                item.getVisualization().repaint();
            }           
        });
        
        searchQ = new SearchQueryBinding(vt.getNodeTable(), label);
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchQ.getSearchSet());
        searchQ.getPredicate().addExpressionListener(new UpdateListener() {
            protected void update(Object src) {
                m_vis.cancel("animatePaint");
                m_vis.run("fullPaint");
                m_vis.run("animatePaint");
            }
        });
        
        // perform layout
        m_vis.run("layout");
    }
    
    public SearchQueryBinding getSearchQuery() {
        return searchQ;
    }
    
    public static void main(String argv[]) {
        UILib.setPlatformLookAndFeel();
        
        
        String infile = TREE_CHI;
        String label = "name";
        if ( argv.length > 1 ) {
            infile = argv[0];
            label = argv[1];
        }
        JComponent treemap = demo(infile, label);
        
        JFrame frame = new JFrame("p r e f u s e  |  t r e e m a p");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(treemap);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static JComponent demo() {
        return demo(TREE_CHI, "name");
    }
    
    public static JComponent demo(String datafile, final String label) {
        Tree t = null;
        try {
            t = (Tree)new TreeMLReader().readGraph(datafile);
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(1);
        }
        
        // create a new treemap
        final TreeMap treemap = new TreeMap(t, label);
        
        // create a search panel for the tree map
        JSearchPanel search = treemap.getSearchQuery().createSearchPanel();
        search.setShowResultCount(true);
        search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
        search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
        
        final JFastLabel title = new JFastLabel("                 ");
        title.setPreferredSize(new Dimension(350, 20));
        title.setVerticalAlignment(SwingConstants.BOTTOM);
        title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
        
        treemap.addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
                title.setText(item.getString(label));
            }
            public void itemExited(VisualItem item, MouseEvent e) {
                title.setText(null);
            }
        });
        
        Box box = UILib.getBox(new Component[]{title,search}, true, 10, 3, 0);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(treemap, BorderLayout.CENTER);
        panel.add(box, BorderLayout.SOUTH);
        UILib.setColor(panel, Color.BLACK, Color.GRAY);
        return panel;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Set the stroke color for drawing treemap node outlines. A graded
     * grayscale ramp is used, with higer nodes in the tree drawn in
     * lighter shades of gray.
     */
    public static class BorderColorAction extends ColorAction {
        
        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }
        
        public int getColor(VisualItem item) {
            NodeItem nitem = (NodeItem)item;
            if ( nitem.isHover() )
                return ColorLib.rgb(99,130,191);
            
            int depth = nitem.getDepth();
            if ( depth < 2 ) {
                return ColorLib.gray(100);
            } else if ( depth < 4 ) {
                return ColorLib.gray(75);
            } else {
                return ColorLib.gray(50);
            }
        }
    }
    
    /**
     * Set fill colors for treemap nodes. Search items are colored
     * in pink, while normal nodes are shaded according to their
     * depth in the tree.
     */
    public static class FillColorAction extends ColorAction {
        private ColorMap cmap = new ColorMap(
            ColorLib.getInterpolatedPalette(10,
                ColorLib.rgb(85,85,85), ColorLib.rgb(0,0,0)), 0, 9);

        public FillColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }
        
        public int getColor(VisualItem item) {
            if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS) )
                return ColorLib.rgb(191,99,130);
            
            double v = (item instanceof NodeItem ? ((NodeItem)item).getDepth():0);
            return cmap.getColor(v);
        }
        
    } // end of inner class TreeMapColorAction
    
    /**
     * A renderer for treemap nodes. Draws simple rectangles, but defers
     * the bounds management to the layout. Leaf nodes are drawn fully,
     * higher level nodes only have their outlines drawn. Labels are
     * rendered for top-level (i.e., depth 1) subtrees.
     */
    public static class TreeMapRenderer extends AbstractShapeRenderer {
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        private String m_label;
        
        public TreeMapRenderer(String label) {
            m_manageBounds = false;
            m_label = label;
        }
        public int getRenderType(VisualItem item) {
            if ( ((NodeItem)item).getChildCount() == 0 ) {
                // if a leaf node, both draw and fill the node
                return RENDER_TYPE_DRAW_AND_FILL;
            } else {
                // if not a leaf, only draw the node outline
                return RENDER_TYPE_DRAW;
            }
        }
        protected Shape getRawShape(VisualItem item) {
            m_bounds.setRect(item.getBounds());
            return m_bounds;
        }
        
        public void render(Graphics2D g, VisualItem item) {
            super.render(g, item);
            // if a top-level node, draw the category name
            if ( ((NodeItem)item).getDepth() == 1 ) {
                Rectangle2D b = item.getBounds();
                String s = item.getString(m_label);
                Font f = item.getFont();
                FontMetrics fm = g.getFontMetrics(f);
                int w = fm.stringWidth(s);
                int h = fm.getAscent();
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(s, (float)(b.getCenterX()-w/2.0),
                                (float)(b.getCenterY()+h/2));
            }
        }
        
    } // end of inner class NodeRenderer
    
} // end of class TreeMap
