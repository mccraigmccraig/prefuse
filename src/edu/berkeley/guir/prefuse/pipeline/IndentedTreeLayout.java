package edu.berkeley.guir.prefuse.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.collections.DOIItemComparator;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * Performs a simple indented hierarchical layout of a tree. This is the
 * layout most people are used to seeing in their file managers.
 * 
 * Apr 28, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class IndentedTreeLayout extends AbstractPipelineComponent
	implements Layout
{
	public static final String ATTR_EXPANDED = "expanded";

	private class LayoutEntry {
		public LayoutEntry(NodeItem i, int d) {
			nodeItem = i;
			aggrItem = null;
			elided   = false;
			hidden   = false;
			index    = -1;
			depth    = d;
		} //
		GraphItem nodeItem, aggrItem;
		boolean elided, hidden;
		int index, depth;
	} //

	private List m_entryList = new ArrayList();
	private int m_verticalInc = 15;
	private int m_indent = 16;
	private int m_bias = 16;

	private boolean    m_elide = false;            // controls elision
	private List       m_tlist = new LinkedList(); // temporary list
	private Comparator m_comp  = new Comparator() {
		Comparator comp = new DOIItemComparator();
		public int compare(Object o1, Object o2) {
			GraphItem item1 = ((LayoutEntry)o1).nodeItem;
			GraphItem item2 = ((LayoutEntry)o2).nodeItem;
			return comp.compare(item1, item2);
		} //
	};
	
	private AggregateItem m_tmpAggr = null;
	
	public int getIndent() {
		return m_indent;
	} //
	
	public void setIndent(int indent) {
		m_indent = indent;
	} //
	
	public boolean isEliding() {
		return m_elide;
	} //
	
	public void setEliding(boolean s) {
		m_elide = s;
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent#process()
	 */
	public void process() {
		m_tmpAggr = null;
		Tree t = (Tree)m_graph;
		TreeNode root = t.getRoot();
		NodeItem item = m_registry.getNodeItem(root);
		if ( item != null && item.isVisible() ) {
			int availHeight = m_display.getHeight() - m_bias;
			int treeHeight  = calcTreeHeight(m_entryList, root, 0, 0);
			updateStartLocations(m_entryList);
			if ( m_elide && treeHeight > availHeight )
				elide(treeHeight, availHeight);
			layout(m_entryList, 2 + item.getBounds().height/2);
		} else {
			System.err.println("IndentedTreeLayout: Tree root not visible!");
		}
		m_entryList.clear();
	} //
	
	/**
	 * Calculates the full height of the tree while constructing an
	 * in-order list of all visible entries. 
	 */
	protected int calcTreeHeight(List entryList, TreeNode n, int height, int depth) {
		NodeItem item = m_registry.getNodeItem(n);
		if ( item != null && item.isVisible() ) {
			// add entry to entry list
			LayoutEntry entry = new LayoutEntry(item, depth);
			entry.index = entryList.size();
			entryList.add(entry);
			
			// increment height and recurse as necessary
			height += item.getBounds().height;
			if ( isExpanded(item) ) {
				Iterator childIter = n.getChildren();
				while ( childIter.hasNext() ) {
					TreeNode c = (TreeNode)childIter.next();
					height = calcTreeHeight(entryList, c, height, depth+1);
				}
			}
		}
		return height;
	} //
	
	/**
	 * Elides nodes of lower interest until the structure fits within
	 * its display bounds.
	 * @param treeHeight the current height of the tree structure
	 * @param availHeight the available height for displaying the structure
	 */
	protected void elide(int treeHeight, int availHeight) {
		//// allocate auxiliary data structures
//		Map       pmap     = new HashMap();
		List list = new ArrayList(m_entryList);
		boolean elided[]   = new boolean[list.size()];
		
		//// sort all NodeItems in increasing order by doi
		Collections.sort(list, m_comp);
		
		//// iterate through the sorted NodeItems
		Iterator nodeIter = list.iterator();
		while ( nodeIter.hasNext() && treeHeight > availHeight ) {
			// get the next node, set node item as elided, calculate space savings
			LayoutEntry entry = (LayoutEntry)nodeIter.next();
			GraphItem nitem = entry.nodeItem;
			int run, idx = entry.index;
			elided[idx] = true;
			if ( (run=elisionRun(elided, idx)) > 0 ) {
				for ( int j = 0; j < run; j++ ) {
					GraphItem item = ((LayoutEntry)m_entryList.get(idx+j)).nodeItem;
					treeHeight -= item.getBounds().height;
					
					// if all children are elided, don't bother with aggregate
//					TreeNode p = (TreeNode)item.getEntity();
//					if ( p != null ) {
//						Integer ecount = (Integer)pmap.get(p);
//						ecount = new Integer((ecount == null ? 1 : ecount.intValue()+1));
//						if ( ecount.intValue() == p.getNumChildren() ) {
//							treeHeight -= item.getBounds().height;
//						}
//					}
					
					//System.out.println("elided: " + item.getAttribute("FullName"));
				}
			}
		}
		
		//// update nodes and aggregates to reflect elided status
		AggregateItem aitem = null;
		for ( int i = 0, size = 0; i < elided.length; i++ ) {
			if ( (aitem != null && elided[i]) || 
				 (i < elided.length-1 && elided[i] && elided[i+1]) ) {
				LayoutEntry entry = ((LayoutEntry)m_entryList.get(i));
				GraphItem item = entry.nodeItem;
				TreeNode n = (TreeNode)item.getEntity();
				if ( aitem == null ) {
					// get the new aggregate item when needed
					aitem = m_registry.getAggregateItem(n, false);
					if ( aitem != null )
						m_registry.removeMappings(aitem);
					aitem = m_registry.getAggregateItem(n, true);
					copyAttributes(item, aitem);
				} else {
					// otherwise add a mapping
					m_registry.addMapping(n, aitem);
				}
				aitem.setAggregateSize(++size);
				item.setVisible(false);
				entry.elided = true;
				entry.aggrItem = aitem;
			} else if ( aitem != null && !elided[i] ) {
				aitem = null; size = 0;
			}
		}
	} //

	private int elisionRun(boolean[] elided, int idx) {
		int len = elided.length;
		if ( idx == 0 ) {
			return ( len > 1 && elided[1] ? 1 : 0 );
		} else if ( idx == len-1 ) {
			return ( idx > 0 && elided[idx-1] ? 1 : 0 );
		} else {
			if ( len >= 2 && elided[idx-1] && elided[idx+1] ) {
				return 2;
			} else if ( (idx > 0 && elided[idx-1]) || (idx < len-1 && elided[idx+1]) ) {
				return 1;
			} else {
				return 0;
			}
		}
	} //
	
	/**
	 * Copy attributes from one item to another. Used for initializing
	 * aggregate items.
	 */
	private void copyAttributes(GraphItem item1, GraphItem item2) {
		item2.setLocation(item1.getLocation());
		item2.setEndLocation(item1.getEndLocation());
		item2.setSize(item1.getSize());
		item2.setEndSize(item1.getEndSize());		
	} //

	/**
	 * Updates the starting locations of newly visible nodes to ensure
	 * that they animate from their intuitive sources. Must
	 * be run before elision is performed, so that old aggregate
	 * positions are retrieved correctly.
	 * @param entryList
	 */
	protected void updateStartLocations(List entryList) {
		for ( int i = 0; i < entryList.size(); i++ ) {
			LayoutEntry entry = (LayoutEntry)entryList.get(i);
			GraphItem item;
			item = entry.nodeItem;
					
			// added set start position for newly visible nodes -- jheer
			if ( item.isNewlyVisible() ) {
				TreeNode node = (TreeNode)item.getEntity();
				AggregateItem aitem = m_registry.getAggregateItem(node);
				if ( aitem != null && aitem.isVisible() ) {
					item.setLocation(aitem.getEndLocation());
				} else {
					TreeNode p = node.getParent();
					if ( p != null ) {
						GraphItem pitem = m_registry.getNodeItem(p);
						item.setLocation(pitem.getEndLocation());
					}
				}
			}
		}		
	} //

	/**
	 * Compute the layout.
	 */
	protected int layout(List entryList, int height) {
		GraphItem tmpAggr = null;
		for ( int i = 0; i < entryList.size(); i++ ) {
			LayoutEntry entry = (LayoutEntry)entryList.get(i);
			GraphItem item;
			if ( entry.hidden ) {
				continue;
			} else if ( entry.elided ) {
				item = entry.aggrItem;
				if ( item == tmpAggr ) {
					continue;
				} else {
					tmpAggr = item;
				}
			} else {
				item = entry.nodeItem;
			}			
			setLocation(item, entry.depth*m_indent+m_bias, height);
			height += item.getBounds().height;
		}
		return height;
	} //
		
	/**
	 * Set the (x,y) co-ordinates of the given node. Updates aggregated
	 * items as well as visible items.
	 * @param n the node to set the position for
	 * @param x the x-coordinate of the node
	 * @param y the y-coordinate of the node
	 */
	protected void setLocation(GraphItem item, double x, double y) {
		List entities = null;
		if ( item instanceof AggregateItem ) {
			entities = ((AggregateItem)item).getEntities();
		}
		item.updateLocation(x,y);
		item.setLocation(x,y);
		if ( entities != null ) {
			Iterator iter = entities.iterator();
			while ( iter.hasNext() ) {
				NodeItem nitem = m_registry.getNodeItem((TreeNode)iter.next());
				nitem.updateLocation(x,y);
				nitem.setLocation(x,y);
			}
		}
	} //

	/**
	 * Indicates whether or not a node has been manually expanded.
	 */
	private boolean isExpanded(GraphItem item) {
		Boolean b = ((Boolean)item.getVizAttribute(ATTR_EXPANDED));
		return ( b == null ? false : b.booleanValue() );
	} //

} // end of class IndentedTreeLayout
