package edu.berkeley.guir.prefuse.graph.event;

import java.util.EventListener;

import edu.berkeley.guir.prefuse.graph.Entity;

/**
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public interface GraphLoaderListener extends EventListener {

    public void entityLoaded(Entity e);
    public void entityUnloaded(Entity e);
    
} // end of interface GraphLoaderListener
