/*
 * Created on Jul 3, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ResizeBoundsFunction;
import edu.berkeley.guir.prefuse.action.assignment.SetBoundsFunction;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefusex.controls.MultiSelectFocusControl;
import edu.berkeley.guir.prefusex.controls.YAxisDragControl;
import edu.berkeley.guir.prefusex.distortion.FisheyeDistortion;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineDemo extends JFrame implements TimelineConstants {
    // (( CONSTANTS )) \\
    private static final String MUSIC_HISTORY = "etc/musichistory.xml";
    private static final String TITLE = "The History of Music";
    private static final int NUM_DIVISIONS = 0, NOTCH_LENGTH = 1;

    
    // (( FIELDS )) \\\
    private final int appWidth = 1000;
    private final int appHeight = 800; // this should be more fixed than the width
    private final int divisionSpecification =
        //NOTCH_LENGTH;
        NUM_DIVISIONS;
    private final int timeline_start = 0;
    private final int timeline_end = 2005;
    private final int timelineSpan = timeline_end - timeline_start;
    private final ActionList initialArrange;

    private final int timelineLength = appWidth * 9 / 10; // this factor ought to be
                                                 // shared across all
                                                 // timeline instances
    private final double yearPerPixel = (double) timelineSpan / timelineLength;
    
    private Graph graph;

    
    // (( CONSTRUCTORS )) 
    
    public TimelineDemo() {
        // 1a. Load the timeline data into graph
        try {
            graph = new XMLGraphReader().loadGraph(MUSIC_HISTORY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1b. Add notch nodes into the graph
        final int numNotches = addNotchNodesToGraph();

        // 2. Create a new item registry
        final ItemRegistry registry = new ItemRegistry(graph, false);
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
        display.addControlListener(new MultiSelectFocusControl(registry));
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
        final TimelineGraphFilter filter = new TimelineGraphFilter();
        distort.add(filter);//new /*Timeline*/GraphFilter());
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
        final Container content = frame.getContentPane();
        content.add(new CheckBoxFilters(filter), BorderLayout.SOUTH);
		content.add(display, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        initialArrange = new ActionList(registry);
        initialArrange.add(new TimelineGraphFilter());
        initialArrange.add(new MusicHistoryColorFunction());
        initialArrange.add(new MusicHistoryLayout(timeline_start, timeline_end, timelineLength, numNotches));
        initialArrange.add(new SetBoundsFunction()); // after layout
        initialArrange.add(new RepaintAction());
        initialArrange.runNow();
    }
    
    
	// (( METHODS )) \\
    /**
	 * @return number of notches added
	 */
	private int addNotchNodesToGraph() {
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
		return notchIndex;
	}

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
    private static class MusicHistoryLayout extends TimelineLayout {
    	private final int start;
    	private final int end;
    	
    	/**
    	 * 
    	 * @param start
    	 * @param end
    	 * @param m_timelineLength
    	 * @param m_numDivisions
    	 */
        public MusicHistoryLayout(final int start, final int end,
        		final int m_timelineLength, final int m_numDivisions) {
            super(m_timelineLength, m_numDivisions);
            this.start = start;
            this.end = end;
        }

        private double getNodePosition(final int startYear, final int endYear, final double leftOffset) {
            final int yearsFromLeft = startYear - start;
            final double centerCorrection = ((double) endYear - startYear) / (end - start) * m_timelineLength / 2;
            final double fractionFromLeft = (double) yearsFromLeft / (end - start); // horn or not?
            return leftOffset + centerCorrection + (fractionFromLeft * m_timelineLength);
        }

        public void run(final ItemRegistry registry, final double frac) {
            final Iterator nodeItems = registry.getFilteredGraph().getNodes();//getNodeItems();
            final Dimension displaySize = registry.getDisplay(0).getSize();
            final double leftOffset = (displaySize.getWidth() - m_timelineLength) / 2;
            VisualItem node;
            while (nodeItems.hasNext()) {
                node = (VisualItem) nodeItems.next();
                if (node.getAttribute(NODE_TYPE).equals(NOTCH_TYPE)) {
                    layoutNotchNode(displaySize, leftOffset, node);
                } else { // regular data node
                    final Rectangle2D bounds = getLayoutBounds(registry);
                    layoutDataNode(leftOffset, bounds, node);
                }
            }
        }

		/**
		 * @param leftOffset
		 * @param bounds
		 * @param node
		 */
		private void layoutDataNode(final double leftOffset, final Rectangle2D bounds, VisualItem node) {
			final String startYearString = node.getAttribute(START_YEAR);
			final String endYearString = node.getAttribute(END_YEAR);

			final int startYear, endYear;
			if (startYearString.equals(TIMELINE_START)) {
				startYear = start;
			} else {
				startYear = new Integer(startYearString).intValue(); // whoever gets children's hsopital
			}
			if (endYearString.equals(TIMELINE_END)) {
				endYear = end;
			} else {
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

		/**
		 * @param displaySize
		 * @param leftOffset
		 * @param node
		 */
		private void layoutNotchNode(final Dimension displaySize, final double leftOffset, VisualItem node) {
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
		}
    } // end of class MusicHistoryLayout
    

    
    private class CheckBoxFilters extends JPanel {
        private final TimelineGraphFilter filter;
        
        public CheckBoxFilters(final TimelineGraphFilter filter) {
            super();
            this.filter = filter;
            setLayout(new FlowLayout()); // 
            for (final Iterator it = filter.getRegisteredTypes().iterator(); it.hasNext();) {
                final TTypeWrapper type = (TTypeWrapper) it.next();
                System.out.println("name: "+type.toString()+" ;  isShown: "+type.isShown());
                final JCheckBox checkBox = new JCheckBox(type.toString());//, type.isShown());
                checkBox.setModel(new TTypeCheckBoxModel(type));
                checkBox.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent act) {
                        type.setShown(!type.isShown()); // checkBox automatically gets set
                        checkBox.setSelected(!type.isShown());
                        initialArrange.runNow();
                        // refilter and show new timeline
                    }});
                add(checkBox);
            }
        }
    }
    
    private static class TTypeCheckBoxModel extends DefaultButtonModel {
        private final TTypeWrapper type;
        
        public TTypeCheckBoxModel(final TTypeWrapper type) {
            this.type = type;
        }
        
        /**
         * @return Returns the type.
         */
        public TTypeWrapper getType() {
            return type;
        }
    }

    
    // (( MAIN )) \\
    public static void main(String[] args) {
        new TimelineDemo();
    }
} // end of class TimelineDemo
