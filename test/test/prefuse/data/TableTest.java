package test.prefuse.data;

import java.awt.geom.GeneralPath;

import junit.framework.TestCase;
import prefuse.data.Table;
import prefuse.data.column.Column;

public class TableTest extends TestCase implements TableTestData {

    int rows[] = { -1, NROWS+1, 2*NROWS };
    int cols[] = { -1, NCOLS+1, 2*NCOLS };
    
    Table t;
    
    public static Table getTestCaseTable() {
        Table t = new Table(NROWS, NCOLS);
        for ( int c=0; c<NCOLS; ++c ) {
            t.addColumn(HEADERS[c], TYPES[c]);
            for ( int r=0; r<NROWS; ++r ) {
                t.set(r, HEADERS[c], TABLE[c][r]);
            }
        }
        return t;
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        t = getTestCaseTable();
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        t = null;
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.getColumnCount()'
     */
    public void testGetColumnCount() {
        assertEquals(NCOLS, t.getColumnCount());
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.getColumnClass(int)'
     */
    public void testGetColumnClassInt() {
        for ( int c=0; c<NCOLS; ++c ) {
            assertEquals(TYPES[c], t.getColumnType(c));
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.getColumnClass(String)'
     */
    public void testGetColumnClassString() {
        for ( int c=0; c<NCOLS; ++c ) {
            assertEquals(TYPES[c], t.getColumnType(HEADERS[c]));
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.getRowCount()'
     */
    public void testGetRowCount() {
        assertEquals(NROWS, t.getRowCount());
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.addRow()'
     */
    public void testAddRow() {
        int r = t.addRow();
        assertEquals(NROWS+1, t.getRowCount());
        assertEquals(NROWS, r);
        t.set(r,HEADERS[0],TABLE[0][0]);
        assertEquals(TABLE[0][0], t.get(r,HEADERS[0]));
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.addRows(int)'
     */
    public void testAddRows() {
        int nrows = 3;
        t.addRows(nrows);
        assertEquals(NROWS+nrows, t.getRowCount());
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.removeRow(int)'
     */
    public void testRemoveRow() {
        for ( int i=0; i<NROWS; ++i ) {
            t.removeRow(i);
            assertEquals(NROWS-1, t.getRowCount());
            int r = t.addRow();
            assertEquals(i, r);
            assertEquals(NROWS, t.getRowCount());
        }
        
        try {
            tearDown(); setUp();
        } catch ( Exception e ) {}
        
        for ( int i=0; i<NROWS; ++i ) {
            t.removeRow(i);
            assertEquals(NROWS-i-1, t.getRowCount());
            try {
                t.get(i, HEADERS[0]);
                fail("Allowed access to invalid row");
            } catch ( Exception e ) {}
            try {
                t.get(i, HEADERS[2]);
                fail("Allowed access to invalid row");
            } catch ( Exception e ) {}
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.getColumnName(int)'
     */
    public void testGetColumnName() {
        for ( int c=0; c<NCOLS; ++c ) {
            assertEquals(HEADERS[c], t.getColumnName(c));
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.getColumnIndex(String)'
     */
    public void testGetColumnIndex() {
        for ( int c=0; c<NCOLS; ++c ) {
            assertEquals(c, t.getColumnNumber(HEADERS[c]));
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.getColumn()'
     */
    public void testGetColumn() {
        for ( int c=0; c<NCOLS; ++c ) {
            Column col1 = t.getColumn(c);
            Column col2 = t.getColumn(HEADERS[c]);
            assertEquals(col1, col2);
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.addColumn(String, Class)'
     */
    public void testAddColumn() {
        String[] names = { "polygon", "boolean" };
        Class[]  types = { GeneralPath.class, boolean.class };
        
        for ( int i=0; i < names.length; ++i ) {
            t.addColumn(names[i], types[i]);
            Column col = t.getColumn(names[i]);
            assertTrue(col.getRowCount() >= t.getRowCount());
            assertTrue(col.canSet(types[i]));
            assertFalse(col.canSet(Math.class));
            
            assertEquals(NCOLS+i+1, t.getColumnCount());
            assertEquals(types[i], t.getColumnType(names[i]));
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.removeColumn(String)'
     */
    public void testRemoveColumnString() {
        t.removeColumn(HEADERS[0]);
        assertEquals(NCOLS-1, t.getColumnCount());
        assertEquals(-1, t.getColumnNumber(HEADERS[0]));
        try {
            t.get(0,HEADERS[0]);
            fail("Allowed access to removed column");
        } catch ( Exception success ) {}
        try {
            t.set(0,HEADERS[0],TABLE[0][0]);
            fail("Allowed access to removed column");
        } catch ( Exception success ) {}
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.canGet(String, Class)'
     */
    public void testCanGet() {
        for ( int c=0; c<NCOLS; ++c ) {
            assertTrue(t.canGet(HEADERS[c], TYPES[c]));
            assertFalse(t.canGet(HEADERS[c], Math.class));
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.canSet(String, Class)'
     */
    public void testCanSet() {
        for ( int c=0; c<NCOLS; ++c ) {
            assertTrue(t.canSet(HEADERS[c], TYPES[c]));
            assertFalse(t.canSet(HEADERS[c], Math.class));
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.get(String, int)'
     */
    public void testGet() {
        for ( int r=0; r<NROWS; ++r ) {
            for ( int c=0; c<NCOLS; ++c ) {
                assertEquals(TABLE[c][r], t.get(r, HEADERS[c]));
            }
        }
        
        for ( int i=0; i<rows.length; ++i ) {
            try {
                t.get(rows[i],HEADERS[0]);
                fail("Allowed illegal access");
            } catch ( Exception success ) {
            }
        }
        
        for ( int i=0; i<cols.length; ++i ) {
            try {
                t.getValueAt(0,cols[i]);
                fail("Allowed illegal access");
            } catch ( Exception success ) {
            }
        }
    }

    /*
     * Test method for 'edu.berkeley.guir.prefuse.data.Table.set(Object, String, int)'
     */
    public void testSet() {
        for ( int c=0; c<NCOLS; ++c ) {
            t.set(0, HEADERS[c], TABLE[c][1]);
            assertEquals(TABLE[c][1], t.get(0, HEADERS[c]));
        }
        
        for ( int i=0; i<rows.length; ++i ) {
            try {
                t.set(rows[i],HEADERS[0],TABLE[0][i]);
                fail("Allowed illegal access");
            } catch ( Exception success ) {
            }
        }
        
        for ( int i=0; i<cols.length; ++i ) {
            try {
                t.setValueAt(0,cols[i],TABLE[i][0]);
                fail("Allowed illegal access");
            } catch ( Exception success ) {
            }
        }
    }

}
