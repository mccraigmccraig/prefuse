package edu.berkeley.guir.prefuse.action;

/**
 * Signals the <code>ItemRegistry</code> to perform a garbage collection 
 * operation. The class type of the <code>GraphItem</code> to garbage
 * collect must be specified through the constructor and/or 
 * <code>addItemClass()</code> method.
 * 
 * Jul 15, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class GarbageCollector extends Filter {
	
    /**
     * Creates a new instance that signals garbage collection for the given
     * item class.
     * @param itemClass the item class to garbage collect
     */
	public GarbageCollector(String itemClass) {
		super(itemClass, true);
	} //

    /**
     * Creates a new instance that signals garbage collection for the given
     * item classes.
     * @param itemClasses the item classes to garbage collect
     */
	public GarbageCollector(String[] itemClasses) {
	    super(itemClasses, true);
    } //

} // end of class GarbageCollector
