package edu.berkeley.guir.prefuse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
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

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import edu.berkeley.guir.prefuse.event.ControlListener;
import edu.berkeley.guir.prefuse.event.ControlEventMulticaster;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.util.Clip;
import edu.berkeley.guir.prefuse.util.ToolTipManager;

/**
 * User interface component that provides an interactive visualization 
 * of a graph. The Display is responsible for drawing items to the
 * screen and providing callbacks for user interface actions such as
 * mouse and keyboard events. A Display must be associated with an
 * ItemRegistry from which it pulls the items to visualize.
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class Display extends JComponent {

	protected ItemRegistry    m_registry;
	protected ControlListener m_listener;
	protected BufferedImage   m_offscreen;
    protected Clip            m_clip, m_pclip, m_cclip;
    
    protected AffineTransform m_transform  = new AffineTransform();
    protected AffineTransform m_itransform = new AffineTransform();
    protected Point2D m_tmpPoint = new Point2D.Double();
    
    protected double frameRate;
    protected int  nframes = 0;
    private int  sampleInterval = 10;
    private long mark = -1L;
    
    private JTextComponent m_editor;
    private boolean        m_editing;
    private GraphItem      m_editItem;
    private String         m_editAttribute;
    
    private ToolTipManager m_ttipManager;
	
	/**
	 * Constructor. Creates a new display instance.
	 */
	public Display() {
        setDoubleBuffered(false);
        setBackground(Color.WHITE);
        
        // initialize text editor
        m_editing = false;
        m_editor = new JTextField();
        m_editor.setBorder(null);
        m_editor.setVisible(false);
        this.add(m_editor);
        
        // register input event capturer
		InputEventCapturer iec = new InputEventCapturer();
		addMouseListener(iec);
		addMouseMotionListener(iec);
		addMouseWheelListener(iec);
		addKeyListener(iec);
        
        m_clip  = new Clip();
        m_pclip = new Clip();
        m_cclip = new Clip();
	} //

    public void setUseCustomTooltips(boolean s) {
        if ( s && m_ttipManager == null ) {
            m_ttipManager = new ToolTipManager(this);
            String text = super.getToolTipText();
            super.setToolTipText(null);
            m_ttipManager.setToolTipText(text);
            this.addMouseMotionListener(m_ttipManager);
        } else if ( !s && m_ttipManager != null ) {
            this.removeMouseMotionListener(m_ttipManager);
            String text = m_ttipManager.getToolTipText();
            m_ttipManager.setToolTipText(null);
            super.setToolTipText(text);
            m_ttipManager = null;
        }
    } //
    
    public ToolTipManager getToolTipManager() {
        return m_ttipManager;
    } //
    
    public void setToolTipText(String text) {
        if ( m_ttipManager != null ) {
            m_ttipManager.setToolTipText(text);
        } else {
            super.setToolTipText(text);
        }
    } //
    
	/**
	 * Set the size of the Display.
	 * @see java.awt.Component#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		m_offscreen = null;
        setPreferredSize(new Dimension(width,height));
		super.setSize(width, height);
	} //
	
	/**
	 * Set the size of the Display.
	 * @see java.awt.Component#setSize(java.awt.Dimension)
	 */
	public void setSize(Dimension d) {
		m_offscreen = null;
        setPreferredSize(d);
		super.setSize(d);
	} //

    /**
     * Reshapes (moves and resizes) this component.
     */
    public void reshape(int x, int y, int w, int h) {
        m_offscreen = null;
        super.reshape(x,y,w,h);
    } //
    
    /**
     * Sets the font used by this Display. This determines the font used
     * by this Display's text editor.
     */
    public void setFont(Font f) {
        super.setFont(f);
        m_editor.setFont(f);
    } //
    
    /**
     * Returns the item registry used by this display.
     * @return this Display's ItemRegistry
     */
    public ItemRegistry getRegistry() {
        return m_registry;
    } //
    
    /**
     * Set the ItemRegistry associated with this Display. This Display
     * will render the items contained in the provided registry. If this
     * Display is already associated with a different ItemRegistry, the
     * Display unregisters itself with the previous registry.
     * @param registry the ItemRegistry to associate with this Display.
     */
    public void setRegistry(ItemRegistry registry) {
        if ( m_registry == registry ) {
            // nothing need be done
            return;
        } else if ( m_registry != null ) {
            // remove this display from it's previous registry
            m_registry.removeDisplay(this);
        }
        m_registry = registry;
        m_registry.addDisplay(this);
    } //

    // ========================================================================
    // == TRANSFORM METHODS ===================================================
    
    /**
     * Set the 2D AffineTransform (e.g., scale, shear, pan, rotate) used by
     * this display before rendering graph items. The provided transform
     * must be invertible, otherwise an expection will be thrown. For simple
     * panning and zooming transforms, you can instead use the provided
     * pan() and zoom() methods.
     */
    public void setTransform(AffineTransform transform) 
        throws NoninvertibleTransformException
    {
        m_transform = transform;
        m_itransform = m_transform.createInverse();
    } //
    
    /**
     * Returns a reference to the AffineTransformation used by this Display.
     * Changes made to this reference will likely corrupt the state of 
     * this display. Use setTransform() to safely update the transform state.
     * @return the AffineTransform
     */
    public AffineTransform getTransform() {
        return m_transform;
    } //
    
    /**
     * Returns a reference to the inverse of the AffineTransformation used by
     * this display. Changes made to this reference will likely corrupt the
     * state of this display.
     * @return the inverse AffineTransform
     */
    public AffineTransform getInverseTransform() {
        return m_itransform;
    } //
    
    /**
     * Gets the absolute co-ordinate corresponding to the given screen
     * co-ordinate.
     * @param screen the screen co-ordinate to transform
     * @param abs a reference to put the result in. If this is the same
     *  object as the screen co-ordinate, it will be overridden safely. If
     *  this value is null, a new Point2D instance will be created and 
     *  returned.
     * @return the point in absolute co-ordinates
     */
    public Point2D getAbsoluteCoordinate(Point2D screen, Point2D abs) {
        return m_itransform.transform(screen, abs);
    } //
    
    /**
     * Pans the view provided by this display in screen coordinates.
     * @param dx the amount to pan along the x-dimension, in pixel units
     * @param dy the amount to pan along the y-dimension, in pixel units
     */
    public void pan(double dx, double dy) {
        double panx = ((double)dx) / m_transform.getScaleX();
        double pany = ((double)dy) / m_transform.getScaleY();
        panAbs(panx,pany);
    } //
    
    /**
     * Pans the view provided by this display in absolute (i.e. non-screen)
     * coordinates.
     * @param dx the amount to pan along the x-dimension, in absolute co-ords
     * @param dy the amount to pan along the y-dimension, in absolute co-ords
     */
    public void panAbs(double dx, double dy) {
        m_transform.translate(dx, dy);
        try {
            m_itransform = m_transform.createInverse();
        } catch ( Exception e ) { /*will never happen here*/ }
    } //

    /**
     * Zooms the view provided by this display by the given scale,
     * anchoring the zoom at the specified point in screen coordinates.
     * @param p the anchor point for the zoom, in screen coordinates
     * @param scale the amount to zoom by
     */
    public void zoom(final Point2D p, double scale) {
        m_itransform.transform(p, m_tmpPoint);
        zoomAbs(m_tmpPoint, scale);
    } //    
    
    /**
     * Zooms the view provided by this display by the given scale,
     * anchoring the zoom at the specified point in absolute coordinates.
     * @param p the anchor point for the zoom, in absolute
     *  (i.e. non-screen) co-ordinates
     * @param scale the amount to zoom by
     */
    public void zoomAbs(final Point2D p, double scale) {;
        double zx = p.getX(), zy = p.getY();
        m_transform.translate(zx, zy);
        m_transform.scale(scale,scale);
        m_transform.translate(-zx, -zy);
        try {
            m_itransform = m_transform.createInverse();
        } catch ( Exception e ) { /*will never happen here*/ }
    } //
    
    // ========================================================================
    // == RENDERING METHODS ===================================================
    
	/**
	 * Returns the offscreen buffer used by this component for 
	 *  double-buffering.
	 * @return the offscreen buffer
	 */
	public BufferedImage getOffscreenBuffer() {
		return m_offscreen;
	} //
	
    /**
     * Creates a new buffered image to use as an offscreen buffer.
     */
	protected BufferedImage getNewOffscreenBuffer() {
        return (BufferedImage)createImage(getSize().width, getSize().height);
	} //
	
    /**
     * Updates this display
     */
	public void update(Graphics g) {
		paint(g);
	} //

    /**
     * Paints the offscreen buffer to the provided graphics context.
     * @param g the Graphics context to paint to
     */
	protected void paintBufferToScreen(Graphics g) {
        int x = 0, y = 0;
        BufferedImage img = m_offscreen;
        //if ( m_clip != null ) {
        //    x = m_clip.getX();
        //    y = m_clip.getY();
        //    img = m_offscreen.getSubimage(x,y,m_clip.getWidth(),m_clip.getHeight());
        //}
		g.drawImage(img, x, y, null);
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

    /**
     * Sets the transform of the provided Graphics context to be the
     * transform of this Display and sets the desired rendering hints.
     * @param g the Graphics context to prepare.
     */
    protected void prepareGraphics(Graphics2D g) {
        if ( m_transform != null )
            g.setTransform(m_transform);
        setRenderingHints(g);
    } //
    
	/**
	 * Sets the rendering hints that should be used while drawing
	 * the visualization to the screen. Subclasses can override
     * this method to set hints as desired.
	 * @param g the Graphics context on which to set the rendering hints
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
	 * @param g the Graphics context to draw into
	 */
	protected void prePaint(Graphics2D g) {
	} //

	/**
	 * Paint routine called <i>after</i> items are drawn. Subclasses should
	 * override this method to perform custom drawing.
	 * @param g the Graphics context to draw into
	 */
	protected void postPaint(Graphics2D g) {
	} //

	/**
	 * Draws the visualization to the screen. Draws each visible item to the
	 * screen in a rendering loop. Rendering order can be controlled by adding
	 * the desired Comparator to the Display's ItemRegistry.
	 */
	public void paintComponent(Graphics g) {
		if  (m_offscreen == null) {
			m_offscreen = getNewOffscreenBuffer();
        }
		Graphics2D g2D = (Graphics2D) m_offscreen.getGraphics();
        //Graphics2D g2D = (Graphics2D)g;
        
		// paint background
		g2D.setColor(getBackground());
		Dimension d = this.getSize();
		g2D.fillRect(0, 0, d.width, d.height);

		prepareGraphics(g2D);
		prePaint(g2D);
        
		g2D.setColor(Color.BLACK);
		synchronized (m_registry) {
            m_clip.setClip(0,0,getWidth(),getHeight());
            m_clip.transform(m_itransform);
            //m_clip.limit(0,0,getWidth(),getHeight());
//            m_cclip.setClip(Integer.MAX_VALUE, Integer.MAX_VALUE,
//                            Integer.MIN_VALUE, Integer.MIN_VALUE);
//            int count = 0;
//            Iterator items = m_registry.getItems();
//            while (items.hasNext()) {
//                GraphItem gi = (GraphItem) items.next();
//                Rectangle b = gi.getBounds();
//                m_cclip.union(b);
//                count++;
//            }
//            // update clipping region
//            if ( count == 0 )
//                m_cclip.setClip(0,0,getWidth(),getHeight());
//            m_cclip.transform(m_transform);
//            m_clip.setClip(m_cclip);
//            m_clip.union(m_pclip);
//            m_clip.limit(0,0,getWidth(),getHeight());
//            m_pclip.setClip(m_cclip);
            Iterator items = m_registry.getItems();
            while (items.hasNext()) {
                GraphItem gi = (GraphItem) items.next();
                Renderer renderer = gi.getRenderer();
                Rectangle b = renderer.getBoundsRef(gi);
                if ( m_clip.intersects(b) )
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
            //System.out.println("frameRate: " + frameRate);
        }
	} //
    
    /**
     * Clears the specified region of the display (in screen co-ordinates)
     * in the display's offscreen buffer. The cleared region is replaced 
     * with the background color. Call the repaintImmediate() method to
     * have this change directly propagate to the screen.
     * @param r a Rectangle specifying the region to clear, in screen co-ords
     */
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

    // ========================================================================
    // == CONTROL LISTENER METHODS ============================================
    
	/**
	 * Adds a ControlListener to receive all input events on GraphItems.
	 * @param cl the listener to add.
	 */
	public void addControlListener(ControlListener cl) {
		m_listener = ControlEventMulticaster.add(m_listener, cl);
	} //

	/**
	 * Removes a registered ControlListener.
	 * @param cl the listener to remove.
	 */
	public void removeControlListener(ControlListener cl) {
		m_listener = ControlEventMulticaster.remove(m_listener, cl);
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
		synchronized (m_registry) {
			Iterator items = m_registry.getItemsReversed();
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
	 */
	public class InputEventCapturer
		implements MouseMotionListener, MouseWheelListener, MouseListener, KeyListener {

		private GraphItem activeGI = null;
		private boolean mouseDown = false;
        private boolean itemDrag = false;

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
		    mouseDown = true;
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
            if ( m_listener != null && activeGI != null 
                    && mouseDown && isOffComponent(e) )
            {
                // mouse was dragged off of the component, 
                // then released, so register an exit
                m_listener.itemExited(activeGI, e);
                activeGI = null;
            }
            mouseDown = false;
		} //

		public void mouseEntered(MouseEvent e) {
			if ( m_listener != null ) {
				m_listener.mouseEntered(e);	
			}
		} //

		public void mouseExited(MouseEvent e) {
			if (m_listener != null && !mouseDown && activeGI != null) {
                // we've left the component and an item 
                // is active but not being dragged, deactivate it
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
        
        private boolean isOffComponent(MouseEvent e) {
            int x = e.getX(), y = e.getY();
            return ( x<0 || x>getWidth() || y<0 || y>getWidth() );
        } //
	} // end of inner class MouseEventCapturer
    
    
    // ========================================================================
    // == TEXT EDITING CONTROL ================================================
    
    /**
     * Returns the TextComponent used for on-screen text editing.
     * @return the TextComponent used for text editing
     */
    public JTextComponent getTextEditor() {
        return m_editor;
    } //
    
    /**
     * Sets the TextComponent used for on-screen text editing.
     * @param tc the TextComponent to use for text editing
     */
    public void setTextEditor(JTextComponent tc) {
        this.remove(m_editor);
        m_editor = tc;
        this.add(m_editor, 1);
    } //
    
    /**
     * Edit text for the given GraphItem and attribute. Presents a text
     * editing widget spaning the item's bounding box. Use stopEditing()
     * to hide the text widget. When stopEditing() is called, the attribute
     * will automatically be updated with the GraphItem.
     * @param item the GraphItem to edit
     * @param attribute the attribute to edit
     */
    public void editText(GraphItem item, String attribute) {
        if ( m_editing ) { stopEditing(); }
        Rectangle r = item.getBounds();
        
        // hacky placement code that attempts to keep text in same place
        // configured under Windows XP and Java 1.4.2b
        if ( m_editor instanceof JTextArea ) {
            r.y -= 2; r.width += 22; r.height += 2;
        } else {
            r.x += 3; r.y += 1; r.width -= 5; r.height -= 2;
        }
        r = m_transform.createTransformedShape(r).getBounds();
        Font f = getFont();
        int size = (int)Math.round(f.getSize()*m_transform.getScaleX());
        Font nf = new Font(f.getFontName(), f.getStyle(), size);
        m_editor.setFont(nf);
        
        editText(item, attribute, r);
    } //
    
    /**
     * Edit text for the given GraphItem and attribute. Presents a text
     * editing widget spaning the given bounding box. Use stopEditing()
     * to hide the text widget. When stopEditing() is called, the attribute
     * will automatically be updated with the GraphItem.
     * @param item the GraphItem to edit
     * @param attribute the attribute to edit
     * @param r Rectangle representing the desired bounding box of the text
     *  editing widget
     */
    public void editText(GraphItem nitem, String attribute, Rectangle r) {
        if ( m_editing ) { stopEditing(); }
        String txt = nitem.getAttribute(attribute);
        m_editItem = nitem;
        m_editAttribute = attribute;
        Paint c = nitem.getColor(), fc = nitem.getFillColor();
        if ( c instanceof Color )
            m_editor.setForeground((Color)c);
        if ( fc instanceof Color )
            m_editor.setBackground((Color)fc);
        editText(txt, r);
    } //
    
    /**
     * Show a text editing widget containing the given text and spanning the
     * specified bounding box. Use stopEditing() to hide the text widget. Use
     * the method calls getTextEditor().getText() to get the resulting edited
     * text.
     * @param txt the text string to display in the text widget
     * @param r Rectangle representing the desired bounding box of the text
     *  editing widget
     */
    public void editText(String txt, Rectangle r) {
        if ( m_editing ) { stopEditing(); }
        m_editing = true;
        m_editor.setBounds(r.x,r.y,r.width,r.height);
        m_editor.setText(txt);
        m_editor.setVisible(true);
        m_editor.setCaretPosition(txt.length());
        m_editor.requestFocus();
    } //
    
    /**
     * Stops text editing on the display, hiding the text editing widget. If
     * the text editor was associated with a specific GraphItem (ie one of the
     * editText() methods which include a GraphItem as an argument was called),
     * the item is updated with the edited text.
     */
    public void stopEditing() {
        m_editor.setVisible(false);
        if ( m_editItem != null ) {
            String txt = m_editor.getText();
            m_editItem.setAttribute(m_editAttribute, txt);
            m_editItem = null;
            m_editAttribute = null;
            m_editor.setBackground(null);
            m_editor.setForeground(null);
        }
        m_editing = false;
    } //
    
} // end of class Display
