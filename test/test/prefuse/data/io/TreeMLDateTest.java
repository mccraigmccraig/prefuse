package test.prefuse.data.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.data.io.TreeMLWriter;
import junit.framework.TestCase;
import test.prefuse.data.TableTestData;

public class TreeMLDateTest extends TestCase implements TableTestData {

    public void testReadWriteDates() throws Exception {
        Tree<?, ?, ?> tree = Tree.createTree();

        // add basic columns
        tree.addColumn( "dateColumn", Date.class );

        Calendar cal = Calendar.getInstance();
        // seconds (and milliseconds) are not parsed
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date now = cal.getTime();
        Node<?, ?> n = tree.addRoot();
        n.setDate("dateColumn", now);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new TreeMLWriter().writeGraph(tree, baos);
        Tree<?,?,?> t = (Tree<?,?,?>) new TreeMLReader().readGraph(new ByteArrayInputStream(baos.toByteArray()));
        Date recoveredDate = t.getRoot().getDate("dateColumn");
        assertEquals(now, recoveredDate);

    }

}
