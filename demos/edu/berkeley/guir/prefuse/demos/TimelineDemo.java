/*
 * Created on Jul 3, 2004
 */
package edu.berkeley.guir.prefuse.demos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.Display;
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
import edu.berkeley.guir.prefuse.timeline.TimelineConstants;
import edu.berkeley.guir.prefuse.timeline.TimelineDataRenderer;
import edu.berkeley.guir.prefuse.timeline.TimelineLayout;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

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
    private Graph graph;

    // (( CONSTRUCTOR )) \\
    public TimelineDemo() {
        // 1a. Load the timeline data into graph
        try {
            graph = new XMLGraphReader().loadGraph(MUSIC_HISTORY);
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
            final int numDivisions = 12; // entered desired division in this
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
        final ItemRegistry registry = new ItemRegistry(graph);
        final int timelineLength = appWidth * 3 / 4; // this factor ought to be
                                                     // shared across all
                                                     // timeline instances
        registry.setRendererFactory(new DefaultRendererFactory(
                new TimelineDataRenderer(timeline_start, timeline_end,
                        timelineLength), new DefaultEdgeRenderer(), null));

        final Display display = new Display(registry);
        display.setSize(appWidth, appHeight);
        display.addControlListener(new DragControl());

        // set up this JFrame
        final JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(display);
        frame.pack();
        frame.setVisible(true);

        final ActionList actions = new ActionList(registry);
        actions.add(new GraphFilter());
        actions.add(new MusicHistoryColorFunction());
        actions.add(new RandomLayout()); // change to timeline layout,
                                         // musictimelinelayout
        //actions.add(new TimelineLayout(timelineLength, notchIndex)); // the
        // final notchIndex is the number of divisions
        actions.add(new MusicHistoryLayout(timelineLength, notchIndex));
        actions.add(new RepaintAction());
        actions.runNow();
    }

    // (( METHODS )) \\
    private Node connectNewNotchNode(final String notchIndex,
            final Node prevNotchNode, final int nextNotchNum) {
        final DefaultNode nextNotchNode = new DefaultNode();
        nextNotchNode.setAttribute(XMLGraphReader.XMLGraphHandler.ID, NOTCH
                + notchIndex);
        nextNotchNode.setAttribute(XMLGraphReader.XMLGraphHandler.LABEL, ""
                + nextNotchNum);
        nextNotchNode.setAttribute(NODE_TYPE, NOTCH_TYPE);
        graph.addNode(nextNotchNode);

        if (prevNotchNode != null) { // the first node doesn't have an in-edge
            graph.addEdge(new DefaultEdge(prevNotchNode, nextNotchNode));
        }

        return nextNotchNode;
    }

    // (( INNER CLASSES )) \\
    private static class MusicHistoryColorFunction extends ColorFunction {
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
            }
            return super.getFillColor(item);
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

        private double getNodePosition(final int year, final double leftOffset) {
            final int yearsFromLeft = year - timeline_start;
            final double fractionFromLeft = (double) yearsFromLeft
                    / timelineSpan;
            return leftOffset + (fractionFromLeft * m_timelineLength);
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
                    } else if (notchIndexString.equals(END)) {
                        x = leftOffset + m_timelineLength;
                    } else { // a regular notch node
                        final int notchIndex = new Integer(notchIndexString)
                                .intValue();
                        x = leftOffset
                                + ((double) notchIndex / m_numDivisions * m_timelineLength);
                    }
                    setLocation(node, null, x, y);
                } else {
                    final String startYear = node.getAttribute(START_YEAR);
                    final double x;
                    if (startYear.equals(TIMELINE_START)) {
                        x = getNodePosition(timeline_start, leftOffset);
                    } else {
                        System.out.println(startYear);
                        x = getNodePosition(new Integer(startYear).intValue(),
                                leftOffset);
                    }
                    final double yOffset;
                    final String nodeType = node.getAttribute(NODE_TYPE);
                    if (nodeType.equals(PERIOD_TYPE)) {
                        yOffset = 1 / 5;
                    } else if (nodeType.equals(EVENT_TYPE)) {
                        yOffset = 2 / 5;
                    } else if (nodeType.equals(PERSON_TYPE)) {
                        yOffset = 3 / 5;
                    } else if (nodeType.equals(PIECE_TYPE)) {
                        yOffset = 4 / 5;
                    } else {
                        yOffset = 1;
                    }
                    final Rectangle2D b = getLayoutBounds(registry);
                    final double y = b.getY() + Math.random()*b.getHeight();
                    //final double y = bounds.getY() - yOffset * bounds.getHeight();//
                    		//+ Math.random() / 10 * bounds.getHeight(); // make y random for now
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
