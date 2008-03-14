package test.prefuse.data.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.data.io.TreeMLWriter;
import junit.framework.TestCase;

public class TreeMLReaderTest extends TestCase {

    public void testWriteReadUnicode() throws Exception {
        Tree<?, ?, ?> tree = Tree.createTree();

        // add basic columns
        tree.addColumn( "unicodeColumn", String.class );

        Node<?, ?> n = tree.addRoot();
        StringBuffer buf = new StringBuffer();
        buf.appendCodePoint(1048696);
        n.setString("unicodeColumn", buf.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new TreeMLWriter().writeGraph(tree, baos);
        System.out.println(new String(baos.toByteArray(), "UTF-8"));
        Tree<?,?,?> t = (Tree<?,?,?>) new TreeMLReader().readGraph(new ByteArrayInputStream(baos.toByteArray()));
        String recoveredString = t.getRoot().getString("unicodeColumn");
        assertEquals(recoveredString, buf.toString());
    }

}
