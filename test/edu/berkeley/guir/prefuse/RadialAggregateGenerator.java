package edu.berkeley.guir.prefuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.collections.BreadthFirstTreeIterator;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.HDirTreeReader;
import edu.berkeley.guir.prefuse.pipeline.DefaultPipelineManager;
import edu.berkeley.guir.prefuse.pipeline.Filter;
import edu.berkeley.guir.prefuse.pipeline.FisheyeTreeFilter;
import edu.berkeley.guir.prefuse.pipeline.RadialTreeLayout;
import edu.berkeley.guir.prefuse.pipeline.TreeEdgeFilter;
import edu.berkeley.guir.prefuse.render.Renderer;

/**
 * Generates pre-rendered aggregate images.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class RadialAggregateGenerator {

	//public static final String SAVE_DIR = "aggregates/";
	public static final String SAVE_DIR = "chi_aggregates/";
	public static final String TREE_ORGCHART = "etc/orgchart-parc.txt";
	public static final String TREE_INTERNET = "etc/bfs_tree.txt";
	public static final String TREE_DMOZ = "etc/odp.txt";
	public static final String TREE_CHI97 = "etc/chitest.hdir";
	
	public static final int RADIUS_INC = 25;

	//public static final String nameField = "FullName";
	public static final String nameField = "Text";
	public static final String labelField = "Key";
		
	public ItemRegistry registry;
	public Tree tree;
	public Pipeline pipeline;
	public Display display;
	public RadialTreeLayout layout;
	public Color bgcolor = Color.WHITE;
	public int bgred, bggreen, bgblue;
	
	public List nodeList = new LinkedList();
	public Set  aggSet   = new HashSet();
	
	public static void main(String[] args) {
		RadialAggregateGenerator ag = new RadialAggregateGenerator();
		ag.renderAggregates();
		System.exit(0);
	} //
	
	public RadialAggregateGenerator() {
		try {
			bgcolor = new Color(255,255,255,0);
			bgred   = bgcolor.getRed();
			bggreen = bgcolor.getGreen();
			bgblue  = bgcolor.getBlue();
			
			//String inputFile = TREE_ORGCHART;
			//tree = new TabDelimitedTreeReader().loadTree(inputFile);
			tree = new HDirTreeReader().loadTree(TREE_CHI97);

			display = new Display() {
				public void paint(Graphics g) {
					m_offscreen = (BufferedImage)createImage(getSize().width,getSize().height);
					Graphics2D g2D = (Graphics2D) m_offscreen.getGraphics();
	
					prePaint(g2D);
	
					g2D.setColor(Color.BLACK);
					AffineTransform baseTransform = g2D.getTransform();
					synchronized ( m_registry ) {
					  Iterator items = m_registry.getItems();
					  while (items.hasNext()) {
						VisualItem vi = (VisualItem) items.next();
						Renderer renderer = vi.getRenderer();
						g2D.setTransform(baseTransform);
						renderer.render(g2D, vi);
					  }
					}
	
					postPaint(g2D);
	
					g.drawImage(m_offscreen,0,0,null);
					g2D.dispose();
				} //
			};				
			pipeline = new Pipeline(tree, display);
			pipeline.setPipelineManager(new DefaultPipelineManager());
			registry = pipeline.getItemRegistry();
			display.setSize(4000,4000);
			display.setBackground(bgcolor);
						
			Filter       nodeFilter    = new FisheyeTreeFilter();
			             layout        = new RadialTreeLayout();
			Filter       edgeFilter    = new TreeEdgeFilter();

			pipeline.addComponent(nodeFilter);
			pipeline.addComponent(layout);
			pipeline.addComponent(edgeFilter);
			
			pipeline.setIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI, Integer.MIN_VALUE+1);
			layout.setRadiusIncrement(RADIUS_INC);
			
			TreeNode focus = tree.getRoot();
			registry.addFocus(focus);

			JFrame frame = new JFrame("PrefuseDemo");
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			frame.getContentPane().add(display, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
	public void renderAggregates() {
		File f = new File(SAVE_DIR);
		if ( !f.exists() ) { f.mkdirs(); }
		
		Tree subtree;
		nodeList.clear();
		aggSet.clear();
		double st, et;
		
		int maxDepth = -1;
		Iterator iter = new BreadthFirstTreeIterator(tree.getRoot());
		while ( iter.hasNext() ) {
			TreeNode n = (TreeNode)iter.next();
			nodeList.add(n);
			int d = tree.getDepth(n);
			if ( d > maxDepth ) { maxDepth = d;	}
		}
		System.err.println("maxDepth = " + maxDepth);
		
		Iterator rootIter = nodeList.iterator();
		while ( rootIter.hasNext() ) {
			TreeNode root = (TreeNode)rootIter.next();
			tree.switchRoot(root);
			
			Display disp = new Display() {
				public void paint(Graphics g) {
				} //
			};
			disp.setSize(4000,4000);
			Pipeline pline = new Pipeline(tree, disp); 
			pline.setPipelineManager(new DefaultPipelineManager());
			Filter               nodeFilter    = new FisheyeTreeFilter();
			RadialTreeLayout	 nodeLayout    = new RadialTreeLayout();
			nodeLayout.setRadiusIncrement(RADIUS_INC);
			pline.addComponent(nodeFilter);
			pline.addComponent(nodeLayout);
			pline.setIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI, -2);
			pline.getItemRegistry().addFocus(root);
			
			iter = new BreadthFirstTreeIterator(tree.getRoot());
			while ( iter.hasNext() ) {
				TreeNode n = (TreeNode)iter.next();
				if ( n.getNumChildren() > 0 ) {
					// key is "label_parentLabel"
					TreeNode p = n.getParent();
					String key = n.getAttribute(labelField) + "_"
						+ ( p == null ? null : p.getAttribute(labelField));
					
					if ( !aggSet.contains(key) ) {
						int depth = tree.getDepth(n);
						//System.out.println("--> minDOI = " + -1*depth);
						pline.setIntegerAttribute(FisheyeTreeFilter.ATTR_MIN_DOI, -1*depth);
						//System.out.println("--> $%$%$%$%$%$");
						pline.getItemRegistry().setFocus(root);
						//System.out.println("--> $%$%$%$%$%$");
						pline.runPipeline();
						
						subtree = new Tree(n);
						pipeline.setGraph(subtree);
						layout.setStartRadius(2*RADIUS_INC*depth);
						double angw = nodeLayout.getAngularWidth(registry.getNodeItem(n));
						System.out.println("angular width: " + n.getAttribute(nameField) + " = " + angw);
						st = (Math.PI-angw)/2.0;
						et = st + angw;
						layout.setStartTheta(st);
						layout.setEndTheta(et);
						registry.setFocus(n);
						pipeline.runPipeline();
						
						try {
							Thread.sleep(1000);
						} catch ( InterruptedException e ) {}
						
						NodeItem item = registry.getNodeItem(n);
						saveImage(SAVE_DIR+key+".png",display,(int)item.getX(),(int)item.getY());
						aggSet.add(key);
					}
				}
			}
		}
		System.out.println("done!");
	} //
	
	public void saveImage(String filename, Display display, int cx, int cy) {
		try {		
			BufferedImage image = display.getOffscreenBuffer();
			Rectangle r = getImageBounds(image, cx, cy);
		
			if ( r.width < 0 || r.height < 0 ) {
				System.out.println("out of area (x,y,w,h)=("+r.x+","+r.y+","+r.width+","+r.height+")");
			}
		
			Image saveImage = image.getSubimage(r.x,r.y,r.width,r.height);
			saveImage = makeTransparent((BufferedImage)saveImage);
			saveImage = toBufferedImage(saveImage);
			writeImage(saveImage, filename);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //

	public Rectangle getImageBounds(BufferedImage image, int cx, int cy) {
		int w, h, x0, y0, x1, x2, y1, y2, x, y, pixel;
		w = image.getWidth();
		h = image.getHeight();
		x0 = 0; y0 = 0; x1 = w; y1 = h;	x2 = 0; y2 = 0;
		int[] pixels = new int[w * h];
		PixelGrabber pg = new PixelGrabber(image, x0, y0, w, h, pixels, 0, w);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("interrupted waiting for pixels!");
			return null;
		}
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				x = x0+i; y = y0+j;
				pixel = pixels[j*w+i];
				int alpha = (pixel >> 24) & 0xff;
				int red   = (pixel >> 16) & 0xff;
				int green = (pixel >>  8) & 0xff;
				int blue  = (pixel      ) & 0xff;
				if ( red != bgred || green != bggreen || blue != bgblue ) {
					if ( x < x1 ) x1 = x;
					if ( x > x2 ) x2 = x;
					if ( y < y1 ) y1 = y;
					if ( y > y2 ) y2 = y;
				}
			}
		}
		int dx1 = cx - x1;
		int dx2 = x2 - cx;
		if ( dx1 > dx2 ) {
			x2 = cx + dx1;
		} else {
			x1 = cx - dx2;
		}
		return new Rectangle(x1,y1,x2-x1,y2-y1);
	} //
	
	public Image makeTransparent(BufferedImage image) {
		RGBImageFilter filter = new MyImageFilter();
		FilteredImageSource src = new FilteredImageSource(image.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(src);
	} //
	
	public void writeImage(Image image, String filename) {
		try {
			ImageIO.write((RenderedImage)image, "png", new File(filename));
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	} //
	
	 //	This method returns a buffered image with the contents of an image
	 public static BufferedImage toBufferedImage(Image image) {
		 if (image instanceof BufferedImage) {
			 return (BufferedImage)image;
		 }
    
		 // This code ensures that all the pixels in the image are loaded
		 image = new ImageIcon(image).getImage();
    
		 // Determine if the image has transparent pixels; for this method's
		 // implementation, see e665 Determining If an Image Has Transparent Pixels
		 boolean hasAlpha = true;
    
		 // Create a buffered image with a format that's compatible with the screen
		 BufferedImage bimage = null;
		 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 try {
			 // Determine the type of transparency of the new buffered image
			 int transparency = Transparency.OPAQUE;
			 if (hasAlpha) {
				 transparency = Transparency.BITMASK;
			 }
    
			 // Create the buffered image
			 GraphicsDevice gs = ge.getDefaultScreenDevice();
			 GraphicsConfiguration gc = gs.getDefaultConfiguration();
			 bimage = gc.createCompatibleImage(
				 image.getWidth(null), image.getHeight(null), transparency);
		 } catch (HeadlessException e) {
			 // The system does not have a screen
		 }
    
		 if (bimage == null) {
			 // Create a buffered image using the default color model
			 int type = BufferedImage.TYPE_INT_RGB;
			 if (hasAlpha) {
				 type = BufferedImage.TYPE_INT_ARGB;
			 }
			 bimage = new BufferedImage(image.getWidth(null),
 			 image.getHeight(null), type);
		 }
    
		 // Copy image to buffered image
		 Graphics g = bimage.createGraphics();
    
		 // Paint the image onto the buffered image
		 g.drawImage(image, 0, 0, null);
		 g.dispose();
    
		 return bimage;
	 } //

	class MyImageFilter extends RGBImageFilter {
			public MyImageFilter() {
				canFilterIndexColorModel = true;
			} //
			
			public int filterRGB(int x, int y, int rgb) {
				int alpha = (rgb >> 24) & 0xff;
				int red   = (rgb >> 16) & 0xff;
				int green = (rgb >>  8) & 0xff;
				int blue  = (rgb      ) & 0xff;
				if ( red == bgred && green == bggreen && blue == bgblue ) {
					return rgb & 0x00ffffff;
				} else {
					return rgb;
				}
			} //
	} //
		
} // end of classs AggregateGenerator
