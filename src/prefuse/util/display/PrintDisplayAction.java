package prefuse.util.display;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import prefuse.Display;
import prefuse.util.io.PrintLib;

/**
 * Swing ActionListener that reveals a dialog box that allows users to export
 * the current Display view to a printer.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class PrintDisplayAction extends AbstractAction {

	private final Display display;

	/**
	 * Create a new ExportDisplayAction for the given Display.
	 * 
	 * @param display
	 *            the Display to capture
	 */
	public PrintDisplayAction(Display display) {
		this.display = display;
	}

	private void init() {
	}

	/**
	 * Shows the image export dialog and processes the results.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		PrintLib.printComponent(display);
	}

} // end of class SaveImageAction
