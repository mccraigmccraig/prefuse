package edu.berkeley.guir.prefuse.action;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * <p>Filters nodes in a graph using the original Furnas fisheye calculation,
 * and sets DOI (degree-of-interest) values for each filtered node. This 
 * function filters current focus nodes, and includes neighbors only in a 
 * limited window around these foci. The size of this window is determined
 * by the minimum DOI value set for this action. By convention, DOI values
 * start at zero for focus nodes, and becoming decreasing negative numbers for
 * each hop away from a focus. This filter also performs garbage collection
 * of node items by default.</p>
 * 
 * <p>For more information about Furnas' fisheye view calculation and DOI values,
 * take a look at G.W. Furnas, "The FISHEYE View: A New Look at Structured 
 * Files," Bell Laboratories Tech. Report, Murray Hill, New Jersey, 1981. 
 * Available online at <a href="http://citeseer.nj.nec.com/furnas81fisheye.html">
 * http://citeseer.nj.nec.com/furnas81fisheye.html</a>.</p>
 * 
 * <p>For a more recent example of fisheye views and DOI functions in information
 * visualization check out S.K. Card and D. Nation. "Degree-of-Interest 
 * Trees: A Component of an Attention-Reactive User Interface," Advanced 
 * Visual Interfaces, Trento, Italy, 2002. Available online at
 * <a href="http://www2.parc.com/istl/projects/uir/pubs/items/UIR-2002-11-Card-AVI-DOITree.pdf">
 * http://www2.parc.com/istl/projects/uir/pubs/items/UIR-2002-11-Card-AVI-DOITree.pdf</a>
 * </p>
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> - prefuse(AT)jheer.org
 */
public class FisheyeGraphFilter extends Filter {

    public static final int DEFAULT_MIN_DOI = -2;
	public static final String ATTR_CENTER  = "center";

	protected int m_minDOI;
    
    protected ItemRegistry m_registry;
    protected Graph m_graph;
    protected List m_queue = new LinkedList();
    
    public FisheyeGraphFilter() {
        this(DEFAULT_MIN_DOI);
    } //
    
    public FisheyeGraphFilter(int minDOI) {
        super(ItemRegistry.DEFAULT_NODE_CLASS, true);
        m_minDOI = minDOI;
    } //
    
    protected Iterator getFoci(ItemRegistry registry) {
        Iterator iter = registry.getDefaultFocusSet().iterator();
        if ( !iter.hasNext() )
            iter = Collections.EMPTY_LIST.iterator();
        return iter;
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
	public void run(ItemRegistry registry, double frac) {
        m_registry = registry;
		m_graph = registry.getGraph();

		Iterator focusIter = getFoci(registry);
		while ( focusIter.hasNext() ) {
            Object focus = focusIter.next();
            if ( !(focus instanceof Node) ) continue;
            
            Node fnode = (Node)focus;
            NodeItem fitem = registry.getNodeItem(fnode);

            boolean recurse = false;
            recurse = ( fitem==null ||  fitem.getDirty()>0 || fitem.getDOI()<0 );
            
            fitem = registry.getNodeItem(fnode, true);
            fitem.removeAllNeighbors(); // necessary?
            
            if ( !recurse )
                continue;
            
            fitem.setDOI(0);
            m_queue.add(fitem);
        
            while ( !m_queue.isEmpty() ) {
                NodeItem ni = (NodeItem)m_queue.remove(0);
                Node n = (Node)ni.getEntity();
                
                double doi = ni.getDOI()-1;
                if ( doi >= m_minDOI ) {					
                    Iterator niter = n.getNeighbors();
                    int i = 0;
                    while ( niter.hasNext() ) {
                        Node nn = (Node)niter.next();
                        NodeItem nni = m_registry.getNodeItem(nn);
                        
                        recurse = ( nni==null ||  nni.getDirty()>0 || nni.getDOI()<doi );
                        nni = m_registry.getNodeItem(nn, true);
                        nni.removeAllNeighbors(); // necessary?
                        
                        if ( recurse ) {
                            nni.setDOI(doi);
                            m_queue.add(nni);
                        }
                    }
                }
            } // elihw
		}
        
        m_registry = null;
        m_graph = null;
        
        // optional garbage collection
        super.run(registry, frac);
	} //

} // end of class FisheyeTreeFilter
