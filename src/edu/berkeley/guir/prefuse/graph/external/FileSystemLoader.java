package edu.berkeley.guir.prefuse.graph.external;

import java.io.File;
import java.io.IOException;

import edu.berkeley.guir.prefuse.ItemRegistry;

/**
 * 
 * Mar 8, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FileSystemLoader extends GraphLoader {

    public FileSystemLoader(ItemRegistry registry) {
        super(registry);
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.graph.external.GraphLoader#getNeighbors(edu.berkeley.guir.prefuse.graph.external.ExternalNode)
     */
    protected void getNeighbors(ExternalNode n) {
        ExternalNode nn;
        String filename = n.getAttribute("filename");
        File f = new File(filename);
        
        File p = f.getParentFile();
        if ( p != null ) {
            nn = buildNode(p);
            this.foundNode(LOAD_NEIGHBORS, n, nn);
        }
        
        File[] fl = f.listFiles();
        if ( fl == null ) return;
        for ( int i=0; i<fl.length; i++ ) {
            nn = buildNode(fl[i]);
            foundNode(LOAD_NEIGHBORS, n, nn);
        }
        
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.external.GraphLoader#getChildren(edu.berkeley.guir.prefuse.graph.external.ExternalTreeNode)
     */
    protected void getChildren(ExternalTreeNode n) {
        
    } //
    
    public ExternalNode buildNode(File f) {
        try {
            ExternalNode n = new ExternalNode();
            f = f.getCanonicalFile();
            n.setAttribute("label", f.getName());
            n.setAttribute("filename", f.getPath());
            n.setAttribute("size", String.valueOf(f.length()));
            return n;
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return null;
    } //

} // end of class FileSystemLoader
