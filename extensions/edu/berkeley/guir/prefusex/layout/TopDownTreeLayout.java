package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;

/**
 * Implements the Reingold-Tilford tree layout algorithm.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class TopDownTreeLayout extends TreeLayout {

    /**
     * The algorithm (Walker, S P & E 20 (7), 685-705, 1990).
     * First look at the situation (FirstWalk):
     * <ul>
     * <li> Leaf ?
     *    <ul>
     *    <li> Has left brother ?
     *    <p> No -> Set Preliminary coord to 0
     *    <p> Yes -> Set Preliminary coord according to left brother and minimal distance.
     *    <p> In both cases -> Set modifier to 0
     * </li>
     * <li> Apex ?
     *    <ul>
     *    <li> Has left brother ?
     *    <p> No -> Set preliminary position to center of children's coord -> Set modifier field to 0
     *    <p> Yes -> Set Preliminary position to minimal distance with left brother
     *     -> Set modifier to preliminary position - (average of kids coord)
     *    <p> For Apex (with left brother): need to look for overlap with an already positioned
     *    tree on the left. In this case, compute how much the tree needs to be translated
     *    (to the right) and adjust both preliminary coord AND modifier fields.
     *    <p> After those adjustments are made, need to evenly place kids.
     *    Go over the kids coord and evenly distribute them (according to their left brother
     *    coord and the average distance to be put in between nodes) -- actually not done.
     *
     * </li>
     * </ul>
     *
     * Second walk: go through nodes root first, then from left to right
     * and compute absolute coord by recursively applying modifier fields
     * to preliminary coords.
     * <p>
     * Vertical positioning: nodes are positioned root at the top. The default case
     * is tolayer nodes according to their depth in the tree (LayerMetric).
     * However, the algorithm first looks whether nodes have a property called
     * VERTICAL_POSITION (a check is done on each node -- if only some of them share this propert,
     * strange results must be expected). 
     * <p>
     * As the layout can be used as the super layout of a compound layout, it checks whether there
     * is a context to get node sizes from. If so, then vertical positioning must be defined
     * according to the size of the metanodes.
     */
    
    /**
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
    public void run(ItemRegistry registry, double frac) {
        // TODO Auto-generated method stub

    }

}
