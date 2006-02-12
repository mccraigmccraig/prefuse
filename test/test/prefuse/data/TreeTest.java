package test.prefuse.data;

import java.net.URL;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import junit.framework.TestCase;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.demos.TreeMap;
import prefuse.util.ui.JPrefuseTable;

public class TreeTest extends TestCase {

    public static final String TREE_CHI = "/chi-ontology.xml.gz";
    
    public void testTreeReader() {
        // load graph and initialize the item registry
        URL url = TreeMap.class.getResource(TREE_CHI);
        Tree t = null;
        try {
            GZIPInputStream gzin = new GZIPInputStream(url.openStream());
            t = (Tree) new TreeMLReader().readGraph(gzin);
        } catch ( Exception e ) {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(true, t.isValidTree());
        
        Node[] nodelist = new Node[t.getNodeCount()];
        
        Iterator nodes = t.nodes();
        for ( int i=0; nodes.hasNext(); ++i ) {
            nodelist[i] = (Node)nodes.next();
        }
//        nodes = t.nodes(true);
//        for ( int i=t.getNodeCount(); --i >= 0; ) {
//            assertEquals(nodelist[i], nodes.next());
//        }
        assertEquals(false, nodes.hasNext());
    }
    
    public static void main(String[] argv) {
        URL url = TreeMap.class.getResource(TREE_CHI);
        Tree t = null;
        try {
            GZIPInputStream gzin = new GZIPInputStream(url.openStream());
            t = (Tree) new TreeMLReader().readGraph(gzin);
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(1);
        }
        
        JPrefuseTable table = new JPrefuseTable(t.getEdgeTable());
        JFrame frame = new JFrame("edges");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);
    }
    
}
