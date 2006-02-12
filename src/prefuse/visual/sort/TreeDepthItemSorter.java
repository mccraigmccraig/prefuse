package prefuse.visual.sort;

import prefuse.Visualization;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * ItemSorter that sorts items by tree depths, items higher in the
 * tree are given higher scores that items with greater depth levels.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TreeDepthItemSorter extends ItemSorter {

    /**
     * Score items similarly to {@link ItemSorter}, but additionally
     * ranks items based on their tree depth, scoring items with
     * lower tree depth higher.
     * @see prefuse.visual.sort.ItemSorter#score(prefuse.visual.VisualItem)
     */
    public int score(VisualItem item) {
        int score = 0;
        if ( item.isHover() ) {
            score += (1<<29);
        }
        if ( item.isHighlighted() ) {
            score += (1<<28);
        }
        if ( item instanceof NodeItem ) {
            score += (1<<27); // nodes before edges
            score -= (((NodeItem)item).getDepth()<<12);
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
