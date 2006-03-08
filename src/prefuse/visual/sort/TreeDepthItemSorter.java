package prefuse.visual.sort;

import prefuse.Visualization;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * ItemSorter that sorts items by tree depths. By default items deeper
 * in the tree are given lower scores, so that parent nodes are drawn
 * on top of child nodes. This ordering can be reversed using the
 * appropriate constructor arguments.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TreeDepthItemSorter extends ItemSorter {

    private int m_childrenAbove;
    private int m_hover;
    private int m_highlight;
    private int m_depth;
    
    /**
     * Create a new TreeDepthItemSorter that orders nodes such that parents
     * are placed above their children.
     */
    public TreeDepthItemSorter() {
        this(false);
    }
    
    /**
     * Create a new TreeDepthItemSorter with the given sort ordering by depth.
     * @param childrenAbove true if children should be ordered above their
     * parents, false if parents should be ordered above their children.
     */
    public TreeDepthItemSorter(boolean childrenAbove) {
        if ( childrenAbove ) {
            m_childrenAbove = 1;
            m_hover = 13;
            m_highlight = 12;
            m_depth = 14;
        } else {
            m_childrenAbove = -1;
            m_hover = 29;
            m_highlight = 28;
            m_depth = 12;
        }
    }
    
    /**
     * Score items similarly to {@link ItemSorter}, but additionally
     * ranks items based on their tree depth.
     * @see prefuse.visual.sort.ItemSorter#score(prefuse.visual.VisualItem)
     */
    public int score(VisualItem item) {
        int score = 0;
        if ( item.isHover() ) {
            score += (1<<m_hover);
        }
        if ( item.isHighlighted() ) {
            score += (1<<m_highlight);
        }
        if ( item instanceof NodeItem ) {
            score += (1<<27); // nodes before edges
            score += m_childrenAbove*(((NodeItem)item).getDepth()<<m_depth);
        }
        if ( item.isInGroup(Visualization.FOCUS_ITEMS) ) {
            score += (1<<11);
        }
        if ( item.isInGroup(Visualization.SEARCH_ITEMS) ) {
            score += (1<<10);
        }
        if ( item instanceof DecoratorItem ) {
            score += (1<<9);
        }
        return score;
    }

} // end of class TreeDepthItemSorter
