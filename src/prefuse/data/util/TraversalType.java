/**
 * 
 */
package prefuse.data.util;

public enum TraversalType {
    NODE_TRAVERSAL(true,false),
    EDGE_TRAVERSAL(false,true),
    NODE_AND_EDGE_TRAVERSAL(true,true);
    private final boolean traverseNodes;
    private final boolean traverseEdges;
    TraversalType(boolean traverseNodes, boolean traverseEdges) {
    	this.traverseNodes = traverseNodes;
    	this.traverseEdges = traverseEdges;
    }
	public boolean isTraverseNodes() {
		return traverseNodes;
	}
	public boolean isTraverseEdges() {
		return traverseEdges;
	}

}