package edu.berkeley.guir.prefuse;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.event.ControlListener;
import edu.berkeley.guir.prefuse.event.PrefuseControlEventMulticaster;
import edu.berkeley.guir.prefuse.render.Renderer;

/**
 * Component that provides an interactive visualization of a graph.
 * 
 * Apr 22, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class Display extends Canvas {

	protected Pipeline        m_pipeline;
	protected ItemRegistry    m_itemRegistry;
	protected ControlListener m_listener;
	protected BufferedImage   m_offscreen;
    
    protected AffineTransform m_transform, m_itransform;
    protected Point2D m_tmpPoint = new Point2D.Double();
    
    protected double frameRate;
    private int  nframes = 0;
    private int  sampleInterval = 10;
    private long mark = -1L;
	
	/**
	 * Constructor. Creates a new display instance.
	 */
	public Display() {
		InputEventCapturer mec = new InputEventCapturer();
		addMouseListener(mec);
		addMouseMotionListener(mec);
		addMouseWheelListener(mec);
		addKeyListener(mec);
        // XXX DEBUG
//        try {
//            setTransform(AffineTransform.getRotateInstance(Math.PI/6));
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
	} //

	/**
	 * Set the size of the Display.
	 * @see java.awt.Component#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		m_offscreen = null;
		super.setSize(width, height);
	} //
	
	/**
	 * Set the size of the Display.
	 * @see java.awt.Component#setSize(java.awt.Dimension)
	 */
	public void setSize(Dimension d) {
		m_offscreen = null;
		super.setSize(d);
	} //

	/**
	 * Returns the pipeline associated with this Display instance.
	 * @return the pipeline
	 */
	public Pipeline getPipeline() {
		return m_pipeline;
	} //

	/**
	 * Sets the pipeline associated with this Display instance.
	 * @return the new associated pipeline
	 */
	public void setPipeline(Pipeline pipeline) {
		m_pipeline = pipeline;
		m_itemRegistry = pipeline.getItemRegistry();
	} //

    public void setTransform(AffineTransform transform) 
        throws NoninvertibleTransformException
    {
        m_transform = transform;
        m_itransform = m_transform.createInverse();
    } //
    
    public AffineTransform getTransform() {
        return m_transform;
    } //
    
    public AffineTransform getInverseTransform() {
        return m_itransform;
    } //
    
	/**
	 * Returns the offscreen buffer used by this component for 
	 *  double-buffering.
	 * @return the offscreen buffer
	 */
	public BufferedImage getOffscreenBuffer() {
		return m_offscreen;
	} //
	
	protected BufferedImage getNewOffscreenBuffer() {
		return (BufferedImage)createImage(getSize().width, getSize().height);
	} //
	
	public void update(Graphics g) {
		paint(g);
	} //

	protected void paintBufferToScreen(Graphics g) {
		g.drawImage(m_offscreen, 0, 0, null);
	} //

	/**
	 * Immediately repaints the contents of the offscreen buffer
	 * to the screen. This bypasses the usual rendering loop.
	 */
	public void repaintImmediate() {
		Graphics g = this.getGraphics();
		if (g != null && m_offscreen != null) {
			paintBufferToScreen(g);
		}
	} //

    protected void prepareGraphics(Graphics2D g) {
        if ( m_transform != null )
            g.setTransform(m_transform);
        setRenderingHints(g);
    } //
    
	/**
	 * Sets the rendering hints that should be used while drawing
	 * the visualization to the screem.
	 * @param g
	 */
	protected void setRenderingHints(Graphics2D g) {
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		//					RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	} //
	
	/**
	 * Paint routine called <i>before</i> items are drawn. Subclasses should
	 * override this method to perform custom drawing.
	 * @param g
	 */
	protected void prePaint(Graphics2D g) {
	} //

	/**
	 * Paint routine called <i>after</i> items are drawn. Subclasses should
	 * override this method to perform custom drawing.
	 * @param g
	 */
	protected void postPaint(Graphics2D g) {
	} //

	/**
	 * Draws the visualization to the screen. Draws each visible item to the
	 * screen in a rendering loop. Rendering order can be controlled by adding
	 * the desired Comparator to this visualization's ItemRegistry.
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		if (m_offscreen == null) {
			m_offscreen = getNewOffscreenBuffer();
		}
		Graphics2D g2D = (Graphics2D) m_offscreen.getGraphics();

		// paint background
		g2D.setColor(this.getBackground());
		Dimension d = this.getSize();
		g2D.fillRect(0, 0, d.width, d.height);

		prepareGraphics(g2D);
		prePaint(g2D);
        
		g2D.setColor(Color.BLACK);
		synchronized (m_itemRegistry) {
			Iterator items = m_itemRegistry.getItems();
			while (items.hasNext()) {
				GraphItem gi = (GraphItem) items.next();
				Renderer renderer = gi.getRenderer();
				renderer.render(g2D, gi);
			}
		}

		postPaint(g2D);

		paintBufferToScreen(g);		
		g2D.dispose();
        
        // compute frame rate
        nframes++;
        if ( mark < 0 ) {
            mark = System.currentTimeMillis();
            nframes = 0;
        } else if ( nframes == sampleInterval ){
            long t = System.currentTimeMillis();
            frameRate = (1000.0*nframes)/(t-mark);
            mark = t;
            nframes = 0;
        }
	} //

	public void clearRegion(Rectangle r) {
		Graphics2D g2D = (Graphics2D) m_offscreen.getGraphics();
		if (g2D != null) {
			g2D.setColor(this.getBackground());
			g2D.fillRect(r.x, r.y, r.width, r.height);
		}
	} //

	/**
	 * Draws a single item to the <i>offscreen</i> display
	 * buffer. Useful for incremental drawing. Call the repaintImmediate()
	 * method to have these changes directly propagate to the screen.
	 * @param item
	 */
	public void drawItem(GraphItem item) {
		Graphics2D g2D = (Graphics2D) m_offscreen.getGraphics();
		if (g2D != null) {
            prepareGraphics(g2D);
			item.getRenderer().render(g2D, item);
		}
	} //

	/**
	 * Adds a ControlListener to receive all input events on GraphItems.
	 * @param cl the listener to add.
	 */
	public void addControlListener(ControlListener cl) {
		m_listener = PrefuseControlEventMulticaster.add(m_listener, cl);
	} //

	/**
	 * Removes a registered ControlListener.
	 * @param cl the listener to remove.
	 */
	public void removeControlListener(ControlListener cl) {
		m_listener = PrefuseControlEventMulticaster.remove(m_listener, cl);
	} //

	/**
	 * Returns the GraphItem located at (x,y).
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the GraphItem located at (x,y), if any
	 */
	public GraphItem findItem(Point p) {
        Point2D p2 = (m_itransform==null ? p : 
                        m_itransform.transform(p, m_tmpPoint));
		synchronized (m_itemRegistry) {
			Iterator items = m_itemRegistry.getItemsReversed();
			while (items.hasNext()) {
				GraphItem gi = (GraphItem) items.next();
				Renderer r = gi.getRenderer();
				if (r != null && r.locatePoint(p2, gi)) {
					return gi;
				}
			}
		}
		return null;
	} //

	/**
	 * Captures all mouse and key events on the display, detects relevant 
	 * GraphItems, and informs ControlListeners.
	 * 
	 * TODO ? Improve event handling.
	 */
	public class InputEventCapturer
		implements MouseMotionListener, MouseWheelListener, MouseListener, KeyListener {

		private GraphItem activeGI = null;
		private boolean mouseDown = false;

		public void mouseDragged(MouseEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemDragged(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.mouseDragged(e);
			}
		} //

		public void mouseMoved(MouseEvent e) {
			boolean earlyReturn = false;
			//check if we've gone over any item
			GraphItem g = findItem(e.getPoint());
			if (m_listener != null && activeGI != null && activeGI != g) {
				m_listener.itemExited(activeGI, e);
				earlyReturn = true;
			}
			if (m_listener != null && g != null && g != activeGI) {
				m_listener.itemEntered(g, e);
				earlyReturn = true;
			}
			activeGI = g;
			if ( earlyReturn ) return;
			
			if ( m_listener != null && g != null && g == activeGI ) {
				m_listener.itemMoved(g, e);
			}
			if ( m_listener != null && g == null ) {
				m_listener.mouseMoved(e);
			}
		} //

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemWheelMoved(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.mouseWheelMoved(e);
			}
		} //

		public void mouseClicked(MouseEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemClicked(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.mouseClicked(e);
			}
		} //

		public void mousePressed(MouseEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemPressed(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.mousePressed(e);
			}
		} //

		public void mouseReleased(MouseEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemReleased(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.mouseReleased(e);
			}
		} //

		public void mouseEntered(MouseEvent e) {
			if ( m_listener != null ) {
				m_listener.mouseEntered(e);	
			}
		} //

		public void mouseExited(MouseEvent e) {
			if (m_listener != null && !mouseDown && activeGI != null) {
				//we've left the component and an item is active, deactivate it
				m_listener.itemExited(activeGI, e);
				activeGI = null;
			}
			if ( m_listener != null ) {
				m_listener.mouseExited(e);
			}
		} //

		public void keyPressed(KeyEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemKeyPressed(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.keyPressed(e);
			}
		} //

		public void keyReleased(KeyEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemKeyReleased(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.keyReleased(e);
			}
		} //

		public void keyTyped(KeyEvent e) {
			if (m_listener != null && activeGI != null) {
				m_listener.itemKeyTyped(activeGI, e);
			} else if ( m_listener != null ) {
				m_listener.keyTyped(e);
			}
		} //
	} // end of inner class MouseEventCapturer

} // end of class Display
