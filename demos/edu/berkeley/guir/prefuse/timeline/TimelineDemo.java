/*
 * Created on Jul 3, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefusex.distortion.FisheyeDistortion;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineDemo extends JFrame implements TimelineConstants {
    // (( CONSTANTS )) \\
    private static final String MUSIC_HISTORY = "etc/musichistory.xml";
    private static final String TITLE = "The History of Music";
    private static final int NUM_DIVISIONS = 0, NOTCH_LENGTH = 1;

    
    // (( FIELDS )) \\
    private final int appWidth = 1000;
    private final int appHeight = 800; // this should be more fixed than the width
    private final int divisionSpecification =
        //NOTCH_LENGTH;
        NUM_DIVISIONS;
    private final int timeline_start = 0;
    private final int timeline_end = 2005;
    private final int timelineSpan = timeline_end - timeline_start;

    private final int timelineLength = appWidth * 9 / 10; // this factor ought to be
                                                 // shared across all
                                                 // timeline instances
    private final double yearPerPixel = (double) timelineSpan / timelineLength;
    
    private Graph graph;

    
    // (( CONSTRUCTOR )) \\
    public TimelineDemo() {
        // 1a. Load the timeline data into graph
        try {
            //graph = new XMLGraphReader().loadGraph(MUSIC_HISTORY);
            graph = new /*Timeline*/XMLGraphReader().loadGraph(MUSIC_HISTORY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 1b. Add notch nodes into the graph
        final double notchLength;
        Node prevNotchNode = null;
        int nextNotchNum = timeline_start;
        prevNotchNode = connectNewNotchNode(START, prevNotchNode, nextNotchNum); // start
                                                                                 // node
        int notchIndex = 1; // (0 is time_start, taken care of in the line
                            // before)
        if (divisionSpecification == NUM_DIVISIONS) {
            final int numDivisions = 20; // entered desired division in this
                                         // mode
            notchLength = timelineSpan / numDivisions;
            for (; notchIndex < numDivisions; notchIndex++) {
                nextNotchNum = (int) ((notchIndex * notchLength) + timeline_start);
                prevNotchNode = connectNewNotchNode("" + notchIndex,
                        prevNotchNode, nextNotchNum);
            }
        } else if (divisionSpecification == NOTCH_LENGTH) {
            notchLength = 311; // entered desired length in this mode
            nextNotchNum += notchLength;
            for (; nextNotchNum < timeline_end; nextNotchNum += notchLength) {
                //notchIndex = (nextNotchNum - timeline_start) / notchLength;
                prevNotchNode = connectNewNotchNode("" + notchIndex,
                        prevNotchNode, nextNotchNum);
                notchIndex++;
            }
        }
        connectNewNotchNode(END, prevNotchNode, timeline_end); // end node

        // 2. Create a new item registry
        final ItemRegistry registry = new ItemRegistry(graph, false);
        //registry.addItemClass(NOTCH_NODE_TYPE, NOTCH_NODE_ITEM_CLASS); // order matters
        //registry.addItemClass(NOTNOTCH_NODE_TYPE, NOTNOTCH_NODE_ITEM_CLASS);
        // XXX Gotta switch back later (comment out)
        registry.addItemClass(XMLGraphReader.XMLGraphHandler.NODE, NodeItem.class);
        registry.addItemClass(ItemRegistry.DEFAULT_EDGE_CLASS, EdgeItem.class);
        final TextItemRenderer nodeRenderer = new TimelineDataRenderer(
        		timeline_start, timeline_end, timelineLength);
        //nodeRenderer.setVerticalAlignment(TextItemRenderer.ALIGNMENT_CENTER);
        nodeRenderer.setHorizontalAlignment(TextItemRenderer.ALIGNMENT_CENTER);
		registry.setRendererFactory(new DefaultRendererFactory(
                nodeRenderer, new DefaultEdgeRenderer(), null));

        final Display display = new Display(registry);
        display.setUseCustomTooltips(true);
        display.setSize(appWidth, appHeight);
        
        display.addControlListener(new YAxisDragControl());
/*        display.addControlListener(new ControlAdapter() {
            private Point2D  m_tmp = new Point2D.Float();
            
            public void mouseMoved(MouseEvent e) {
                final Display d = (Display)e.getSource();
                d.getAbsoluteCoordinate(e.getPoint(), m_tmp);
                d.setToolTipText(""+getYear(m_tmp.getX()));
            } //
            
        });*/
        
        final ActionList distort = new ActionList(registry);
        distort.add(new /*Timeline*/GraphFilter());
        final FisheyeDistortion feye = new FisheyeDistortion(1,0,true);//NOTCH_NODE_TYPE);
        distort.add(feye);
        distort.add(new ResizeBoundsFunction(feye));
        distort.add(new MusicHistoryColorFunction());
        distort.add(new RepaintAction());
        
        // enable distortion mouse-over: should be only
        final TimelineInteractionListener mouseOverUpdates = 
            new TimelineInteractionListener(feye, distort, 10, timelineSpan, appWidth, timelineLength);
        display.addMouseListener(mouseOverUpdates);
        display.addMouseMotionListener(mouseOverUpdates);

        // set up this JFrame
        final JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(display);
        frame.pack();
        frame.setVisible(true);

        final ActionList initialArrange = new ActionList(registry);
        initialArrange.add(new GraphFilter());
        initialArrange.add(new MusicHistoryColorFunction());
        initialArrange.add(new MusicHistoryLayout(timelineLength, notchIndex));
        initialArrange.add(new SetBoundsFunction()); // after layout
        initialArrange.add(new RepaintAction());
        initialArrange.runNow();
    }

    
    // (( METHODS )) \\
    private int getYear(final double xCoord) {
        //return (int) ((xCoord - (appWidth / 8)) * timelineSpan / timelineLength);
        return ((8 * (int) xCoord * timelineSpan) - (timelineSpan * appWidth)) / (8 * timelineLength);
    }
    
    private Node connectNewNotchNode(final String notchIndex,
            final Node prevNotchNode, final int nextNotchNum) {
        final Node nextNotchNode = new /*NotchNode();*/DefaultNode();
        nextNotchNode.setAttribute(NODE_TYPE, NOTCH_TYPE);
        nextNotchNode.setAttribute(
        		XMLGraphReader.XMLGraphHandler.ID, NOTCH+notchIndex);
        nextNotchNode.setAttribute(
        		XMLGraphReader.XMLGraphHandler.LABEL, ""+nextNotchNum);
        graph.addNode(nextNotchNode);

        if (prevNotchNode != null) { // the first node doesn't have an in-edge
            graph.addEdge(new DefaultEdge(prevNotchNode, nextNotchNode));
        }

        return nextNotchNode;
    }

    
    // (( INNER CLASSES )) \\
    private static class MusicHistoryColorFunction extends ColorFunction {
        private Color pastelOrange = new Color(255,200,125);

        public Paint getColor(VisualItem item) {
            if ( item instanceof NodeItem ) {
                if ( item.isHighlighted() )
                    return pastelOrange;
                else
                    return Color.BLACK;//Color.LIGHT_GRAY;
            } else {
                return Color.BLACK;
            }
        }
        
        public Paint getFillColor(VisualItem item) {
            if (item instanceof NodeItem) {
                final String nodeType = item.getAttribute(NODE_TYPE);
                if (nodeType.equals(PERIOD_TYPE)) {
                    return Color.BLUE;
                } else if (nodeType.equals(EVENT_TYPE)) {
                    return Color.RED;
                } else if (nodeType.equals(PERSON_TYPE)) {
                    return Color.GRAY;
                } else if (nodeType.equals(PIECE_TYPE)) {
                    return Color.DARK_GRAY;
                } else {
                    return Color.MAGENTA;
                }
            } else {
                return super.getFillColor(item);
            }
        }
    } // end of class MusicHistoryColorFunction

    private class MusicHistoryLayout extends TimelineLayout {
        /**
         * @param timelineLength
         * @param numDivisions
         */
        public MusicHistoryLayout(final int m_timelineLength,
                final int m_numDivisions) {
            super(m_timelineLength, m_numDivisions);
        }

        private double getNodePosition(final int startYear, final int endYear, final double leftOffset) {
            final int yearsFromLeft = startYear - timeline_start;
            final double centerCorrection = ((double) endYear - startYear) / (timeline_end - timeline_start) * m_timelineLength / 2;
            final double fractionFromLeft = (double) yearsFromLeft / timelineSpan; // horn or not?
            return leftOffset + centerCorrection + (fractionFromLeft * m_timelineLength);
        }

        public void run(final ItemRegistry registry, final double frac) {
            final Iterator nodeItems = registry.getNodeItems();
            final Dimension displaySize = registry.getDisplay(0).getSize();
            final double leftOffset = (displaySize.getWidth() - m_timelineLength) / 2;
            final Rectangle2D bounds = getLayoutBounds(registry);
            VisualItem node;
            while (nodeItems.hasNext()) {
                node = (VisualItem) nodeItems.next();
                if (node.getAttribute(NODE_TYPE).equals(NOTCH_TYPE)) {
                    final String notchIndexString = getNotchIndex(node
                            .getAttribute(XMLGraphReader.XMLGraphHandler.ID));
                    final double y = displaySize.getHeight() / 3;
                    final double x;
                    if (notchIndexString.equals(START)) {
                        x = leftOffset;
                        //node.setFixed(true);
                    } else if (notchIndexString.equals(END)) {
                        x = leftOffset + m_timelineLength;
                        //node.setFixed(true);
                    } else { // a regular notch node
                        final int notchIndex = new Integer(notchIndexString)
                                .intValue();
                        x = leftOffset
                                + ((double) notchIndex / m_numDivisions * m_timelineLength);
                    }
                    setLocation(node, null, x, y);
                } else { // regular data node
                    final String startYearString = node.getAttribute(START_YEAR);
                    final String endYearString = node.getAttribute(END_YEAR);

                    final int startYear, endYear;
                    if (startYearString.equals(TIMELINE_START)) {
                    	startYear = timeline_start;
                    } else {
                    	startYear = new Integer(startYearString).intValue(); // whoever gets children's hsopital
                    }
                    if (endYearString.equals(TIMELINE_END)) {
                    	endYear = timeline_end;
                    }
                    else {
                    	endYear = new Integer(endYearString).intValue();
                    }
                    final double x = getNodePosition(startYear, endYear, leftOffset);
                    final double yOffset;
                    final String nodeType = node.getAttribute(NODE_TYPE);
                    if (nodeType.equals(PERIOD_TYPE)) {
                        yOffset = 1.0 / 6;
                    } else if (nodeType.equals(EVENT_TYPE)) {
                        yOffset = 2.0 / 5;
                    } else if (nodeType.equals(PERSON_TYPE)) {
                        yOffset = 3.0 / 5;
                    } else if (nodeType.equals(PIECE_TYPE)) {
                        yOffset = 4.0 / 5;
                    } else {
                        yOffset = 1.0;
                    }
                    //final double y = bounds.getY() + Math.random()*bounds.getHeight();
                    final double y = bounds.getY() + yOffset * bounds.getHeight()
                    		+ Math.random() / 8 * bounds.getHeight();
                    setLocation(node, null, x, y);
                }
            }
        }
    } // end of class MusicHistoryLayout

    
    // (( MAIN )) \\
    public static void main(String[] args) {
        new TimelineDemo();
    }
} // end of class TimelineDemo
