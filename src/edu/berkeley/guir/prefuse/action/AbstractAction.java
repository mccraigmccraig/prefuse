package edu.berkeley.guir.prefuse.action;

import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * 
 * Feb 6, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class AbstractAction implements Action {
    
    protected boolean  m_enabled;
    
    /**
     * Default constructor.
     */
    public AbstractAction() {
        m_enabled = true;
    } //

    public abstract void run(ItemRegistry registry, double frac);

    public boolean isEnabled() {
        return m_enabled;
    } //
    
    public void setEnabled(boolean s) {
        m_enabled = s;
    } //

} // end of class Action
