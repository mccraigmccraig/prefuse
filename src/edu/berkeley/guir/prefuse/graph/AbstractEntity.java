package edu.berkeley.guir.prefuse.graph;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class from which graph entities (nodes and edges) can descend.
 * Provides support for handling entity attributes.
 * 
 * Apr 24, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class AbstractEntity implements Entity {	

	protected Map m_attributes;	

	/**
	 * Get an attribute associated with this entity.
	 * @param name the name of the attribute
	 * @return the attribute value (possibly null)
	 */
	public String getAttribute(String name) {
		if ( m_attributes == null ) {
			return null;
		} else {
			return (String)m_attributes.get(name);
		}
	} //

	/**
	 * Get all attributes associated with this entity.
	 * @return a Map of all this entity's attributes.
	 */
	public Map getAttributes() {
		if ( m_attributes == null ) {
			return Collections.EMPTY_MAP;
		} else {
			return m_attributes;
		}
	} //

	/**
	 * Get an attribute associated with this entity.
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setAttribute(String name, String value) {
		if ( m_attributes == null ) {
			try {
				m_attributes = (Map)MAP_TYPE.newInstance();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		m_attributes.put(name, value);
	} //

	/**
	 * Sets the attribute map for this Entity. The map
	 * should contain only <code>String</code> values,
	 * otherwise an exception will be thrown.
	 * @param attrMap the attribute map
	 * @throws IllegalArgumentException is the input map
	 * contains non-<code>String</code> values.
	 */
	public void setAttributes(Map attrMap) {
		Iterator iter = attrMap.keySet().iterator();
		while ( iter.hasNext() ) {
			Object key = iter.next();
			Object val = attrMap.get(key);
			if ( !(key instanceof String && val instanceof String) )
				throw new IllegalArgumentException(
					"Non-string value contained in attribute map");
		}
		m_attributes = attrMap;
	} //
		
	/**
	 * Remove all attributes associated with this entity.
	 */	
	public void clearAttributes() {
		if ( m_attributes != null ) {
			m_attributes.clear();
		}
	} //
	
	/**
	 * Returns a String representation of the Entity.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Entity[");
		Iterator iter = m_attributes.keySet().iterator();
		while ( iter.hasNext() ) {
			String key = (String)iter.next();
			String val = (String)m_attributes.get(key);
			sbuf.append(key).append('=').append(val);
			if ( iter.hasNext() )
				sbuf.append(',');
		}
		sbuf.append("]");
		return sbuf.toString();
	} //

} // end of abstract class AbstractEntity
