package edu.berkeley.guir.prefuse.util.display;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import edu.berkeley.guir.prefuse.Display;

/**
 * SaveImageAction
 * 
 * Created on Dec 25, 2004
 * @version 1.0
 * @author jheer
 */
public class SaveImageAction extends AbstractAction {

    private Display display;
    
    public SaveImageAction(Display display) {
        this.display = display;
    } //
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        // open image save dialog
        File f = new File("prefuse.display.png");
        String format = "png";
        double scale = 1.0;
        
        // save image
        boolean success = false;
        try {
            //OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
            //System.out.print("Saving image "+f.getName()+"...");
            //success = display.saveImage(out, format, scale);
            //System.out.println("\tDONE");
            success = display.saveImage2();
        } catch ( Exception e ) {
            success = false;
        }
        // show result dialog on failure
        if ( !success ) {
            JOptionPane.showMessageDialog(display,
                    "Error Saving Image!",
                    "Image Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    } //
    
} // end of class SaveImageAction
