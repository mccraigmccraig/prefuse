package edu.berkeley.guir.prefuse;

import java.util.HashMap;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.util.DefaultFocusSet;
import edu.berkeley.guir.prefuse.util.FocusSet;

/**
 * This class helps manage focus and/or selected items in a visualization.
 * It assumes there is at most a single user-selected focus, corresponding
 * to the user's current locus of attention (e.g. a moused-over, or previously
 * clicked item). However, there can also be any number of other focus sets,
 * including search results, or multiple user selections. This class supports 
 * the storage, retrieval, and monitoring of such focus items and sets. 
 * 
 * Feb 9, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FocusManager {

    public static final String DEFAULT_KEY    = "default";
    public static final String SELECTION_KEY  = "selection";
    public static final String SEARCH_KEY     = "search";
    
    private HashMap m_focusSets;

    public FocusManager() {
        m_focusSets = new HashMap();
        setDefaultFocusSet(new DefaultFocusSet());
    } //
    
    public FocusSet getFocusSet(Object key) {
        return (FocusSet)m_focusSets.get(key);
    } //
    
    public void putFocusSet(Object key, FocusSet set) {
        m_focusSets.put(key, set);
    } //
    
    public FocusSet getDefaultFocusSet() {
        return (FocusSet)m_focusSets.get(DEFAULT_KEY);
    } //
    
    public void setDefaultFocusSet(FocusSet set) {
        m_focusSets.put(DEFAULT_KEY, set);
    } //
    
    public Iterator getFocusSetIterator() {
        return m_focusSets.values().iterator();
    } //
    
    public boolean isFocus(Object key, Entity entity) {
        FocusSet set = getFocusSet(key);
        return ( set==null ? false : set.contains(entity) );
    } //
    
    public boolean isFocus(Entity entity) {
        Iterator iter = m_focusSets.keySet().iterator();
        while ( iter.hasNext() ) {
            FocusSet set = (FocusSet)m_focusSets.get(iter.next());
            if ( set.contains(entity) )
                return true;
        }
        return false;
    } //
    
} // end of class FocusManager
