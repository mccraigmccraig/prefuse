package edu.berkeley.guir.prefuse.util.display;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.berkeley.guir.prefuse.Display;

/**
 * SaveImageAction
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ExportDisplayAction extends AbstractAction {

    private Display display;
    private JFileChooser chooser;
    
    public ExportDisplayAction(Display display) {
        this.display = display;
        chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Export Prefuse Display...");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new SimpleFileFilter("jpg", "JPG Image (*.jpg)"));
        chooser.setFileFilter(new SimpleFileFilter("png", "PNG Image (*.png)"));
    } //
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        // open image save dialog
        File f = null;
        int returnVal = chooser.showSaveDialog(display);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
           f = chooser.getSelectedFile();
        } else {
            return;
        }
        String format = ((SimpleFileFilter)chooser.getFileFilter()).getExtension();
        String ext = getExtension(f);        
        if ( !format.equals(ext) ) {
            f = new File(f.getName()+"."+format);
        }
        
        double scale = 1.0;
        
        // save image
        boolean success = false;
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
            System.out.print("Saving image "+f.getName()+", "+format+" format...");
            success = display.saveImage(out, format, scale);
            System.out.println("\tDONE");
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
    
    public static String getExtension(File f) {
    	if (f != null) {
    	    String filename = f.getName();
    	    int i = filename.lastIndexOf('.');
    	    if(i>0 && i<filename.length()-1) {
    	        return filename.substring(i+1).toLowerCase();
    	    }
    	}
    	return null;
    } //
    
    public class SimpleFileFilter extends FileFilter {
        private String ext, desc;
        
        public SimpleFileFilter(String ext, String desc) {
            this.ext = ext;
            this.desc = desc;
        } //
        public boolean accept(File f) {
            if ( f == null )
                return false;
            if ( f.isDirectory() )
                return true;
            String extension = ExportDisplayAction.getExtension(f);
            return ( extension != null && extension.equals(ext) );
        } //
        public String getDescription() {
            return desc;
        } //
        public String getExtension() {
            return ext;
        } //
    } // end of class SimpleFileFilter
    
} // end of class SaveImageAction
