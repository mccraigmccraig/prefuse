package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

/**
 * Inteface for classes to monitor changes to the focus status
 * of graph elements.
 * 
 * Apr 26, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface FocusListener extends EventListener {
    
    public void focusChanged(FocusEvent e);

} // end of interface FocusListener
