package prefuse.data.column;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import prefuse.data.Table;
import prefuse.util.ArrayLib;
import prefuse.util.TypeLib;
import prefuse.util.collections.DefaultLiteralComparator;
import prefuse.util.collections.IntIterator;

/**
 * Linrary routines for performing operations of data columns.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class Columns {

    private Columns() {
        // prevent instantiation
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Get an array containing all column values for a given table and field.
     * @param t the data table
     * @param col the column / data field name
     * @return an array containing the column values
     */
    public static Object[] toArray(Table t, String col) {
        // instantiate iterator before getting row count
        // that way we'll get a concurrent modification exception,
        // instead of possibly having an undetected change between
        // getting the row count and getting the iterator.
        int cidx = t.getColumnNumber(col);
        IntIterator rows = t.rows();
        Object[] array = new Object[t.getRowCount()];
        for ( int i=0; rows.hasNext(); ++i ) {
            array[i] = t.getValueAt(rows.nextInt(), cidx);
        }
        return array;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Get a sorted array containing all column values for a given table and
     * field.
     * @param t the data table
     * @param col the column / data field name
     * @return an array containing the column values sorted
     */
    public static Object[] ordinalArray(Table t, String col) {
        return ordinalArray(t, col, DefaultLiteralComparator.getInstance());
    }

    /**
     * Get a sorted array containing all column values for a given table and
     * field.
     * @param t the data table
     * @param col the column / data field name
     * @param cmp a comparator for sorting the column contents
     * @return an array containing the column values sorted
     */
    public static Object[] ordinalArray(Table t, String col, Comparator cmp) {
        // get set of all unique values
        int cidx = t.getColumnNumber(col);
        IntIterator rows = t.rows();
        HashSet set = new HashSet(Math.min(10, t.getRowCount()/2));
        while ( rows.hasNext() ) {
            set.add(t.getValueAt(rows.nextInt(), cidx));
        }
        
        // sort the unique values
        Object[] o = set.toArray();
        Arrays.sort(o, cmp);
        return o;
    }
    
    /**
     * Get map mapping from column values (as Object instances) to their
     * ordinal index in a sorted array.
     * @param t the data table
     * @param col the column / data field name
     * @return a map mapping column values to their position in a sorted
     * order of values
     */
    public static Map ordinalMap(Table t, String col) {
        return ordinalMap(t, col, DefaultLiteralComparator.getInstance());
    }
    
    /**
     * Get map mapping from column values (as Object instances) to their
     * ordinal index in a sorted array.
     * @param t the data table
     * @param col the column / data field name
     * @param cmp a comparator for sorting the column contents
     * @return a map mapping column values to their position in a sorted
     * order of values
     */
    public static Map ordinalMap(Table t, String col, Comparator cmp) {
        Object[] o = ordinalArray(t, col, cmp);
        
        // map the values to the non-negative numbers
        HashMap map = new HashMap();
        for ( int i=0; i<o.length; ++i )
            map.put(o[i], new Integer(i));
        return map;
    }
    
    // ------------------------------------------------------------------------    
    
    /**
     * Get the number of distinct values in a data column.
     * @param t the data table
     * @param col the column / data field name
     * @return the number of distinct values
     */
    public static int uniqueCount(Table t, String col) {
        int cidx = t.getColumnNumber(col);
        IntIterator rows = t.rows();
        HashSet set = new HashSet(Math.min(10, t.getRowCount()/2));
        while ( rows.hasNext() ) {
            set.add(t.getValueAt(rows.nextInt(), cidx));
        }
        return set.size();
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Get the row with the minimum column data value.
     * @param t the data table
     * @param col the column / data field name
     * @return the row with the minimum column data value
     */
    public static int min(Table t, String col) {
        return min(t, col, DefaultLiteralComparator.getInstance());
    }

    /**
     * Get the row with the minimum column data value.
     * @param t the data table
     * @param col the column / data field name
     * @param cmp a comparator for sorting the column contents
     * @return the row with the minimum column data value
     */
    public static int min(Table t, String col, Comparator cmp) {
        IntIterator rows = t.rows();
        int i, idx = -1, cidx = t.getColumnNumber(col);
        Object min = null;
        if ( rows.hasNext() ) {
            idx = rows.nextInt();
            min = t.get(idx, col);
        }
        while ( rows.hasNext() ) {
            i = rows.nextInt();
            Object obj = t.getValueAt(i, cidx);
            if ( cmp.compare(obj,min) < 0 ) {
                idx = i;
                min = obj;
            }
        }
        return idx;
    }

    // ------------------------------------------------------------------------    
    
    /**
     * Get the row with the maximum column data value.
     * @param t the data table
     * @param col the column / data field name
     * @return the row with the maximum column data value
     */
    public static int max(Table t, String col) {
        return max(t, col, DefaultLiteralComparator.getInstance());
    }
    
    /**
     * Get the row with the maximum column data value.
     * @param t the data table
     * @param col the column / data field name
     * @param cmp a comparator for sorting the column contents
     * @return the row with the maximum column data value
     */
    public static int max(Table t, String col, Comparator cmp) {
        IntIterator rows = t.rows();
        int i, idx = -1, cidx = t.getColumnNumber(col);
        Object max = null;
        if ( rows.hasNext() ) {
            idx = rows.nextInt();
            max = t.get(idx, col);
        }
        while ( rows.hasNext() ) {
            i = rows.nextInt();
            Object obj = t.getValueAt(i, cidx);
            if ( cmp.compare(obj,max) > 0 ) {
                idx = i;
                max = obj;
            }
        }
        return idx;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Get the row with the median column data value.
     * @param t the data table
     * @param col the column / data field name
     * @return the row with the median column data value
     */
    public static int median(Table t, String col) {
        return median(t, col, DefaultLiteralComparator.getInstance());
    }
    
    /**
     * Get the row with the median column data value.
     * @param t the data table
     * @param col the column / data field name
     * @param cmp a comparator for sorting the column contents
     * @return the row with the median column data value
     */
    public static int median(Table t, String col, Comparator cmp) {
        int cidx = t.getColumnNumber(col);
        IntIterator rows = t.rows();
        int[]    index = new int[t.getRowCount()];
        Object[] array = new Object[t.getRowCount()];
        for ( int i=0; rows.hasNext(); ++i ) {
            index[i] = rows.nextInt();
            array[i] = t.getValueAt(index[i], cidx);
        }
        ArrayLib.sort(array, index, cmp);
        return index[index.length/2];
    }
    
    // ------------------------------------------------------------------------    
    
    /**
     * Get the mean value of a numeric data column. If the column is not of
     * a numeric data type, NaN will be returned.
     * @param t the data table
     * @param col the column / data field name
     * @return the mean value, or NaN if not a numeric data type
     */
    public static double mean(Table t, String col) {
        int cidx = t.getColumnNumber(col);
        Column c = t.getColumn(cidx);

        if ( TypeLib.isNumericType(c.getColumnClass()) ) {
            IntIterator rows = t.rows();
            int r, count = 0;
            double sum = 0;
            
            while ( rows.hasNext() ) {
                r = t.getColumnRow(rows.nextInt(), cidx);
                sum += c.getDouble(r);
                ++count;
            }
            return sum/count;
        } else {
            return Double.NaN;
        }
    }
    
    // ------------------------------------------------------------------------    
    
    /**
     * Get the standard deviation value of a numeric data column. If the column
     * is not of a numeric data type, NaN will be returned.
     * @param t the data table
     * @param col the column / data field name
     * @return the standard deviation value, or NaN if not a numeric data type
     */
    public static double deviation(Table t, String col) {
        return deviation(t, col, mean(t, col));
    }

    /**
     * Get the standard deviation value of a numeric data column. If the column
     * is not of a numeric data type, NaN will be returned.
     * @param t the data table
     * @param col the column / data field name
     * @param mean the mean of the column, used to speed up accurate
     * deviation calculation
     * @return the standard deviation value, or NaN if not a numeric data type
     */
    public static double deviation(Table t, String col, double mean) {
        int cidx = t.getColumnNumber(col);
        Column c = t.getColumn(cidx);
        
        if ( TypeLib.isNumericType(c.getColumnClass()) ) {
            IntIterator rows = t.rows();
            int r, count = 0;
            double sumsq = 0;
            double x;
            
            while ( rows.hasNext() ) {
                r = t.getColumnRow(rows.nextInt(), cidx);
                x = c.getDouble(r)-mean;
                sumsq += x*x;
                ++count;
            }
            return Math.sqrt(sumsq/count);
        } else {
            return Double.NaN;
        }
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Get the sum of a numeric data column. If the column
     * is not of a numeric data type, NaN will be returned.
     * @param t the data table
     * @param col the column / data field name
     * @return the column sum, or NaN if not a numeric data type
     */
    public static double sum(Table t, String col) {
        int cidx = t.getColumnNumber(col);
        Column c = t.getColumn(cidx);

        if ( TypeLib.isNumericType(c.getColumnClass()) ) {
            IntIterator rows = t.rows();
            double sum = 0;
            int r;
            while ( rows.hasNext() ) {
                r = t.getColumnRow(rows.nextInt(), cidx);
                sum += c.getDouble(r);
            }
            return sum;
        } else {
            return Double.NaN;
        }
    }
    
} // end of class Columns
