package prefuse.action.layout.graph;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import prefuse.action.layout.Layout;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;


/**
 * <p>Layout instance implementing the Fruchterman-Reingold algorithm for
 * force-directed placement of graph nodes. The computational complexity of
 * this algorithm is quadratic [O(n^2)] in the number of nodes, so should
 * only be applied for relatively small graphs, particularly in interactive
 * situations.</p>
 *
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jung.sourceforge.net/">JUNG</a> framework.</p>
 *
 * @author Scott White, Yan-Biao Boey, Danyel Fisher
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class FruchtermanReingoldLayout extends Layout {

    private double forceConstant;
    private double temp;
    private int maxIter = 700;

    protected String m_nodeGroup;
    protected String m_edgeGroup;
    protected int m_fidx;

    private static final double EPSILON = 0.000001D;
    private static final double ALPHA = 0.1;

    /**
     * Create a new FruchtermanReingoldLayout.
     * @param graph the data field to layout. Must resolve to a Graph instance.
     */
    public FruchtermanReingoldLayout(String graph) {
        this(graph, 700);
    }

    /**
     * Create a new FruchtermanReingoldLayout
     * @param graph the data field to layout. Must resolve to a Graph instance.
     * @param maxIter the maximum number of iterations of the algorithm to run
     */
    public FruchtermanReingoldLayout(String graph, int maxIter) {
        super(graph);
        m_nodeGroup = PrefuseLib.getGroupName(graph, Graph.NODES);
        m_edgeGroup = PrefuseLib.getGroupName(graph, Graph.EDGES);
        this.maxIter = maxIter;
    }

    /**
     * Get the maximum number of iterations to run of this algorithm.
     * @return the maximum number of iterations
     */
    public int getMaxIterations() {
        return maxIter;
    }

    /**
     * Set the maximum number of iterations to run of this algorithm.
     * @param maxIter the maximum number of iterations to use
     */
    public void setMaxIterations(int maxIter) {
        this.maxIter = maxIter;
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    @Override
	public void run(double frac) {
        Graph<?,? extends NodeItem<?,?>,? extends EdgeItem<?,?>> g = (Graph<?,? extends NodeItem<?,?>,? extends EdgeItem<?,?>>) m_vis.getGroup(m_group);
        Rectangle2D bounds = super.getLayoutBounds();
        init(g, bounds);

        for (int curIter=0; curIter < maxIter; curIter++ ) {

            // Calculate repulsion
        	for(NodeItem<?,?> n : g.nodes()) {
                if (n.isFixed()) {
					continue;
				}
                calcRepulsion(g, n);
            }

            // Calculate attraction
            for (EdgeItem<?,?> e : g.edges()) {
                calcAttraction(e);
            }

        	for(NodeItem<?,?> n : g.nodes()) {
                if (n.isFixed()) {
					continue;
				}
                calcPositions(n,bounds);
            }

            cool(curIter);
        }

        finish(g);
    }

    private void init(Graph<?,? extends NodeItem<?,?>,? extends EdgeItem<?,?>> g, Rectangle2D b) {
        initSchema(g.getNodes());

        temp = b.getWidth() / 10;
        forceConstant = 0.75 *
            Math.sqrt(b.getHeight()*b.getWidth()/g.getNodeCount());

        // initialize node positions
        Random rand = new Random(42); // get a deterministic layout result
        double scaleW = ALPHA*b.getWidth()/2;
        double scaleH = ALPHA*b.getHeight()/2;
    	for(NodeItem<?,?> n : g.nodes()) {
            Params np = getParams(n);
            np.loc[0] = b.getCenterX() + rand.nextDouble()*scaleW;
            np.loc[1] = b.getCenterY() + rand.nextDouble()*scaleH;
        }
    }

    private void finish(Graph<?,? extends NodeItem<?,?>,? extends EdgeItem<?,?>> g) {
    	for(NodeItem<?,?> n : g.nodes()) {
            Params np = getParams(n);
            setX(n, null, np.loc[0]);
            setY(n, null, np.loc[1]);
        }
    }

    public void calcPositions(NodeItem<?,?> n, Rectangle2D b) {
        Params np = getParams(n);
        double deltaLength = Math.max(EPSILON,
                Math.sqrt(np.disp[0]*np.disp[0] + np.disp[1]*np.disp[1]));

        double xDisp = np.disp[0]/deltaLength * Math.min(deltaLength, temp);

        if (Double.isNaN(xDisp)) {
            System.err.println("Mathematical error... (calcPositions:xDisp)");
         }

        double yDisp = np.disp[1]/deltaLength * Math.min(deltaLength, temp);

        np.loc[0] += xDisp;
        np.loc[1] += yDisp;

        // don't let nodes leave the display
        double borderWidth = b.getWidth() / 50.0;
        double x = np.loc[0];
        if (x < b.getMinX() + borderWidth) {
            x = b.getMinX() + borderWidth + Math.random() * borderWidth * 2.0;
        } else if (x > b.getMaxX() - borderWidth) {
            x = b.getMaxX() - borderWidth - Math.random() * borderWidth * 2.0;
        }

        double y = np.loc[1];
        if (y < b.getMinY() + borderWidth) {
            y = b.getMinY() + borderWidth + Math.random() * borderWidth * 2.0;
        } else if (y > b.getMaxY() - borderWidth) {
            y = b.getMaxY() - borderWidth - Math.random() * borderWidth * 2.0;
        }

        np.loc[0] = x;
        np.loc[1] = y;
    }

    public void calcAttraction(EdgeItem<?,?> e) {
        NodeItem<?,?> n1 = e.getSourceNode();
        Params n1p = getParams(n1);
        NodeItem<?,?> n2 = e.getTargetNode();
        Params n2p = getParams(n2);

        double xDelta = n1p.loc[0] - n2p.loc[0];
        double yDelta = n1p.loc[1] - n2p.loc[1];

        double deltaLength = Math.max(EPSILON,
                Math.sqrt(xDelta*xDelta + yDelta*yDelta));
        double force = deltaLength*deltaLength / forceConstant;

        if (Double.isNaN(force)) {
            System.err.println("Mathematical error...");
        }

        double xDisp = xDelta/deltaLength * force;
        double yDisp = yDelta/deltaLength * force;

        n1p.disp[0] -= xDisp; n1p.disp[1] -= yDisp;
        n2p.disp[0] += xDisp; n2p.disp[1] += yDisp;
    }

    public void calcRepulsion(Graph<?,? extends NodeItem<?,?>,? extends EdgeItem<?,?>> g, NodeItem<?,?> n1) {
        Params np = getParams(n1);
        np.disp[0] = 0.0; np.disp[1] = 0.0;

        for(NodeItem<?,?> n2 : g.nodes()) {
            Params n2p = getParams(n2);
            if (n2.isFixed()) {
				continue;
			}
            if (n1 != n2) {
                double xDelta = np.loc[0] - n2p.loc[0];
                double yDelta = np.loc[1] - n2p.loc[1];

                double deltaLength = Math.max(EPSILON,
                        Math.sqrt(xDelta*xDelta + yDelta*yDelta));

                double force = forceConstant*forceConstant / deltaLength;

                if (Double.isNaN(force)) {
                    System.err.println("Mathematical error...");
                }

                np.disp[0] += xDelta/deltaLength*force;
                np.disp[1] += yDelta/deltaLength*force;
            }
        }
    }

    private void cool(int curIter) {
        temp *= 1.0 - curIter / (double) maxIter;
    }

    // ------------------------------------------------------------------------
    // Params Schema

    /**
     * The data field in which the parameters used by this layout are stored.
     */
    public static final String PARAMS = "_fruchtermanReingoldParams";
    /**
     * The schema for the parameters used by this layout.
     */
    public static final Schema PARAMS_SCHEMA = new Schema();
    static {
        PARAMS_SCHEMA.addColumn(PARAMS, Params.class);
    }

    protected void initSchema(TupleSet<?> ts) {
        try {
            ts.addColumns(PARAMS_SCHEMA);
        } catch ( IllegalArgumentException iae ) {};
    }

    private Params getParams(VisualItem<?> item) {
        Params rp = (Params)item.get(PARAMS);
        if ( rp == null ) {
            rp = new Params();
            item.set(PARAMS, rp);
        }
        return rp;
    }

    /**
     * Wrapper class holding parameters used for each node in this layout.
     */
    public static class Params implements Cloneable {
        double[] loc = new double[2];
        double[] disp = new double[2];
    }

} // end of class FruchtermanReingoldLayout
