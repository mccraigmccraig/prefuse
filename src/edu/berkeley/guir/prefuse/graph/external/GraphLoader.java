package edu.berkeley.guir.prefuse.graph.external;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.SimpleGraph;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderListener;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderMulticaster;


/**
 * Loads graph data from an external data source, such as a database or
 * filesystem.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class GraphLoader implements Runnable {

    public static final int LOAD_NEIGHBORS = 0;
    public static final int LOAD_CHILDREN  = 1;
    
    protected List m_queue = new LinkedList();
    
    protected Graph m_graph;
    protected ItemRegistry m_registry;
    
    protected String m_keyField;
    protected LinkedHashMap m_cache = new LinkedHashMap(200, 0.75f, true) {
        public boolean removeEldestEntry(Map.Entry eldest) {
            return evict();
        }
    };
    protected GraphLoaderListener m_listener;
    
    public GraphLoader(ItemRegistry registry, String keyField) {
        m_keyField = keyField;
        m_registry = registry;
        m_graph = registry.getGraph();
        Thread t = new Thread(this);
        
        // we don't want this to slow down animation!
        // besides, most of its work is blocking on IO anyway...
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    } //
    
    public void addGraphLoaderListener(GraphLoaderListener l) {
        m_listener = GraphLoaderMulticaster.add(m_listener, l);
    } //
    
    public void removeGraphLoaderListener(GraphLoaderListener l) {
        m_listener = GraphLoaderMulticaster.remove(m_listener, l);
    } //
    
    public synchronized void loadNeighbors(ExternalNode n) {
        Job j = new Job(LOAD_NEIGHBORS,n);
        if ( !m_queue.contains(j) ) {
            m_queue.add(j);
            this.notifyAll();
        }
    } //
    
    public synchronized void loadChildren(ExternalTreeNode n) {
        Job j = new Job(LOAD_CHILDREN,n);
        if ( !m_queue.contains(j) ) {
            m_queue.add(j);
            this.notifyAll();
        }
    } //
    
    public boolean evict() {
        return false;
    } //
    
    public void run() {
        while ( true ) {
            Job job = getNextJob();
            if ( job != null ) {
                if ( job.type == LOAD_NEIGHBORS ) {
                    getNeighbors(job.n);
                    job.n.setNeighborsLoaded(true);
                } else if ( job.type == LOAD_CHILDREN ) {
                    getChildren((ExternalTreeNode)job.n);
                }
            } else {
                // nothing to do, chill out until notified
                try {
                    synchronized (this) { wait(); }
                } catch (InterruptedException e) { }
            }
        }
    } //
    
    protected synchronized Job getNextJob() {
        return (m_queue.isEmpty() ? null : (Job)m_queue.remove(0));
    } //
    
    protected void foundNode(int type, ExternalNode src, ExternalNode n, Edge e) {
        String key = n.getAttribute(m_keyField);
        if ( m_cache.containsKey(key) )
            // switch n reference to original loaded version 
            n = (ExternalNode)m_cache.get(key);
        else
            m_cache.put(key, n);
        
        n.setLoader(this);
        
        synchronized ( m_registry ) {
            ((SimpleGraph)m_graph).addNode(n);
            if ( src != null )
                ((SimpleGraph)m_graph).addEdge(src,n);
        }
        
        if ( m_listener != null )
            m_listener.entityLoaded(n);
    } //
    
    protected abstract void getNeighbors(ExternalNode n);
    
    protected abstract void getChildren(ExternalTreeNode n);
    
    public class Job {
        public Job(int type, ExternalNode n) {
            this.type = type;
            this.n = n;
        }
        int type;
        ExternalNode n;
        public boolean equals(Object o) {
            if ( !(o instanceof Job) )
                return false;
            Job j = (Job)o;
            return ( type==j.type && n==j.n );
        }
    } //
    
} // end of class GraphLoader
