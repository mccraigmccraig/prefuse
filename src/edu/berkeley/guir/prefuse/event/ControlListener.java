package edu.berkeley.guir.prefuse.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.EventListener;

import edu.berkeley.guir.prefuse.GraphItem;

/**
 * Listener interface for processing user interface events on 
 * a prefuse Display.
 * 
 * @version 1.0
 * @author newbergr
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public interface ControlListener extends 
    EventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	
	//// == Actions performed on GraphItems ===================================

	/**
	 * Invoked when a mouse button is pressed on a GraphItem and then dragged.
	 */
	public void itemDragged(GraphItem item, MouseEvent e);
	
	/**
	 * Invoked when the mouse cursor has been moved onto a GraphItem but
	 *  no buttons have been pushed.
	 */
	public void itemMoved(GraphItem item, MouseEvent e);
	
	/**
	 * Invoked when the mouse wheel is rotated while the mouse is over a
	 *  GraphItem.
	 */
	public void itemWheelMoved(GraphItem item, MouseWheelEvent e);
	
	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on
	 *  a GraphItem.
	 */
	public void itemClicked(GraphItem item, MouseEvent e);
	
	/**
	 * Invoked when a mouse button has been pressed on a GraphItem.
	 */
	public void itemPressed(GraphItem item, MouseEvent e);
	
	/**
	 * Invoked when a mouse button has been released on a GraphItem.
	 */
	public void itemReleased(GraphItem item, MouseEvent e);
	
	/**
	 * Invoked when the mouse enters a GraphItem.
	 */
	public void itemEntered(GraphItem item, MouseEvent e);
	
	/**
	 * Invoked when the mouse exits a GraphItem.
	 */
	public void itemExited(GraphItem item, MouseEvent e);
	
	/**
	 * Invoked when a key has been pressed, while the mouse is over
	 * 	a GraphItem.
	 */
	public void itemKeyPressed(GraphItem item, KeyEvent e);
	
	/**
	 * Invoked when a key has been released, while the mouse is over
	 *  a GraphItem.
	 */
	public void itemKeyReleased(GraphItem item, KeyEvent e);
	
	/**
	 * Invoked when a key has been typed, while the mouse is over
	 *  a GraphItem.
	 */
	public void itemKeyTyped(GraphItem item, KeyEvent e);
	
	
	//// == Actions performed on the Display ==================================
	
	/**
	 * Invoked when the mouse enters the Display.
	 */
	public void mouseEntered(MouseEvent e);
	
	/**
	 * Invoked when the mouse exits the Display.
	 */
	public void mouseExited(MouseEvent e);
	
	/**
	 * Invoked when a mouse button has been pressed on the Display but NOT
	 *  on a GraphItem.
	 */
	public void mousePressed(MouseEvent e);
	
	/**
	 * Invoked when a mouse button has been released on the Display but NOT
	 *  on a GraphItem.
	 */
	public void mouseReleased(MouseEvent e);
	
	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on
	 *  the Display, but NOT on a GraphItem.
	 */
	public void mouseClicked(MouseEvent e);
	
	/**
	 * Invoked when a mouse button is pressed on the Display (but NOT a 
	 *  GraphItem) and then dragged.
	 */
	public void mouseDragged(MouseEvent e);
	
	/**
	 * Invoked when the mouse cursor has been moved on the Display (but NOT a
	 * GraphItem) and no buttons have been pushed.
	 */
	public void mouseMoved(MouseEvent e);
	
	/**
	 * Invoked when the mouse wheel is rotated while the mouse is over the
	 *  Display (but NOT a GraphItem).
	 */
	public void mouseWheelMoved(MouseWheelEvent e);
	
	/**
	 * Invoked when a key has been pressed, while the mouse is NOT 
	 *  over a GraphItem.
	 */
	public void keyPressed(KeyEvent e);
	
	/**
	 * Invoked when a key has been released, while the mouse is NOT
	 *  over a GraphItem.
	 */
	public void keyReleased(KeyEvent e);
	
	/**
	 * Invoked when a key has been typed, while the mouse is NOT
	 *  over a GraphItem.
	 */
	public void keyTyped(KeyEvent e);

} // end of inteface ControlListener
