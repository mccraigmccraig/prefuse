package edu.berkeley.guir.prefuse;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.TextField;

/**
 * Container class designed to support prefuse visualizations. Provides
 * support for direct manipulation text editing and arbitrary tool tips.
 * 
 * Jun 18, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class PrefuseContainer extends Container {

	private Display       m_display;
	private TextComponent m_editor;
	private boolean       m_editing;
	
	private Component     m_toolTipper;
	private ToolTipTimer  m_toolTipTimer;
	private boolean		  m_toolTipsEnabled;
	private long		  m_toolTipDelay = 1000L;
	
	private GraphItem     m_editItem;
	private String        m_editAttribute;
	
	/**
	 * Create a new PrefuseContainer.
	 */
	public PrefuseContainer() {
		m_editor     = new TextField();
		m_editing    = false;
		
		m_toolTipper = new DefaultToolTipper();
		m_toolTipTimer = new ToolTipTimer();
		m_toolTipsEnabled = false;
		
		new Thread(m_toolTipTimer).start();
		
		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		m_editor.setVisible(false);	
		m_toolTipper.setVisible(false);
		
		this.setLayout(null);
		this.add(m_toolTipper);
		this.add(m_editor);		
	} //
	
	/**
	 * Create a new PrefuseContainer encapsulating the given Display.
	 * @param display the Display to wrap in the container.
	 */
	public PrefuseContainer(Display display) {
		this();
		m_display    = display;
		this.add(m_display);
	} //
	
	public void setDisplay(Display display) {
		m_display = display;
		this.add(m_display);
	} //
	
	/**
	 * Returns the TextComponent used for on-screen text editing.
	 * @return the TextComponent used for text editing
	 */
	public TextComponent getTextEditor() {
		return m_editor;
	} //
	
	/**
	 * Sets the TextComponent used for on-screen text editing.
	 * @param tc the TextComponent to use for text editing
	 */
	public void setTextEditor(TextComponent tc) {
		this.remove(m_editor);
		m_editor = tc;
		this.add(m_editor, 1);
	} //
	
	/**
	 * @see java.awt.Component#setSize(java.awt.Dimension)
	 */
	public void setSize(Dimension d) {
		m_display.setSize(d);
	} //
	
	/**
	 * @see java.awt.Component#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		m_display.setSize(width, height);
	} //
	
	/**
	 * @see java.awt.Component#getSize()
	 */
	public Dimension getSize() {
		return m_display.getSize();
	} //
	
	/**
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return m_display.getMinimumSize();
	} //
	
	/**
	 * @see java.awt.Component#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		return m_display.getMaximumSize();
	} //
	
	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return m_display.getPreferredSize();
	} //
	
	
	// ========================================================================
	// == TEXT EDITING METHODS ================================================
	
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
		if ( m_editor instanceof TextArea ) {
			r.y -= 2; r.width += 22; r.height += 2;
		} else {
			r.x -= 1; r.y -= 1; r.width += 2; r.height += 2;
		}
		
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
		m_editor.setForeground(nitem.getColor());
		m_editor.setBackground(nitem.getFillColor());
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
	
	
	// ========================================================================
	// == TOOLTIP METHODS =====================================================
	
	/**
	 * Show a tooltip at the designated location. The tooltip will appear
	 * after the delay time has passed if no interruptions occur. Use the
	 * setToolTipDelay() method to control delay times.
	 * @param x the x-coordinate at which to show the tooltip component
	 * @param y the y-coordinate at which to show the tooltip component
	 */
	public void showToolTip(int x, int y) {
		m_toolTipTimer.show(x,y);
	} //
	
	/**
	 * Hide the tooltip component.
	 */
	public void hideToolTip() {
		m_toolTipTimer.hide();
	} //
	
	/**
	 * Set the tooltip component. This determines which AWT component is used
	 * to present the tooltip display. This API allows arbitrary components to
	 * be displayed, allowing a great rangle of flexibility for tooltips.
	 * @param c the component to display for tooltips
	 */
	public void setToolTipComponent(Component c) {
		this.remove(m_toolTipper);
		m_toolTipper = c;
		this.add(m_toolTipper, 0);
	} //
	
	/**
	 * Returns the tooltip component. This is the component that is displayed
	 * when showToolTip() is called.
	 * @return the tooltip component
	 */
	public Component getToolTipComponent() {
		return m_toolTipper;
	} //
	
	/**
	 * Returns the delay between the time showToolTip() is called and when the
	 * tooltip actually appears.
	 * @return the tooltip delay
	 */
	public long getToolTipDelay() {
		return m_toolTipDelay;
	} //
	
	/**
	 * Sets the delay between the time showToolTip() is called and when the
	 * tooltip actually appears.
	 * @param delay the new tooltip delay
	 */
	public void setToolTipDelay(long delay) {
		m_toolTipDelay = delay;
	} //
	
	/**
	 * Indicates if tooltip display is enabled or not.
	 * @return true if tooltips are enabled, false otherwise
	 */
	public boolean isToolTipsEnabled() {
		return m_toolTipsEnabled;
	} //
	
	/**
	 * Sets if tooltips are enabled or not.
	 * @param s the enabled state. Should be true to enabled tooltips,
	 *  false to disable tooltips
	 */
	public void setToolTipsEnabled(boolean s) {
		m_toolTipsEnabled = s;
	} //
	
	/**
	 * Returns the text string currently displayed in a tooltip. This only
	 * applies if the default tooltip component is used. This method will
	 * throw an exception if a custom tooltip component has been set using
	 * the setToolTipComponent() method.
	 * @return the tooltip text
	 * @throws IllegalStateException if a custom tooltip component has been set
	 */
	public String getToolTipText() {
		if ( m_toolTipper instanceof DefaultToolTipper ) {
			return ((DefaultToolTipper)m_toolTipper).getText();
		} else {
			throw new IllegalStateException();
		}		
	} //
	
	/**
	 * Sets the text string currently displayed in a tooltip. This only
	 * applies if the default tooltip component is used. This method will
	 * throw an exception if a custom tooltip component has been set using
	 * the setToolTipComponent() method.
	 * @param text the new tooltip text
	 * @throws IllegalStateException if a custom tooltip component has been set
	 */	
	public void setToolTipText(String text) {
		if ( m_toolTipper instanceof DefaultToolTipper ) {
			((DefaultToolTipper)m_toolTipper).setText(text);
		} else {
			throw new IllegalStateException();
		}
	} //
	
	/**
	 * Performs timing for controlling the display of tooltips.
	 */
	public class ToolTipTimer implements Runnable {
		boolean visible = false;
		boolean show = false, squit = true, hquit = true, hide = false;
		int x, y;
		public synchronized void show(int x, int y) {
			this.squit = true;
			this.show = true;
			this.hide = false;
			this.x = x;	this.y = y;
			this.notifyAll();
		} //
		public synchronized void hide() {
			this.squit = true;
			this.hide = true;
			this.show = false;
			this.notifyAll();
		} //
		public void run() {
			synchronized (this) {
				while ( true ) {
					if ( !visible && show ) {
						if ( m_toolTipDelay >= 10 ) {
							try { wait(m_toolTipDelay); } catch ( Exception e) {}
						}
						if (!squit) { paint(); visible = true; show = false; }
					} else if ( visible && show ) {
						paint(); show = false;
					} else if ( visible && hide ) {
						try { wait(250L); } catch ( Exception e ) {}
						if (hide) { unpaint(); visible = false; hide = false; }
					} else if ( hide ) {
						hide = false;
					}
					if ( hide == false && show == false ) {
						try { wait(); } catch ( Exception e ) {}
						squit = false;
					}
				}
			}
		} //
		public void paint() {
			Dimension d = m_toolTipper.getSize();
			m_toolTipper.setBounds(x,y,d.width,d.height);
			m_toolTipper.setVisible(true);
			m_toolTipper.repaint();
		} //
		public void unpaint() {
			m_toolTipper.setVisible(false);
			m_toolTipper.repaint();
		} //
	} // end of inner class ToolTipTimer
	
	/**
	 * Default class for displaying tooltips. Presents one line of text
	 * in a light yellow box with a black border.
	 */
	public class DefaultToolTipper extends Label {
		private String text = "";
		public DefaultToolTipper() {
			this.setBackground(new Color(255,255,225));
			this.setForeground(Color.BLACK);
		} //
		public Dimension getSize() {
			Graphics g = this.getGraphics();
			FontMetrics fm = g.getFontMetrics();
			int w = fm.stringWidth(getText())+5;
			int h = fm.getHeight();
			return new Dimension(w,h);
		} //
		public void paint(Graphics g) {
			Rectangle r = this.getBounds();
			g.setColor(getBackground());
			g.fillRect(0,0,r.width-1,r.height-1);
			g.setColor(getForeground());
			g.drawRect(0,0,r.width-1,r.height-1);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(text,2,fm.getAscent());
		} //
		public String getText() {
			return text;
		} //
		public void setText(String text) {
			this.text = text;
		} //
	} // end of inner class DefaultToolTipper
	
} // end of class PrefuseContainer
