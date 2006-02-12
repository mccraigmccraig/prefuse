package prefuse.data.io;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import prefuse.data.Table;
import prefuse.util.collections.IntIterator;

/**
 * TableWriter that writes out a delimited text table, using a designated
 * character string to demarcate data columns.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DelimitedTextTableWriter extends AbstractTableWriter {

    private String m_delim;
    
    /**
     * Create a new DelimitedTextTableWriter that writes tab-delimited
     * text files.
     */
    public DelimitedTextTableWriter() {
        this("\t");
    }
    
    /**
     * Create a new DelimitedTextTableWriter.
     * @param delimiter the delimiter string to use between columns
     */
    public DelimitedTextTableWriter(String delimiter) {
        m_delim = delimiter;
    }
    
    // ------------------------------------------------------------------------

    /**
     * @see prefuse.data.io.TableWriter#writeTable(prefuse.data.Table, java.io.OutputStream)
     */
    public void writeTable(Table table, OutputStream os) throws DataIOException {
        try {
//            // sanity check
//            for ( int i=0; i<table.getColumnCount(); ++i ) {
//                if ( !table.canGetString(table.getColumnName(i)) )
//                    throw new IllegalStateException("Column \"" +
//                        table.getColumnName(i) + "\" of type " +
//                        table.getColumnClass(i).getName() + " does not " +
//                        " support a String representation");
//            }
            
            // get print stream
            PrintStream out = new PrintStream(new BufferedOutputStream(os));
            

            // write out header row
            for ( int i=0; i<table.getColumnCount(); ++i ) {
                if ( i>0 ) out.print(m_delim);
                out.print(table.getColumnName(i));
            }
            out.println();
            
            // write out data
            for ( IntIterator rows = table.rows(); rows.hasNext(); ) {
                int row = rows.nextInt();
                for ( int i=0; i<table.getColumnCount(); ++i ) {
                    if ( i>0 ) out.print(m_delim);
                    Object o = table.get(row, table.getColumnName(i));
                    out.print(o==null ? "null" : o.toString());
                }
                out.println();
            }
            
            // finish up
            out.flush();
        } catch ( Exception e ) {
            throw new DataIOException(e);
        }
    }

} // end of class DelimitedTextTableReader
