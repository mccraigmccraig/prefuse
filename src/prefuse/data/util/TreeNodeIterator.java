/**
 * Copyright (c) 2004-2006 Regents of the University of California.
 * See "license-prefuse.txt" for licensing terms.
 */
package prefuse.data.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Node;

/**
 * A depth-first iterator over the subtree rooted at given node.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TreeNodeIterator <N extends Node<?,?>> implements Iterator<N> {

    private List<N> m_stack;
    private N m_root;
    private boolean m_preorder = true;

    /**
     * Create a new TreeNodeIterator over the given subtree.
     * @param root the root of the subtree to traverse
     */
    public TreeNodeIterator(N root) {
    	this(root, true);
    }

    /**
     * Create a new TreeNodeIterator over the given subtree.
     * @param root the root of the subtree to traverse
     * @param preorder true to use a pre-order traversal, false
     *  for a post-order traversal
     */
    public TreeNodeIterator(N root, boolean preorder) {
    	m_preorder = preorder;
    	m_root = root;
    	m_stack = new ArrayList<N>();
    	m_stack.add(root);

    	if (!preorder) {
    		List<N> children = (List<N>) root.children();
    		while(!children.isEmpty()) {
    			N n = children.get(0);
    			m_stack.add(n);
    			children = (List<N>) n.children();
    		}
    	}

    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return !m_stack.isEmpty();
    }

    /**
     * @see java.util.Iterator#next()
     */
    public N next() {
    	N c, x = null;
    	if (m_preorder) {
    		x = m_stack.get(m_stack.size()-1);
    		List<N> xChildren = (List<N>) x.children();
	    	if ( !xChildren.isEmpty()) {
	    		c = xChildren.get(0);
	    		m_stack.add(c);
	    	} else if ( (c=(N) x.getNextSibling()) != null ) {
	    		m_stack.set(m_stack.size()-1, c);
	    	} else {
	    		m_stack.remove(m_stack.size()-1);
	    		while (!m_stack.isEmpty()) {
		    		c = m_stack.remove(m_stack.size()-1);
		    		if ( c == m_root ) {
		    			break;
		    		} else if ( (c=(N) c.getNextSibling()) != null ) {
		    			m_stack.add(c); break;
		    		}
	    		}
	    	}
    	} else {
    		x = m_stack.remove(m_stack.size()-1);
    		if ( x != m_root && (c=(N) x.getNextSibling()) != null ) {
				m_stack.add(c);
        		List<N> cChildren = (List<N>) c.children();
        		while(!cChildren.isEmpty()) {
        			c = cChildren.get(0);
    				m_stack.add(c);
            		cChildren = (List<N>) c.children();
        		}
    		}
    	}
    	return x;
    }

    /**
     * Throws an UnsupportedOperationException
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }

} // end of class TreeNodeIterator
