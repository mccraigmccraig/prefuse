/*
 * Created on Jul 22, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineInteractionListener extends ControlAdapter implements TimelineConstants {
    // (( FIELDS )) \\
    private Layout[] m_layouts;
    private Activity m_activity;
    private Point2D  m_tmp = new Point2D.Float();
    private final double highlightThresh;
    private final int timelineSpan, appWidth, timelineLength;
    
    // (( CONSTRUCTORS )) \\
    public TimelineInteractionListener(Layout layout, final double highlightThresh,
            final int timelineSpan, final int appWidth, final int timelineLength) {
        this(layout, null, highlightThresh, timelineSpan, appWidth, timelineLength);
    } //
    
    public TimelineInteractionListener(Layout layout, Activity update, 
            final double highlightThresh, final int timelineSpan, final int appWidth, final int timelineLength) {
        this(new Layout[] {layout}, update, highlightThresh, timelineSpan, appWidth, timelineLength);
    } //
    
    public TimelineInteractionListener(Layout[] layout, Activity update, 
            final double highlightThresh, final int timelineSpan, final int appWidth, final int timelineLength) {
        m_layouts = (Layout[])layout.clone();
        m_activity = update;
        this.highlightThresh = highlightThresh;
        this.timelineSpan = timelineSpan;
        this.appWidth = appWidth;
        this.timelineLength = timelineLength;
    } //
    
    
    // (( METHODS )) \\
    public void mouseExited(MouseEvent e) {
        for ( int i=0; i<m_layouts.length; i++ ) 
            m_layouts[i].setLayoutAnchor(null);
        if ( m_activity != null )
            m_activity.runNow();
    } //
    
    public void mouseMoved(MouseEvent e) {
        moveEvent(e);
    } //
    
    public void mouseDragged(MouseEvent e) {
        moveEvent(e);
    } //
    
    public void moveEvent(MouseEvent e) {
        Display d = (Display)e.getSource();
        d.getAbsoluteCoordinate(e.getPoint(), m_tmp);
        d.setToolTipText(""+getYear(m_tmp.getX()));
        for ( int i=0; i<m_layouts.length; i++ ) {
            m_layouts[i].setLayoutAnchor(m_tmp);
            // highlight nearby nodes
            highlighNearbyNodes(d, m_tmp.getX());
        }
        if ( m_activity != null )
            m_activity.runNow();
    } //
    
    private int getYear(final double xCoord) {
        //return (int) ((xCoord - (appWidth / 8)) * timelineSpan / timelineLength);
        return ((8 * (int) xCoord * timelineSpan) - (timelineSpan * appWidth)) / (8 * timelineLength);
    }
    

    // need function: draws line from given node (compute center) to given x (find y)
    // for all nodes, compute if the node is nearby
    // draw lines from nearby nodes to that point on the timeline
    private void highlighNearbyNodes(final Display display, final double mouseXCoord) {
        // get the registry
        final ItemRegistry registry = display.getRegistry();
        synchronized ( registry ) {
            Iterator nodeItemIter = registry.getNodeItems();
            while ( nodeItemIter.hasNext() ) {
                NodeItem nitem = (NodeItem)nodeItemIter.next();
                if ( ! nitem.getAttribute(NODE_TYPE).equals(NOTCH_TYPE) ) {
                    if (isNearby(nitem, mouseXCoord)) {
                        nitem.setHighlighted(true);
                        registry.touch(nitem.getItemClass()); //?
                    } else {
                        nitem.setHighlighted(false);
                        registry.touch(nitem.getItemClass()); //?
                    }
                }
            }
        }
        if ( m_activity != null )
            m_activity.runNow();
    } //
    
    private boolean isNearby(final NodeItem nitem, final double mouseXCoord) {
        final Rectangle2D nodeBounds = nitem.getBounds();
        double leftBound = nodeBounds.getMinX();
        double rightBound = nodeBounds.getMaxX();
        final double nodeWidth = rightBound - leftBound;
        if (nodeWidth < highlightThresh) {
            leftBound -= (highlightThresh - nodeWidth) / 2;
            rightBound += (highlightThresh - nodeWidth) / 2;
        }
        if (mouseXCoord <= leftBound || mouseXCoord >= rightBound) {
            return true;
        } else {
            return false;
        }
    }
        
} // end of class AnchorUpdateControl

