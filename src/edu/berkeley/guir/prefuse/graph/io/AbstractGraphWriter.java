package edu.berkeley.guir.prefuse.graph.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.berkeley.guir.prefuse.graph.Graph;

/**
 * Abstract class supporting GraphWriter implementations.
 * 
 * June 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public abstract class AbstractGraphWriter implements GraphWriter {

	/**
	 * @see edu.berkeley.guir.prefuse.graph.io.GraphWriter#writeGraph(edu.berkeley.guir.prefuse.graph.Graph, java.lang.String)
	 */
	public void writeGraph(Graph g, String filename) 
		throws FileNotFoundException, IOException
	{
		writeGraph(g, new FileOutputStream(filename));		
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.graph.io.GraphWriter#writeGraph(edu.berkeley.guir.prefuse.graph.Graph, java.io.File)
	 */
	public void writeGraph(Graph g, File f) throws FileNotFoundException, IOException {
		writeGraph(g, new FileOutputStream(f));
	} //
	
	/**
	 * @see edu.berkeley.guir.prefuse.graph.io.GraphWriter#writeGraph(edu.berkeley.guir.prefuse.graph.Graph, java.io.OutputStream)
	 */
	public abstract void writeGraph(Graph g, OutputStream is) throws IOException;

} // end of class AbstractGraphWriter
