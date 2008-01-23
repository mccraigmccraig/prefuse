package prefuse.data.util;

import java.util.concurrent.CopyOnWriteArrayList;

import prefuse.data.event.ProjectionListener;

/**
 * Abstract base class for column projection instances. Implements the
 * listener functionality.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public abstract class AbstractColumnProjection implements ColumnProjection {

    // ------------------------------------------------------------------------
    // Listener Methods

    private CopyOnWriteArrayList<ProjectionListener> m_listeners;

    /**
     * @see prefuse.data.util.ColumnProjection#addProjectionListener(prefuse.data.event.ProjectionListener)
     */
    public void addProjectionListener(ProjectionListener lstnr) {
        if ( m_listeners == null ) {
			m_listeners = new CopyOnWriteArrayList<ProjectionListener>();
		}
        if ( !m_listeners.contains(lstnr) ) {
			m_listeners.add(lstnr);
		}
    }

    /**
     * @see prefuse.data.util.ColumnProjection#removeProjectionListener(prefuse.data.event.ProjectionListener)
     */
    public void removeProjectionListener(ProjectionListener lstnr) {
        if ( m_listeners != null ) {
			m_listeners.remove(lstnr);
		}
        if ( m_listeners.size() == 0 ) {
			m_listeners = null;
		}
    }

    public void fireUpdate() {
        if ( m_listeners == null ) {
			return;
		}
        for(ProjectionListener l : m_listeners) {
            l.projectionChanged(this);
        }
    }

} // end of abstract class AbstractColumnProjection
