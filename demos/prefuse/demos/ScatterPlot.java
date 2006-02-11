package prefuse.demos;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLayout;
import prefuse.controls.ToolTipControl;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.query.RangeQueryBinding;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JRangeSlider;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.VisiblePredicate;

/**
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ScatterPlot extends JPanel {

    private static final String group = "data";
    
    private Visualization m_vis;
    private Display m_display;
    private ShapeRenderer m_shapeR = new ShapeRenderer(2);
    
    public ScatterPlot(Table t, String xfield, String yfield) {
        super(new BorderLayout());
        
        // --------------------------------------------------------------------
        // STEP 1: setup the visualized data
        
        m_vis = new Visualization();
        VisualTable vt = m_vis.addTable(group, t);
        
        DefaultRendererFactory rf = new DefaultRendererFactory(m_shapeR);
        m_vis.setRendererFactory(rf);
        
        // --------------------------------------------------------------------
        // STEP 2: create actions to process the visual data

        // set up dynamic queries, search set
        RangeQueryBinding  xaxisQ = new RangeQueryBinding(vt, xfield);
        RangeQueryBinding  yaxisQ = new RangeQueryBinding(vt, yfield);
        
        // construct the filtering predicate
        AndPredicate filter = new AndPredicate(xaxisQ.getPredicate(),
                                               yaxisQ.getPredicate());
        
        // set up the actions
        AxisLayout x_axis = new AxisLayout(group, xfield, 
                Constants.X_AXIS, VisiblePredicate.TRUE);
        x_axis.setRangeModel(xaxisQ.getModel());
        
        AxisLayout y_axis = new AxisLayout(group, yfield, 
                Constants.Y_AXIS, VisiblePredicate.TRUE);
        y_axis.setRangeModel(yaxisQ.getModel());

        ColorAction color = new ColorAction(group, 
                VisualItem.FILLCOLOR, ColorLib.rgb(100,100,255));
        
        ActionList draw = new ActionList();
        draw.add(x_axis);
        draw.add(y_axis);
        draw.add(color);
        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        ActionList update = new ActionList();
        update.add(new VisibilityFilter(group, filter));
        update.add(x_axis);
        update.add(y_axis);
        update.add(new RepaintAction());
        m_vis.putAction("update", update);
        
        UpdateListener lstnr = new UpdateListener() {
            public void update(Object src) {
                m_vis.run("update");
            }
        };
        filter.addExpressionListener(lstnr);
        
        // --------------------------------------------------------------------
        // STEP 3: set up a display and ui components to show the visualization

        m_display = new Display(m_vis);
        m_display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        m_display.setSize(700,450);
        m_display.setHighQuality(true);
        
        ToolTipControl ttc = new ToolTipControl(new String[] {xfield,yfield});
        m_display.addControlListener(ttc);
        
        
        // --------------------------------------------------------------------        
        // STEP 4: launching the visualization
        
        this.addComponentListener(lstnr);
        
        JRangeSlider xslider = xaxisQ.createHorizontalRangeSlider();
        JRangeSlider yslider = yaxisQ.createVerticalRangeSlider();
        
        xslider.setThumbColor(null);
        yslider.setThumbColor(null);
        
        MouseAdapter qualityControl = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                m_display.setHighQuality(false);
            }
            public void mouseReleased(MouseEvent e) {
                m_display.setHighQuality(true);
                m_display.repaint();
            }
        };
        xslider.addMouseListener(qualityControl);
        yslider.addMouseListener(qualityControl);
        
        m_vis.run("draw");
        
        add(m_display, BorderLayout.CENTER);
        add(yslider, BorderLayout.EAST);
        
        Box xbox = new Box(BoxLayout.X_AXIS);
        xbox.add(xslider);
        int corner = yslider.getPreferredSize().width;
        xbox.add(Box.createHorizontalStrut(corner));
        add(xbox, BorderLayout.SOUTH);
    }
    
    public int getPointSize() {
        return m_shapeR.getBaseSize();
    }
    
    public void setPointSize(int size) {
        m_shapeR.setBaseSize(size);
        repaint();
    }
    
    public Display getDisplay() {
        return m_display;
    }
    
} // end of class ScatterPlot
