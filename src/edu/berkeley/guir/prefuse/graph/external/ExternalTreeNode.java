package edu.berkeley.guir.prefuse.graph.external;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ExternalTreeNode extends DefaultTreeNode implements ExternalEntity {

    protected static final int LOAD_CHILDREN = 1;
    protected static final int LOAD_PARENT   = 2;
    protected static final int LOAD_ALL      = LOAD_CHILDREN | LOAD_PARENT;
    
    protected GraphLoader  m_loader;

    protected long    m_access;
    protected boolean m_ploaded = false;
    protected boolean m_ploadStarted = false;
    protected boolean m_loaded = false;
    protected boolean m_loadStarted = false;
    
    protected void checkLoadedStatus(int type) {
        touch();
        if ( (type & LOAD_CHILDREN) > 0 && !m_loadStarted ) {
            m_loadStarted = true;
            m_loader.loadChildren(this);
        }
        if ( (type & LOAD_PARENT) > 0 && !m_ploadStarted ) {
            m_ploadStarted = true;
            m_loader.loadParent(this);
        }
    } //
    
    public void setLoader(GraphLoader gl) {
        m_loader = gl;
    } //
    
    void setChildrenLoaded(boolean s) {
        m_loaded = s;
        m_loadStarted = s;
    } //
    
    void setParentLoaded(boolean s) {
        m_ploaded = s;
        m_ploadStarted = s;
    }

    public boolean isParentLoaded() {
        return m_ploaded;
    } //
    
    public boolean isChildrenLoaded() {
        return m_loaded;
    } //
    
    public void touch() {
        m_access = System.currentTimeMillis();
    } //
    
    // ========================================================================
    // == PROXIED TREE NODE METHODS ===========================================
    
    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#addChild(edu.berkeley.guir.prefuse.graph.Edge)
     */
    public boolean addChild(Edge e) {
        return super.addChild(e);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#addChild(int, edu.berkeley.guir.prefuse.graph.Edge)
     */
    public boolean addChild(int idx, Edge e) {
        return super.addChild(idx, e);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getChild(int)
     */
    public TreeNode getChild(int idx) {
        return super.getChild(idx);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getChildCount()
     */
    public int getChildCount() {
        return super.getChildCount();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getChildEdge(int)
     */
    public Edge getChildEdge(int i) {
        return super.getChildEdge(i);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getChildEdges()
     */
    public Iterator getChildEdges() {
        return super.getChildEdges();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getChildIndex(edu.berkeley.guir.prefuse.graph.Edge)
     */
    public int getChildIndex(Edge e) {
        return super.getChildIndex(e);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getChildIndex(edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public int getChildIndex(TreeNode c) {
        return super.getChildIndex(c);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getChildren()
     */
    public Iterator getChildren() {
        return super.getChildren();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getNextSibling()
     */
    public TreeNode getNextSibling() {
        return super.getNextSibling();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getDescendantCount()
     */
    public int getDescendantCount() {
        return super.getDescendantCount();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getParent()
     */
    public TreeNode getParent() {
        return super.getParent();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getParentEdge()
     */
    public Edge getParentEdge() {
        return super.getParentEdge();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#getPreviousSibling()
     */
    public TreeNode getPreviousSibling() {
        return super.getPreviousSibling();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#isChild(edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public boolean isChild(TreeNode c) {
        return super.isChild(c);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#isChildEdge(edu.berkeley.guir.prefuse.graph.Edge)
     */
    public boolean isChildEdge(Edge e) {
        return super.isChildEdge(e);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#isDescendant(edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public boolean isDescendant(TreeNode n) {
        return super.isDescendant(n);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#isSibling(edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public boolean isSibling(TreeNode n) {
        return super.isSibling(n);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeAllAsChildren()
     */
    public void removeAllAsChildren() {
        super.removeAllAsChildren();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeAllChildren()
     */
    public void removeAllChildren() {
        super.removeAllChildren();
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeAsChild(int)
     */
    public TreeNode removeAsChild(int idx) {
        return super.removeAsChild(idx);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeAsChild(edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public boolean removeAsChild(TreeNode n) {
        return super.removeAsChild(n);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeChild(int)
     */
    public TreeNode removeChild(int idx) {
        return super.removeChild(idx);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeChild(edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public boolean removeChild(TreeNode n) {
        return super.removeChild(n);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeChildEdge(edu.berkeley.guir.prefuse.graph.Edge)
     */
    public boolean removeChildEdge(Edge e) {
        return super.removeChildEdge(e);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#removeChildEdge(int)
     */
    public Edge removeChildEdge(int idx) {
        return super.removeChildEdge(idx);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#setAsChild(int, edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public boolean setAsChild(int idx, TreeNode c) {
        return super.setAsChild(idx,c);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#setAsChild(edu.berkeley.guir.prefuse.graph.TreeNode)
     */
    public boolean setAsChild(TreeNode c) {
        return super.setAsChild(c);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#setDescendantCount(int)
     */
    public void setDescendantCount(int count) {
        super.setDescendantCount(count);
    } //

    /**
     * @see edu.berkeley.guir.prefuse.graph.TreeNode#setParentEdge(edu.berkeley.guir.prefuse.graph.Edge)
     */
    public void setParentEdge(Edge e) {
        super.setParentEdge(e);
    } //

} // end of class ExternalTreeNode
