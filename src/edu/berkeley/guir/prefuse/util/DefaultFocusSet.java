package edu.berkeley.guir.prefuse.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusEventMulticaster;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;

/**
 * Default implementation of the {@link FocusSet FocusSet} interface. This
 * class maintains a <code>java.util.LinkedHashSet</code> of focus entities,
 * supporting quick lookup of entities while maintaining the order in which
 * focus entities are added to the set.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DefaultFocusSet implements FocusSet {

    private Set m_foci = new LinkedHashSet();
    private ArrayList m_tmp = new ArrayList();
    
    private FocusListener m_listener;

    public void addFocusListener(FocusListener fl) {
        m_listener = FocusEventMulticaster.add(m_listener, fl);
    } //

    public void removeFocusListener(FocusListener fl) {
        m_listener = FocusEventMulticaster.remove(m_listener, fl);
    } //

    public void add(Entity focus) {
        if ( m_foci.add(focus) && m_listener != null ) {
            m_listener.focusChanged(new FocusEvent(this,
                FocusEvent.FOCUS_ADDED, new Entity[] {focus}, null));
        }
    } //

    public void add(Collection foci) {
        Iterator iter = foci.iterator();
        while ( iter.hasNext() ) {
            Object o = iter.next();
            if ( !(o instanceof Entity) ) {
                throw new IllegalArgumentException(
                    "All foci must be of type Entity");
            } else if ( m_foci.add(o) && m_listener != null ) {
                m_tmp.add(o);
            }
        }
        if ( m_listener != null && m_tmp.size() > 0 ) {
            Entity[] add = (Entity[])m_tmp.toArray(FocusEvent.EMPTY);
            m_listener.focusChanged(new FocusEvent(this,
                FocusEvent.FOCUS_ADDED, add, null));
        }
        m_tmp.clear();
    } //

    public void remove(Entity focus) {
        if ( m_foci.remove(focus) && m_listener != null ) {
            m_listener.focusChanged(new FocusEvent(this,
                FocusEvent.FOCUS_REMOVED, null, new Entity[] {focus}));
        }
    } //

    public void remove(Collection foci) {
        Iterator iter = foci.iterator();
        while ( iter.hasNext() ) {
            Object o = iter.next();
            if ( m_foci.remove(o) && m_listener != null )
                m_tmp.add(o);
        }
        if ( m_listener != null && m_tmp.size() > 0 ) {
            Entity[] rem = (Entity[])m_tmp.toArray(FocusEvent.EMPTY);
            m_listener.focusChanged(new FocusEvent(this,
                FocusEvent.FOCUS_REMOVED,null,rem));
        }
    } //
    
    public void set(Entity focus) {
        Entity[] add = null, rem = null;
        if ( m_foci.size() > 0 && m_listener != null )
            rem = (Entity[])m_foci.toArray(FocusEvent.EMPTY);
        m_foci.clear();
        if ( m_foci.add(focus) && m_listener != null )
            add = new Entity[] {focus};
        if ( add != null || rem != null ) {
            m_listener.focusChanged(new FocusEvent(this,
                FocusEvent.FOCUS_SET,add,rem));
        }
    } //
    
    public void set(Collection foci) {
        // check validity of input before proceeding
        Iterator iter = foci.iterator();
        while ( iter.hasNext() ) {
            Object o = iter.next();
            if ( !(o instanceof Entity) ) {
                throw new IllegalArgumentException(
                "All foci must be of type Entity");
            }
        }
        // now clear the focus set
        Entity[] add = null, rem = null;
        if ( m_listener != null && m_foci.size() > 0 )
            rem = (Entity[])m_foci.toArray(FocusEvent.EMPTY);
        m_foci.clear();
        // now add new foci
        iter = foci.iterator();
        while ( iter.hasNext() ) {
            Entity o = (Entity)iter.next();
            if ( m_foci.add(o) && m_listener != null ) {
                m_tmp.add(o);
            }
        }
        if ( m_listener != null && m_tmp.size() > 0 ) {
            add = (Entity[])m_tmp.toArray(FocusEvent.EMPTY);
            m_tmp.clear();
        }
        if ( add != null || rem != null ) {
            m_listener.focusChanged(new FocusEvent(this,
                FocusEvent.FOCUS_SET, add, rem));
        }
    } //

    public void clear() {
        Entity[] rem = null;
        if ( m_listener != null && m_foci.size() > 0 )
            rem = (Entity[])m_foci.toArray(FocusEvent.EMPTY);
        m_foci.clear();
        if ( rem != null ) {
            m_listener.focusChanged(new FocusEvent(this,
                    FocusEvent.FOCUS_SET, null, rem));
        }
    } //

    public Iterator iterator() {
        return m_foci.iterator();
    } //

    public int size() {
        return m_foci.size();
    } //

    public boolean contains(Entity entity) {
        return m_foci.contains(entity);
    } //

} // end of class DefaultFocusSet
