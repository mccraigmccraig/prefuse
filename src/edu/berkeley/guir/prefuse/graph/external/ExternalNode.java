package edu.berkeley.guir.prefuse.graph.external;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;


/**
 * Represents a node in a graph that pulls data from an external source,
 * such as a database or file system.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ExternalNode extends Node {

    protected GraphLoader  m_loader;
    protected boolean m_loadStarted = false;
    protected boolean m_loaded = false;
    protected long    m_access;
    
    public boolean isNeighborsLoaded() {
        return m_loaded;
    } //
    
    void setNeighborsLoaded(boolean s) {
        m_loaded = s;
        m_loadStarted = s;
    } //
    
    protected void checkLoadedStatus() {
        touch();
        if ( !m_loadStarted ) {
            m_loadStarted = true;
            m_loader.loadNeighbors(this);
        }
    } //
    
    protected void touch() {
        m_access = System.currentTimeMillis();
    } //
    
    public void setLoader(GraphLoader gl) {
        m_loader = gl;
    } //
    
    /**
     * Returns an iterator over all neighbor nodes of this node.
     * @return an iterator over this node's neighbors.
     */
    public Iterator getNeighbors() {
        checkLoadedStatus();
        return super.getNeighbors();
    } //
    
    /**
     * Returns the i'th neighbor of this node.
     * @param i the index of the neighbor in the neighbor list.
     * @return Node the Node at the specified position in the list of
     *  neighbors
     */
    public Node getNeighbor(int i) {
        checkLoadedStatus();
        return super.getNeighbor(i);
    } //

    /**
     * Indicates if a given node is a neighbor of this one.
     * @param n the node to check as a neighbor
     * @return true if the node is a neighbor, false otherwise
     */
    public boolean isNeighbor(Node n) {
        //checkLoadedStatus();
        return super.isNeighbor(n);
    } //

    /**
     * Returns the index, or position, of a neighbor node. Returns -1 if the
     * input node is not a neighbor of this node.
     * @param n the node to find the index of
     * @return the node index, or -1 if this node is not a neighbor
     */
    public int getNeighborIndex(Node n) {
        //checkLoadedStatus();
        return super.getNeighborIndex(n);
    } //

    /**
     * Return the total number of neighbors of this node.
     * @return the number of neighbors
     */
    public int getNumNeighbors() {
        //checkLoadedStatus();
        return super.getNumNeighbors();
    } //

    /**
     * Add a new neighbor to this node.
     * @param n the node to add
     */
    public void addNeighbor(Node n) {
        touch();
        super.addNeighbor(n);
    } //
    
    /**
     * Add a new neighbor at the specified position.
     * @param i the index at which to insert the new neighbor
     * @param n the node to add as a neighbor
     */
    public void addNeighbor(int i, Node n) {
        touch();
        super.addNeighbor(i,n);
    } //
    
    /**
     * Remove the given node as a child of this node.
     * @param n the node to remove
     */
    public boolean removeNeighbor(Node n) {
        touch();
        return super.removeNeighbor(n);
    } //

    /**
     * Remove the neighbor node at the specified index.
     * @param i the index at which to remove a node
     */
    public Node removeNeighbor(int i) {
        touch();
        return super.removeNeighbor(i);
    } //
    
    public Iterator getEdges() {
        checkLoadedStatus();
        return super.getEdges();
    } //
    
    public Edge getEdge(Node n) {
        checkLoadedStatus();
        return super.getEdge(n);
    } //
    
    public Edge getEdge(int i) {
        checkLoadedStatus();
        return super.getEdge(i);
    } //
    
    public boolean isIncidentEdge(Edge e) {
        //checkLoadedStatus();
        return super.isIncidentEdge(e);
    } //
    
    public int getEdgeIndex(Edge e) {
        //checkLoadedStatus();
        return super.getEdgeIndex(e);
    } //
    
    public int getNumEdges() {
        checkLoadedStatus();
        return super.getNumEdges();
    } //
    
    public void addEdge(Edge e) {
        touch();
        super.addEdge(e);
    } //
    
    public void addEdge(int i, Edge e) {
        touch();
        super.addEdge(i,e);
    } //
    
    public boolean removeEdge(Edge e) {
        touch();
        return super.removeEdge(e);
    } //
    
    public Edge removeEdge(int i) {
        touch();
        return super.removeEdge(i);
    } //
    
} // end of class ExternalNode
