package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * A listener interface through which components can be notified
 * of changes in registry bindings. 
 * 
 * Apr 25, 2003 - alann - Created class
 * 
 * @author alann
 */
public interface ItemRegistryListener extends EventListener {
  
  /**
   * Indicates a binding to a new GraphItem has been established.
   * @param item the new GraphItem
   */
  public void registryItemAdded(GraphItem item);
  
  /**
   * Indicates a binding to a GraphItem has been removed.
   * @param item the removed GraphItem
   */
  public void registryItemRemoved(GraphItem item);
  
} // end of class ItemRegistryListener
