package edu.berkeley.guir.prefuse.action;

import java.util.Iterator;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Filters nodes on a tree using the original Furnas fisheye.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class FisheyeTreeFilter extends AbstractAction {

    public static final int DEFAULT_MIN_DOI = -2;
    
	public static final String ATTR_MIN_DOI = "minDOI";
	public static final String ATTR_CENTER  = "center";

	protected int m_minDOI;
    
    protected ItemRegistry m_registry;
    protected Tree m_tree;
	protected TreeNode m_root;
    
    public FisheyeTreeFilter() {
        this(DEFAULT_MIN_DOI);
    } //
    
    public FisheyeTreeFilter(int minDOI) {
        m_minDOI = minDOI;
    } //
    
    protected Iterator getFoci(ItemRegistry registry) {
        return registry.getDefaultFocusSet().iterator();
    } //
    
	public void run(ItemRegistry registry, double frac) {
        m_registry = registry;
		m_tree = (Tree)registry.getGraph();
		m_root = m_tree.getRoot();

		Iterator focusIter = getFoci(registry);
		while ( focusIter.hasNext() ) {
            Object focus = focusIter.next();
            if ( !(focus instanceof TreeNode) ) continue;
            TreeNode fnode = (TreeNode)focus;
            NodeItem fitem = registry.getNodeItem(fnode);

			boolean recurse = false;
			recurse = ( fitem==null ||  fitem.getDirty()>0 || fitem.getDOI()<0 );
				
			fitem = registry.getNodeItem(fnode, true);
            fitem.removeAllNeighbors();
			if ( recurse ) {
				setDOI(fitem, 0, 0);
				if ( (int)fitem.getDOI() > m_minDOI ) {					
					visitDescendants(fnode, fitem, null);
				}				
				visitAncestors(fnode, fitem);
			}
		}
        
        m_registry = null;
        m_tree = null;
        m_root = null;
	} //
	
	protected void visitDescendants(TreeNode node, NodeItem item, TreeNode skip) {
		int lidx = ( skip == null ? getCenter(item) : node.getChildIndex(skip) );		
		Iterator childIter = node.getChildren();
		int i = 0;
		while ( childIter.hasNext() ) {
			TreeNode cnode = (TreeNode)childIter.next();
			if ( cnode == skip ) { continue; }
			NodeItem citem = m_registry.getNodeItem(cnode, true);
			citem.removeAllNeighbors();					
			
			setDOI(citem, (int)item.getDOI()-1, Math.abs(lidx-i));		
			if ( (int)citem.getDOI() > m_minDOI ) {
				visitDescendants(cnode, citem, null);	
			}
			i++;
		}
	} //
	
	protected void visitAncestors(TreeNode node, NodeItem item) {
		if ( node.getParent() == null || node == m_root ) { return; }
		TreeNode pnode = node.getParent();
		NodeItem pitem = m_registry.getNodeItem(pnode);
		
		boolean recurse = false;
		recurse = ( pitem==null ||  pitem.getDirty()>0 || pitem.getDOI()<0 );
		
		pitem = m_registry.getNodeItem(pnode, true);
        pitem.removeAllNeighbors();
		if ( recurse ) {
			setDOI(pitem, 0, 0);
			if ( (int)pitem.getDOI() > m_minDOI ) {
				visitDescendants(pnode, pitem, node);
			}
			visitAncestors(pnode, pitem);
		}
	} //
	
	protected void setDOI(NodeItem item, int doi, int ldist) {
		double localDOI = -1 * ldist / (double)Math.min(1000.0, m_tree.getNumNodes());
		item.setDOI(doi+localDOI);
	} //

	private int getCenter(NodeItem item) {
		TreeNode node = (TreeNode)item.getVizAttribute(ATTR_CENTER);
		if ( node != null ) {
			TreeNode parent = (TreeNode)item.getEntity();
			int idx = parent.getChildIndex(node);
			if ( idx > -1 )
				return idx;
		}
		return 0;
	} //

} // end of class FisheyeTreeFilter
