/**
 * 
 */
package prefuse.data.util;

import java.util.Arrays;
import java.util.Comparator;

import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.util.collections.CompositeComparator;

/**
 * Utility class representing sorting criteria
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class Sort {

    private static final String ASC  = " ASC";
    private static final String DESC = " DESC";
    private static final String asc  = ASC.toLowerCase();
    private static final String desc = DESC.toLowerCase();
    
    private String[]  m_fields;
    private boolean[] m_ascend;
    
    public Sort() {
    	this(new String[0], new boolean[0]);
    }
    
    public Sort(String[] fields) {
    	this(fields, new boolean[fields.length]);
    	Arrays.fill(m_ascend, true);
    }
    
    public Sort(String[] fields, boolean[] ascend) {
        m_fields = fields;
        m_ascend = ascend;
    }
    
    public void add(String field, boolean ascend) {
        String[] f = new String[m_fields.length+1];
        System.arraycopy(m_fields, 0, f, 0, m_fields.length);
        f[m_fields.length] = field;
        m_fields = f;
        
        boolean[] b = new boolean[m_fields.length+1];
        System.arraycopy(m_ascend, 0, b, 0, m_ascend.length);
        b[m_ascend.length] = ascend;
        m_ascend = b;
    }
    
    public int size() {
        return m_fields.length;
    }
    
    public String getField(int i) {
        return m_fields[i];
    }
    
    public boolean isAscending(int i) {
        return m_ascend[i];
    }
    
    public Comparator getComparator(TupleSet ts) {
        // get the schema, so we can lookup column value types
        // for Tables, we can get this directly
        // otherwise, use the schema of the first tuple in the set
        Schema s = (ts instanceof Table ? ((Table)ts).getSchema()
                                        : ((Tuple)ts.tuples()).getSchema());
        
        // create the comparator
        CompositeComparator cc = new CompositeComparator(m_fields.length);
        for ( int i=0; i<m_fields.length; ++i ) {
            cc.add(new TupleComparator(m_fields[i],
                       s.getColumnType(m_fields[i]), m_ascend[i]));
        }
        return cc;
    }
    
    // ------------------------------------------------------------------------
    
    private static void subparse(String s, Object[] res) {
        s = s.trim();
        
        // extract ascending modifier first
        res[1] = Boolean.TRUE;
        if ( s.endsWith(DESC) || s.endsWith(desc) ) {
            res[1] = Boolean.FALSE;
            s = s.substring(0, s.length()-DESC.length()).trim();
        } else if ( s.endsWith(ASC) || s.endsWith(asc) ) {
            s = s.substring(0, s.length()-ASC.length()).trim();
        }
        
        if ( s.startsWith("[") ) {
            if ( s.lastIndexOf("[") == 0 && 
                 s.endsWith("]") && s.indexOf("]") == s.length() ) {
                res[0] = s.substring(1, s.length()-1);
            } else {
                throw new RuntimeException();
            }
        } else {
            if ( s.indexOf(" ") < 0 && s.indexOf("\t") < 0 ) {
                res[0] = s;
            } else {
                throw new RuntimeException();
            }
        }
    }
    
    public static Sort parse(String s) {
        Sort sort = new Sort();
        Object[] res = new Object[2];
        int idx = 0, len = s.length();
        int comma = s.indexOf(',');
        int quote = s.indexOf('[');
        while ( idx < len ) {
            if ( comma < 0 ) {
                subparse(s.substring(idx), res);
                sort.add((String)res[0], ((Boolean)res[1]).booleanValue());
                break;
            } else if ( quote < 0 || comma < quote ) {
                subparse(s.substring(idx, comma), res);
                sort.add((String)res[0], ((Boolean)res[1]).booleanValue());
                idx = comma + 1;
                comma = s.indexOf(idx, ',');
            } else {
                int q2 = s.indexOf(quote, ']');
                if ( q2 < 0 ) {
                    throw new RuntimeException();
                } else {
                    comma = s.indexOf(q2, ',');
                    subparse(s.substring(idx, comma), res);
                    sort.add((String)res[0], ((Boolean)res[1]).booleanValue());
                    idx = comma + 1;
                    comma = s.indexOf(idx, ',');
                }
            }
        }
        return sort;
        
//        String[] tok = s.trim().split("\\s*,\\s*");
//        boolean[] ascend = new boolean[tok.length];
//        for ( int i=0; i<tok.length; ++i ) {
//            // set default ascend value
//            ascend[i] = true;
//            if ( tok[i].endsWith(ASC) || tok[i].endsWith(asc) ) {
//                tok[i] = tok[i].substring(0, tok[i].length()-ASC.length());
//            } else if ( tok[i].endsWith(DESC) || tok[i].endsWith(desc) ) {
//                ascend[i] = false;
//                tok[i] = tok[i].substring(0, tok[i].length()-DESC.length());
//            }
//        }
//        return new Sort(tok, ascend);
    }
    
    public String toString() {
    	StringBuffer sbuf = new StringBuffer();
    	for ( int i=0; i<m_fields.length; ++i ) {
    		if ( i > 0 ) sbuf.append(", ");
    		sbuf.append('[').append(m_fields[i]).append(']');
    		sbuf.append((m_ascend[i]) ? ASC : DESC);
    	}
    	return sbuf.toString();
    }
    
} // end of class Sort
