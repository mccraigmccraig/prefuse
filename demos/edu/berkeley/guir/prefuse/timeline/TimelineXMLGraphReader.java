/*
 * Created on Jul 21, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.io.GraphReader;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class TimelineXMLGraphReader extends XMLGraphReader implements GraphReader {

	//private final Class NOTNOTCH_NODE_CLASS = NotNotchNode.class;
	//private final Class NOTCH_NODE_CLASS = NotchNode.class;

	/**
	 * @see edu.berkeley.guir.prefuse.graph.io.GraphReader#loadGraph(java.io.InputStream)
	 */
	public Graph loadGraph(InputStream is) throws IOException {
		try {		
		    TimelineXMLGraphHandler handler    = new TimelineXMLGraphHandler();
			SAXParserFactory factory   = SAXParserFactory.newInstance();
			SAXParser        saxParser = factory.newSAXParser();
			saxParser.parse(is, handler);
			return handler.getGraph();
		} catch ( SAXException se ) {
			se.printStackTrace();
		} catch ( ParserConfigurationException pce ) {
			pce.printStackTrace();
		} 
		return null;
	} //
	
	public class TimelineXMLGraphHandler extends XMLGraphHandler implements TimelineConstants {
        protected Node parseNode(Attributes atts) {
			String alName;
			String id = null;
			boolean isNotch = false;
			for ( int i = 0; i < atts.getLength(); i++ ) {
				if ( atts.getQName(i).equals(ID) ) {
					id = atts.getValue(i);
				} else if ( atts.getQName(i).equals(NODE_TYPE) ) {
				    isNotch = atts.getValue(i).equals(NOTCH_TYPE);
				}
			}
			if ( id == null ) {
				System.err.println("Node missing id");
				return null;
			}

			Node n = null;
			try {
			    if (isNotch) {
			        n = (Node)NOTCH_NODE_CLASS.newInstance();
			    } else {
			        n = (Node)NOTNOTCH_NODE_CLASS.newInstance();
			    }
			} catch ( Exception e ) {
				throw new RuntimeException(e);
			}
			n.setAttribute(ID,id);
			m_nodeMap.put(id, n);

			for ( int i = 0; i < atts.getLength(); i++ ) {
				alName = atts.getQName(i);
				if ( !alName.equals(ID) ) {
					n.setAttribute(alName, atts.getValue(i));
				}
			}
            m_graph.addNode(n);
			return n;
        }
}
    public static void main(String[] args) {
    }
}
