package edu.berkeley.guir.prefuse.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Provided an iterator over nodes, this class will iterate over all
 * adjacent edges. Each adjacent edge is returned exactly once in the
 * iteration.
 * 
 * Jun 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class EdgeIterator implements Iterator {

	private Iterator m_nodeIterator;
	private Iterator m_edgeIterator;
	private Node     m_curNode;
	private Set      m_visitedEdgeSet;
	private Edge     m_next;

	/**
	 * Constructor.
	 * @param nodeIterator an iterator over nodes
	 */
	public EdgeIterator(Iterator nodeIterator) {
		m_nodeIterator = nodeIterator;
		if ( nodeIterator.hasNext() ) {
			m_curNode = (Node)nodeIterator.next();
			m_edgeIterator = m_curNode.getEdges(); 
		}
		m_visitedEdgeSet = new HashSet();
		m_next = findNext();
	} //

	/**
	 * Not currently supported. 
	 * TODO: Support in future versions?
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	} //

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return (m_next != null);
	} //

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if ( m_next == null )
			throw new NoSuchElementException("No next item in iterator");
		Edge retval = m_next;
		m_next = findNext();
		return retval;
	} //
	
	private Edge findNext() {
		while ( true ) {
			if ( m_edgeIterator != null && m_edgeIterator.hasNext() ) {
				Edge e = (Edge)m_edgeIterator.next();
				if ( !m_visitedEdgeSet.contains(e) ) {
					m_visitedEdgeSet.add(e);
					return e; 
				}
			} else if ( m_nodeIterator.hasNext() ) {
				m_curNode = (Node)m_nodeIterator.next();
				m_edgeIterator = m_curNode.getEdges();
			} else {
				m_curNode = null;
				m_nodeIterator = null;
				m_edgeIterator = null;
				m_visitedEdgeSet = null;
				return null;
			}
		}
	} //

} // end of class EdgeIterator
