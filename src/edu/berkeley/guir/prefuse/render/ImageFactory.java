package edu.berkeley.guir.prefuse.render;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.TreeNode;

/**
 * <p>Controls loading and management of images. Includes a size-configurable
 * LRU cache for managing loaded images. Also supports optional image scaling
 * of loaded images to cut down on memory and visualization operation costs.
 * </p>
 * 
 * <p>By default images are loaded upon first request. Use the
 * <code>preloadImages()</code> method to load images before they are
 * requested.</p>
 * 
 * @author newbergr
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ImageFactory {
	
	private int m_imageCacheSize = 500;
	private int m_maxImageWidth  = 100;
	private int m_maxImageHeight = 100;
	
	//a nice LRU cache courtesy of java 1.4
	private Map imageCache =
		new LinkedHashMap((int) (m_imageCacheSize + 1 / .75F), .75F, true) {
			public boolean removeEldestEntry(Map.Entry eldest) {
				return size() > m_imageCacheSize;
			}
		};

	private final Component component = new Component() {};
	private final MediaTracker tracker = new MediaTracker(component);
	private int nextTrackerID = 0;

	/**
	 * Constructor. Assumes no scaling of loaded images.
	 */
	public ImageFactory() {
		this(-1,-1);
	} //
	
	/**
	 * Constructor. This instance will scale loaded images if they exceed the
	 * threshold arguments.
	 * @param maxImageWidth the maximum width of input images (-1 means no limit)
	 * @param maxImageHeight the maximum height of input images (-1 means no limit)
	 */
	public ImageFactory(int maxImageWidth, int maxImageHeight) {
		setMaxImageDimensions(maxImageWidth, maxImageHeight);
	} //

	/**
	 * Sets the maximum image dimensions of loaded images, images larger than
	 * these limits will be scaled to fit within bounds.
	 * @param width the maximum width of input images (-1 means no limit)
	 * @param height the maximum height of input images (-1 means no limit)
	 */
	public void setMaxImageDimensions(int width, int height) {
		m_maxImageWidth  = width;
		m_maxImageHeight = height;
	} //

	/**
	 * Sets the capacity of this factory's image cache
	 * @param size the new size of the image cache
	 */
	public void setImageCacheSize(int size) {
		m_imageCacheSize = size;
	} //

	/**
	 * Get the image associated with the given location string. If the image
	 * has already been loaded, it simply will return the image, otherwise it
	 * will load it from the specified location.
	 * 
	 * The imageLocation argument must be a valid resource string.
	 * 
	 * @param imageLocation the image location as a resource string.
	 * @return the corresponding image, if available
	 */
	public Image getImage(String imageLocation) {
		Image image = (Image) imageCache.get(imageLocation);
		if (image == null) {
			URL imageURL = getImageURL(imageLocation);
			if ( imageURL == null ) {
				if ( !imageLocation.startsWith("/") ) {
					return getImage("/" + imageLocation);					
				} else {
					System.err.println("Null image: " + imageLocation);
					return null;
				}
			}
			image = Toolkit.getDefaultToolkit().createImage(imageURL);

			//block for image to load. TODO: decide whether to do this asynchronously
			waitForImage(image);
			if ( m_maxImageWidth > -1 || m_maxImageHeight > -1 )
				image = getScaledImage(image);
			imageCache.put(imageLocation, image);			
		}
		return image;
	} //

	/**
	 * Wait for an image to load.
	 * @param image the image to wait for
	 */
	protected void waitForImage(Image image) {
		int id = ++nextTrackerID;
		tracker.addImage(image, id);
		try {
			tracker.waitForID(id, 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tracker.removeImage(image, id);
	} //

	/**
	 * Get the image associated with the provided aggregate. Assumes the
	 * aggregate represents an elided subtree of a DefaultTreeNode instance.
	 * @param aItem the aggregate item
	 * @return the corresponding image, if available.
	 */
	public Image getImage(AggregateItem aItem) {
		//TODO: something in this hash lookup is slow. 
		// suspects: URL .equals or .hashCode, or LRU list maintenance.
		String imageLocation = getImageLocation(aItem);
		return getImage(imageLocation);
	} //

	/**
	 * Maps from an aggregate item to a location string, using the convention
	 * of the AggregateGenerator classes.
	 * @param aItem AggregateItem to map from
	 * @return the image location string
	 */
	protected String getImageLocation(AggregateItem aItem) {
		TreeNode n = (TreeNode) aItem.getEntity();
		TreeNode p = n.getParent();
		String key = n.getAttribute("Key") + "_" + (p == null ? null : p.getAttribute("Key"));
		String url = "/aggregates/" + key + ".png";
		return url.intern();
	} //

	/**
	 * Returns the URL for a location specified as a resource string.
	 * @param location the resource location string
	 * @return the corresponding URL
	 */
	protected URL getImageURL(String location) {
        if ( location.startsWith("http:/") ||
             location.startsWith("ftp:/")  ||
             location.startsWith("file:/") ) {
            try {
                return new URL(location);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return ImageFactory.class.getResource(location);
	} //
	
	/**
	 * Scales an image to fit within the current size thresholds.
	 * @param img the image to scale
	 * @return the scaled image
	 */
	protected Image getScaledImage(Image img) {		
		// resize image, if necessary, to conserve memory
		//  and reduce future scaling time
		int w = img.getWidth(null) - m_maxImageWidth;
		int h = img.getHeight(null) - m_maxImageHeight;

		if ( w > h && w > 0 && m_maxImageWidth > -1 ) {
			Image scaled = img.getScaledInstance(m_maxImageWidth, -1, Image.SCALE_SMOOTH);
			img.flush(); waitForImage(scaled);
			return scaled;
		} else if ( h > 0 && m_maxImageHeight > -1 ) {
			Image scaled = img.getScaledInstance(-1, m_maxImageHeight, Image.SCALE_SMOOTH);
			img.flush(); waitForImage(scaled);				
			return scaled;
		} else {
			return img;
		}
	} //
	
	/**
	 * Preloads images for use in a visualization. Images to load are
	 * determined by taking objects from the given iterator and retrieving
	 * the attribute of the specified value. The items in the iterator must
	 * be instances of either <code>Entity</code> or <code>VisualItem</code>.
	 * Images are loaded in the order specified by the iterator until the
	 * the iterator is empty or the maximum image cache size is met. Thus
	 * higher priority images should appear sooner in the iteration.
	 * @param iter an Iterator of <code>Entity</code> and/or 
	 *  <code>VisualItem</code> instances
	 * @param attr the attribute that contains the image location
	 */
	public void preloadImages(Iterator iter, String attr) {
		String loc = null;
		while ( iter.hasNext() && imageCache.size() <= m_imageCacheSize ) {
			// get the string describing the image location
			Object o = iter.next();
			if ( o instanceof Entity ) {
				loc = ((Entity)o).getAttribute(attr);
			} else if ( o instanceof VisualItem ) {
				loc = ((VisualItem)o).getAttribute(attr);
			}
			if ( loc != null ) {
				getImage(loc);
			}
		}
	} //
	
} // end of class ImageFactory
