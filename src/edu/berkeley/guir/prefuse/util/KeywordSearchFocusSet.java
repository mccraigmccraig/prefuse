package edu.berkeley.guir.prefuse.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import edu.berkeley.guir.prefuse.event.FocusEventMulticaster;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Tree;

/**
 * <p>
 * A {@link FocusSet FocusSet} implementation that performs efficient keyword
 * searches on graph data. The {@link #index(Iterator, String) index} method
 * should be used to register searchable graph data. Then the
 * {@link #search(String) search} method can be used to perform a search. The
 * matching search results then become the members of this 
 * <code>FocusSet</code>. This class uses a {@link Trie Trie} data structure
 * to find search results in time proportional to only the length of the
 * query string, however, only prefix matches will be identified as valid
 * search matches.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> The {@link #add(Entity) add}, (@link #remove(Entity) remove},
 * and {@link #set(Entity) set} methods are not supported by this 
 * implementation, and will generate exceptions if called. Instead, the focus
 * membership is determined by the search matches found using the
 * {@link #search(String) search} method.
 * </p>
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class KeywordSearchFocusSet implements FocusSet {

    private FocusListener m_listener = null;
    private HashSet m_set = new HashSet();
    private Trie m_trie;
    private Trie.TrieNode m_curNode;
    private String m_delim = " ";
    
    public KeywordSearchFocusSet(boolean caseSensitive) {
        m_trie = new Trie(caseSensitive);
    } //
    
    public void addFocusListener(FocusListener fl) {
        m_listener = FocusEventMulticaster.add(m_listener, fl);
    } //

    public void removeFocusListener(FocusListener fl) {
        m_listener = FocusEventMulticaster.remove(m_listener, fl);
    } //

    public String getDelimiterString() {
        return m_delim;
    } //
    
    public void setDelimiterString(String delim) {
        m_delim = delim;
    } //
    
    public void search(String query) {
        m_curNode = m_trie.find(query);
        if ( m_curNode != null ) {
            Iterator iter = iterator();
            while ( iter.hasNext() )
                m_set.add(iter.next());
        }
    } //
    
    public static Tree getTree(Iterator entities, String attrName) {
        KeywordSearchFocusSet set = new KeywordSearchFocusSet(false);
        set.index(entities, attrName);
        return set.m_trie.tree();
    } //
    
    public void index(Iterator entities, String attrName) {
        String s;
        while ( entities.hasNext() ) {
            Entity e = (Entity)entities.next();
            if ( (s=e.getAttribute(attrName)) == null ) continue;
            StringTokenizer st = new StringTokenizer(s,m_delim);
            while ( st.hasMoreTokens() ) {
                String tok = st.nextToken();
                addString(tok, e);
            }
        }
    } //
    
    private void addString(String s, Entity e) {
        m_trie.addString(s,e);
    } //
    
    public void clear() {
        m_curNode = null;
        m_set.clear();
    } //

    public Iterator iterator() {
        if ( m_curNode == null ) {
            return Collections.EMPTY_LIST.iterator();
        } else {
            return m_trie.new TrieIterator(m_curNode);
        }
    } //

    public int size() {
        return (m_curNode==null ? 0 : m_curNode.leafCount);
    } //

    public boolean contains(Entity entity) {
        return m_set.contains(entity);
    } //
    
    // ========================================================================
    // == UNSUPPORTED OPERATIONS ==============================================
    
    public void add(Entity focus) {
        throw new UnsupportedOperationException();
    } //
    public void add(Collection foci) {
        throw new UnsupportedOperationException();
    } //
    public void remove(Entity focus) {
        throw new UnsupportedOperationException();
    } //
    public void remove(Collection foci) {
        throw new UnsupportedOperationException();
    } //
    public void set(Entity focus) {
        throw new UnsupportedOperationException();
    } //
    public void set(Collection foci) {
        throw new UnsupportedOperationException();
    } //
    
}  // end of class KeywordSearchFocusSet
