package edu.berkeley.guir.prefusex.layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.Layout;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceItem;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;

/**
 * Layout algorithm that positions graph elements based on a physics
 * simulation of interacting forces (e.g., anti-gravity, spring forces,
 * drag forces, etc).
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class ForceDirectedLayout extends Layout {

    private ForceSimulator m_fsim;
    private ItemRegistry registry;
    private long m_lasttime = -1L;
    private int m_preruns = 0;
    private boolean m_enforceBounds;
    
    public ForceDirectedLayout(boolean enforceBounds) {
        m_enforceBounds = enforceBounds;
        m_fsim = new ForceSimulator();
        m_fsim.addForce(new NBodyForce());
        m_fsim.addForce(new SpringForce());
        m_fsim.addForce(new DragForce());
    } //
    
    public ForceDirectedLayout(ForceSimulator fsim, boolean enforceBounds) {
        m_enforceBounds = enforceBounds;
        m_fsim = fsim;
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
    public void run(ItemRegistry registry, double frac) {
        this.registry = registry;
        // first time running through ?
//        if ( m_lasttime == -1 ) {
//            m_lasttime = System.currentTimeMillis();
//            Point2D anchor = getAnchor();
//            Iterator iter = registry.getNodeItems();
//            while ( iter.hasNext() ) {
//                NodeItem  nitem = (NodeItem)iter.next();
//                nitem.setLocation(anchor);
//            }
//            for ( int i = 0; i < m_preruns; i++ ) {
//                initSimulator();
//                m_fsim.runSimulator(50);
//                updateNodePositions();
//            }
//        }
        
        // get timestep
        if ( m_lasttime == -1 )
            m_lasttime = System.currentTimeMillis()-20;
        long time = System.currentTimeMillis();
        long timestep = time - m_lasttime;
        m_lasttime = time;
        
        // run force simulator
        initSimulator();
        m_fsim.runSimulator(timestep);
        updateNodePositions();
        this.registry = null;
    } //

    private void updateNodePositions() {
        // update positions
        Iterator iter = registry.getNodeItems();
        while ( iter.hasNext() ) {
            NodeItem  nitem = (NodeItem)iter.next();
            if ( nitem.isFixed() )
                continue;
            
            ForceItem fitem = (ForceItem)nitem.getVizAttribute("forceItem");
            double x = fitem.location[0];
            double y = fitem.location[1];
            
            if ( m_enforceBounds ) {
                Rectangle2D bounds = getLayoutBounds(registry);
                double x1 = bounds.getMinX(), y1 = bounds.getMinY();
                double x2 = bounds.getMaxX(), y2 = bounds.getMaxY();
                if ( x > x2 ) x = x2;
                if ( x < x1 ) x = x1;
                if ( y > y2 ) y = y2;
                if ( y < y1 ) y = y1;
            }
            nitem.updateLocation(x,y);
            nitem.setLocation(x,y);
        }
    } //
    
    public void reset(ItemRegistry registry) {
        Iterator iter = registry.getNodeItems();
        while ( iter.hasNext() ) {
            NodeItem nitem = (NodeItem)iter.next();
            ForceItem fitem = (ForceItem)nitem.getVizAttribute("forceItem");
            if ( fitem != null ) {
                fitem.location[0] = (float)nitem.getEndLocation().getX();
                fitem.location[1] = (float)nitem.getEndLocation().getY();
                fitem.force[0]    = fitem.force[1]    = 0;
                fitem.velocity[0] = fitem.velocity[1] = 0;
            }
        }
        m_lasttime = -1L;
    } //
    
    private void initSimulator() {
       m_fsim.clear();
       Iterator iter = registry.getNodeItems();
       while ( iter.hasNext() ) {
           NodeItem nitem = (NodeItem)iter.next();
           ForceItem fitem = (ForceItem)nitem.getVizAttribute("forceItem");
           if ( fitem == null ) {
               fitem = new ForceItem();
               nitem.setVizAttribute("forceItem", fitem);
           }
           fitem.location[0] = (float)nitem.getEndLocation().getX();
           fitem.location[1] = (float)nitem.getEndLocation().getY();
           m_fsim.addItem(fitem);
       }
       iter = registry.getEdgeItems();
       while ( iter.hasNext() ) {
           EdgeItem e = (EdgeItem)iter.next();
           NodeItem n1 = e.getFirstNode();
           ForceItem f1 = (ForceItem)n1.getVizAttribute("forceItem");
           NodeItem n2 = e.getSecondNode();
           ForceItem f2 = (ForceItem)n2.getVizAttribute("forceItem");
           m_fsim.addSpring(f1, f2);
       }      
    } //
    
} // end of class ForceDirectedLayout