package edu.berkeley.guir.prefuse.action;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.DefaultTree;

/**
 * Abstract class providing convenience methods for tree layout algorithms.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class TreeLayout extends Layout {

    protected NodeItem m_root;
    
    public NodeItem getLayoutRoot() {
        return m_root;
    } //
    
    public void setLayoutRoot(NodeItem root) {
        m_root = root;
    } //
    
    public NodeItem getLayoutRoot(ItemRegistry registry) {
        if ( m_root != null )
            return m_root;
        DefaultTree t = (DefaultTree)registry.getGraph();
        return registry.getNodeItem(t.getRoot());
    } //

} // end of abstract class TreeLayout
