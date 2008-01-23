package prefuse.action.layout.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import prefuse.Display;
import prefuse.action.layout.Orientation;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ArrayLib;
import prefuse.visual.NodeItem;

/**
 * <p>TreeLayout that computes a tidy layout of a node-link tree
 * diagram. This algorithm lays out a rooted tree such that each
 * depth level of the tree is on a shared line. The orientation of the
 * tree can be set such that the tree goes left-to-right (default),
 * right-to-left, top-to-bottom, or bottom-to-top.</p>
 *
 * <p>The algorithm used is that of Christoph Buchheim, Michael Jünger,
 * and Sebastian Leipert from their research paper
 * <a href="http://citeseer.ist.psu.edu/buchheim02improving.html">
 * Improving Walker's Algorithm to Run in Linear Time</a>, Graph Drawing 2002.
 * This algorithm corrects performance issues in Walker's algorithm, which
 * generalizes Reingold and Tilford's method for tidy drawings of trees to
 * support trees with an arbitrary number of children at any given node.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class NodeLinkTreeLayout extends TreeLayout {

    private Orientation    m_orientation;  // the orientation of the tree
    private double m_bspace = 5;   // the spacing between sibling nodes
    private double m_tspace = 25;  // the spacing between subtrees
    private double m_dspace = 50;  // the spacing between depth levels
    private double m_offset = 50;  // pixel offset for root node position

    private double[] m_depths = new double[10];
    private int      m_maxDepth = 0;

    private double m_ax, m_ay; // for holding anchor co-ordinates

    /**
     * Create a new NodeLinkTreeLayout. A left-to-right orientation is assumed.
     * @param group the data group to layout. Must resolve to a Graph instance.
     */
    public NodeLinkTreeLayout(String group) {
        super(group);
        m_orientation = Orientation.LEFT_RIGHT;
    }

    /**
     * Create a new NodeLinkTreeLayout.
     * @param group the data group to layout. Must resolve to a Graph instance.
     * @param orientation the orientation of the tree layout. One of
     * {@link Orientation#LEFT_RIGHT},
     * {@link Orientation#RIGHT_LEFT},
     * {@link Orientation#TOP_BOTTOM}, or
     * {@link Orientation#BOTTOM_TOP}.
     * @param dspace the spacing to maintain between depth levels of the tree
     * @param bspace the spacing to maintain between sibling nodes
     * @param tspace the spacing to maintain between neighboring subtrees
     */
    public NodeLinkTreeLayout(String group, Orientation orientation,
            double dspace, double bspace, double tspace)
    {
        super(group);
        m_orientation = orientation;
        m_dspace = dspace;
        m_bspace = bspace;
        m_tspace = tspace;
    }

    // ------------------------------------------------------------------------

    /**
     * Set the orientation of the tree layout.
     * @param orientation the orientation value. One of
     * {@link Orientation#LEFT_RIGHT},
     * {@link Orientation#RIGHT_LEFT},
     * {@link Orientation#TOP_BOTTOM}, or
     * {@link Orientation#BOTTOM_TOP}.
     */
    public void setOrientation(Orientation orientation) {
        if ( orientation == Orientation.CENTER )
        {
            throw new IllegalArgumentException(
                "Unsupported orientation value: "+orientation);
        }
        m_orientation = orientation;
    }

    /**
     * Get the orientation of the tree layout.
     * @return the orientation value. One of
     * {@link Orientation#LEFT_RIGHT},
     * {@link Orientation#RIGHT_LEFT},
     * {@link Orientation#TOP_BOTTOM}, or
     * {@link Orientation#BOTTOM_TOP}.
     */
    public Orientation getOrientation() {
        return m_orientation;
    }

    /**
     * Set the spacing between depth levels.
     * @param d the depth spacing to use
     */
    public void setDepthSpacing(double d) {
        m_dspace = d;
    }

    /**
     * Get the spacing between depth levels.
     * @return the depth spacing
     */
    public double getDepthSpacing() {
        return m_dspace;
    }

    /**
     * Set the spacing between neighbor nodes.
     * @param b the breadth spacing to use
     */
    public void setBreadthSpacing(double b) {
        m_bspace = b;
    }

    /**
     * Get the spacing between neighbor nodes.
     * @return the breadth spacing
     */
    public double getBreadthSpacing() {
        return m_bspace;
    }

    /**
     * Set the spacing between neighboring subtrees.
     * @param s the subtree spacing to use
     */
    public void setSubtreeSpacing(double s) {
        m_tspace = s;
    }

    /**
     * Get the spacing between neighboring subtrees.
     * @return the subtree spacing
     */
    public double getSubtreeSpacing() {
        return m_tspace;
    }

    /**
     * Set the offset value for placing the root node of the tree. The
     * dimension in which this offset is applied is dependent upon the
     * orientation of the tree. For example, in a left-to-right orientation,
     * the offset will a horizontal offset from the left edge of the layout
     * bounds.
     * @param o the value by which to offset the root node of the tree
     */
    public void setRootNodeOffset(double o) {
        m_offset = o;
    }

    /**
     * Get the offset value for placing the root node of the tree.
     * @return the value by which the root node of the tree is offset
     */
    public double getRootNodeOffset() {
        return m_offset;
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.layout.Layout#getLayoutAnchor()
     */
    @Override
	public Point2D getLayoutAnchor() {
        if ( m_anchor != null ) {
			return m_anchor;
		}

        m_tmpa.setLocation(0,0);
        if ( m_vis != null ) {
            Display d = m_vis.getDisplay(0);
            Rectangle2D b = this.getLayoutBounds();
            switch ( m_orientation ) {
            case LEFT_RIGHT:
                m_tmpa.setLocation(m_offset, d.getHeight()/2.0);
                break;
            case RIGHT_LEFT:
                m_tmpa.setLocation(b.getMaxX()-m_offset, d.getHeight()/2.0);
                break;
            case TOP_BOTTOM:
                m_tmpa.setLocation(d.getWidth()/2.0, m_offset);
                break;
            case BOTTOM_TOP:
                m_tmpa.setLocation(d.getWidth()/2.0, b.getMaxY()-m_offset);
                break;
            }
            d.getInverseTransform().transform(m_tmpa, m_tmpa);
        }
        return m_tmpa;
    }

    private double spacing(NodeItem<?,?> l, NodeItem<?,?> r, boolean siblings) {

		switch (m_orientation) {
		case LEFT_RIGHT:
		case RIGHT_LEFT: {
			return (siblings ? m_bspace : m_tspace) + l.getBounds().getHeight();
		}
		case TOP_BOTTOM:
		case BOTTOM_TOP: {
			return (siblings ? m_bspace : m_tspace) + l.getBounds().getWidth();
		}

		default: {
			throw new IllegalStateException("unexpected orientation!");
		}
		}

	}

    private void updateDepths(int depth, NodeItem<?,?> item) {
        boolean v = m_orientation == Orientation.TOP_BOTTOM ||
                      m_orientation == Orientation.BOTTOM_TOP;
        double d = v ? item.getBounds().getHeight()
                       : item.getBounds().getWidth();
        if ( m_depths.length <= depth ) {
			m_depths = ArrayLib.resize(m_depths, 3*depth/2);
		}
        m_depths[depth] = Math.max(m_depths[depth], d);
        m_maxDepth = Math.max(m_maxDepth, depth);
    }

    private void determineDepths() {
        for ( int i=1; i<m_maxDepth; ++i ) {
			m_depths[i] += m_depths[i-1] + m_dspace;
		}
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.Action#run(double)
     */
    @Override
	public void run(double frac) {
        Graph<?,?,?> g = (Graph<?,?,?>) m_vis.getGroup(m_group);
        initSchema(g.getNodes());

        Arrays.fill(m_depths, 0);
        m_maxDepth = 0;

        Point2D a = getLayoutAnchor();
        m_ax = a.getX();
        m_ay = a.getY();

        NodeItem<?,?> root = getLayoutRoot();
        Params rp = getParams(root);

        // do first pass - compute breadth information, collect depth info
        firstWalk(root, 0, 1);

        // sum up the depth info
        determineDepths();

        // do second pass - assign layout positions
        secondWalk(root, null, -rp.prelim, 0);
    }

    private void firstWalk(NodeItem<?,?> n, int num, int depth) {
        Params np = getParams(n);
        np.number = num;
        updateDepths(depth, n);

        boolean expanded = n.isExpanded();
        if ( n.children().isEmpty() || !expanded ) // is leaf
        {
            NodeItem<?,?> l = n.getPreviousSibling();
            if ( l == null ) {
                np.prelim = 0;
            } else {
                np.prelim = getParams(l).prelim + spacing(l,n,true);
            }
        }
        else if ( expanded )
        {
            NodeItem<?, ?> leftMost = null;
			NodeItem<?, ?> rightMost = null;
			{
				NodeItem<?, ?> defaultAncestor = null;
				int i = 0;
				for (NodeItem<?, ?> c : n.children()) {
					if (leftMost == null) {
						leftMost = c;
						defaultAncestor = c;
					}
					firstWalk(c, i, depth + 1);
					defaultAncestor = apportion(c, defaultAncestor);
					i++;
					rightMost = c;
				}
			}

            executeShifts(n);

            double midpoint = 0.5 *
                (getParams(leftMost).prelim + getParams(rightMost).prelim);

            NodeItem<?,?> left = n.getPreviousSibling();
            if ( left != null ) {
                np.prelim = getParams(left).prelim + spacing(left, n, true);
                np.mod = np.prelim - midpoint;
            } else {
                np.prelim = midpoint;
            }
        }
    }

    private NodeItem<?,?> apportion(NodeItem<?,?> v, NodeItem<?,?> a) {
        NodeItem<?,?> w = v.getPreviousSibling();
        if ( w != null ) {
            NodeItem<?,?> vip, vim, vop, vom;
            double   sip, sim, sop, som;

            vip = vop = v;
            vim = w;
            vom = vip.getParent().children().get(0);

            sip = getParams(vip).mod;
            sop = getParams(vop).mod;
            sim = getParams(vim).mod;
            som = getParams(vom).mod;

            NodeItem<?,?> nr = nextRight(vim);
            NodeItem<?,?> nl = nextLeft(vip);
            while ( nr != null && nl != null ) {
                vim = nr;
                vip = nl;
                vom = nextLeft(vom);
                vop = nextRight(vop);
                getParams(vop).ancestor = v;
                double shift = getParams(vim).prelim + sim -
                    (getParams(vip).prelim + sip) + spacing(vim,vip,false);
                if ( shift > 0 ) {
                    moveSubtree(ancestor(vim,v,a), v, shift);
                    sip += shift;
                    sop += shift;
                }
                sim += getParams(vim).mod;
                sip += getParams(vip).mod;
                som += getParams(vom).mod;
                sop += getParams(vop).mod;

                nr = nextRight(vim);
                nl = nextLeft(vip);
            }
            if ( nr != null && nextRight(vop) == null ) {
                Params vopp = getParams(vop);
                vopp.thread = nr;
                vopp.mod += sim - sop;
            }
            if ( nl != null && nextLeft(vom) == null ) {
                Params vomp = getParams(vom);
                vomp.thread = nl;
                vomp.mod += sip - som;
                a = v;
            }
        }
        return a;
    }

    private NodeItem<?,?> nextLeft(NodeItem<?,?> n) {
        NodeItem<?,?> c = null;
        if ( n.isExpanded() ) {
        	List<NodeItem<?,?>> cc = (List<NodeItem<?,?>>) (Object) n.children();
        	c = (cc.isEmpty() ? null : cc.get(0));
		}
        return c != null ? c : getParams(n).thread;
    }

    private NodeItem<?,?> nextRight(NodeItem<?,?> n) {
        NodeItem<?,?> c = null;
        if ( n.isExpanded() ) {
        	List<NodeItem<?,?>> cc = (List<NodeItem<?,?>>) (Object) n.children();
        	c = (cc.isEmpty() ? null : cc.get(cc.size() - 1));
		}
        return c != null ? c : getParams(n).thread;
    }

    private void moveSubtree(NodeItem<?,?> wm, NodeItem<?,?> wp, double shift) {
        Params wmp = getParams(wm);
        Params wpp = getParams(wp);
        double subtrees = wpp.number - wmp.number;
        wpp.change -= shift/subtrees;
        wpp.shift += shift;
        wmp.change += shift/subtrees;
        wpp.prelim += shift;
        wpp.mod += shift;
    }

    private void executeShifts(NodeItem<?,?> n) {
        double shift = 0, change = 0;

    	List<NodeItem<?,?>> cc = (List<NodeItem<?,?>>) (Object) n.children();
    	for(int i = cc.size() - 1; i >= 0; i--) {
    		NodeItem<?,?> c = cc.get(i);
            Params cp = getParams(c);
            cp.prelim += shift;
            cp.mod += shift;
            change += cp.change;
            shift += cp.shift + change;
        }
    }

    private NodeItem<?,?> ancestor(NodeItem<?,?> vim, NodeItem<?,?> v, NodeItem<?,?> a) {
        NodeItem<?,?> p = v.getParent();
        Params vimp = getParams(vim);
        if ( vimp.ancestor.getParent() == p ) {
            return vimp.ancestor;
        } else {
            return a;
        }
    }

    private void secondWalk(NodeItem<?,?> n, NodeItem<?,?> p, double m, int depth) {
        Params np = getParams(n);
        setBreadth(n, p, np.prelim + m);
        setDepth(n, p, m_depths[depth]);

        if ( n.isExpanded() ) {
            depth++;
            for (NodeItem<?,?> c : n.children()) {
                secondWalk(c, n, m + np.mod, depth);
            }
        }

        np.clear();
    }

    private void setBreadth(NodeItem<?,?> n, NodeItem<?,?> p, double b) {
        switch ( m_orientation ) {
        case LEFT_RIGHT:
        case RIGHT_LEFT:
            setY(n, p, m_ay + b);
            break;
        case TOP_BOTTOM:
        case BOTTOM_TOP:
            setX(n, p, m_ax + b);
            break;
        default:
            throw new IllegalStateException();
        }
    }

    private void setDepth(NodeItem<?,?> n, NodeItem<?,?> p, double d) {
        switch ( m_orientation ) {
        case LEFT_RIGHT:
            setX(n, p, m_ax + d);
            break;
        case RIGHT_LEFT:
            setX(n, p, m_ax - d);
            break;
        case TOP_BOTTOM:
            setY(n, p, m_ay + d);
            break;
        case BOTTOM_TOP:
            setY(n, p, m_ay - d);
            break;
        default:
            throw new IllegalStateException();
        }
    }

    // ------------------------------------------------------------------------
    // Params Schema

    /**
     * The data field in which the parameters used by this layout are stored.
     */
    public static final String PARAMS = "_reingoldTilfordParams";
    /**
     * The schema for the parameters used by this layout.
     */
    public static final Schema PARAMS_SCHEMA = new Schema();
    static {
        PARAMS_SCHEMA.addColumn(PARAMS, Params.class);
    }

    protected void initSchema(TupleSet<?> ts) {
        ts.addColumns(PARAMS_SCHEMA);
    }

    private Params getParams(NodeItem<?,?> item) {
        Params rp = (Params)item.get(PARAMS);
        if ( rp == null ) {
            rp = new Params();
            item.set(PARAMS, rp);
        }
        if ( rp.number == -2 ) {
            rp.init(item);
        }
        return rp;
    }

    /**
     * Wrapper class holding parameters used for each node in this layout.
     */
    public static class Params implements Cloneable {
        double prelim;
        double mod;
        double shift;
        double change;
        int    number = -2;
        NodeItem<?,?> ancestor = null;
        NodeItem<?,?> thread = null;

        public void init(NodeItem<?,?> item) {
            ancestor = item;
            number = -1;
        }

        public void clear() {
            number = -2;
            prelim = mod = shift = change = 0;
            ancestor = thread = null;
        }
    }

} // end of class NodeLinkTreeLayout
