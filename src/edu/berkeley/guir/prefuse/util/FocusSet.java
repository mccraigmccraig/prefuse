package edu.berkeley.guir.prefuse.util;

import java.util.Collection;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;

/**
 * 
 * Feb 19, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public interface FocusSet {

    public void addFocusListener(FocusListener fl);
    public void removeFocusListener(FocusListener fl);
    
    public void add(Entity focus);
    public void add(Collection foci);
    public void remove(Entity focus);
    public void remove(Collection foci);
    public void set(Entity focus);
    public void set(Collection foci);
    public void clear();
    
    public Iterator iterator();
    public int size();
    public boolean contains(Entity entity);
    
} // end of interface FocusSet
