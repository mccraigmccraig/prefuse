package edu.berkeley.guir.prefuse.action;

import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * 
 * Feb 6, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public interface Action {
    
    public void run(ItemRegistry registry, double frac);
    public boolean isEnabled();
    public void setEnabled(boolean s);

} // end of interface Action
