package edu.berkeley.guir.prefuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.SimpleGraph;
import edu.berkeley.guir.prefuse.graph.io.GraphReader;
import edu.berkeley.guir.prefuse.graph.io.GraphWriter;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphWriter;
import edu.berkeley.guir.prefuse.pipeline.AbstractPipelineComponent;
import edu.berkeley.guir.prefuse.pipeline.Filter;
import edu.berkeley.guir.prefuse.pipeline.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.pipeline.GraphNodeFilter;
import edu.berkeley.guir.prefuse.pipeline.PipelineComponent;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;

/**
 * Prefuse Demo Application, for creating free-form graphs
 * 
 * Apr 25, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class FreeformGraphDemo {

	public static final String OPEN    = "Open";
	public static final String SAVE    = "Save";
	public static final String SAVE_AS = "Save As...";
	public static final String EXIT    = "Exit";

	private static MenuItem saveItem;
	private static Frame frame;

	public static final String TITLE = "Graph Editor";
	public static final String DEFAULT_LABEL = "???";

	public static final String nameField = "label";
	public static final String idField   = "id";
		
	public static ItemRegistry registry;
	public static Display display;
	public static Graph g;
	public static Pipeline pipeline;
	public static PrefuseContainer container;
		
	public static void main(String[] args) {
		setLookAndFeel();
		try {
			g = new SimpleGraph(Collections.EMPTY_LIST);

			display = new Display();				
			pipeline = new Pipeline(g, display);
			registry = pipeline.getItemRegistry();
			Controller controller = new Controller();
			
			// initialize renderers
			Renderer nodeRenderer = new TextImageItemRenderer();
			Renderer edgeRenderer = new DefaultEdgeRenderer() {
				protected int getLineWidth(GraphItem item) {
					try {
						String wstr = item.getAttribute("weight");
						return Integer.parseInt(wstr);
					} catch ( Exception e ) {
						return m_width;
					}
				} //
			};
			registry.setRendererFactory(new DefaultRendererFactory(
				nodeRenderer, edgeRenderer, null));
			
			// initialize display
			display.setPipeline(pipeline);
			display.setSize(600,600);
			display.setBackground(Color.WHITE);
			display.addControlListener(controller);
			
			// initialize pipeline
			Filter nodeFilter = new GraphNodeFilter();
			Filter edgeFilter = new GraphEdgeFilter();
			PipelineComponent locationSaver = new AbstractPipelineComponent() {
				public void process() {
					Iterator nodeIter = m_registry.getNodeItems();
					while ( nodeIter.hasNext() ) {
						NodeItem item = (NodeItem)nodeIter.next();
						item.setAttribute("X",String.valueOf(item.getX()));
						item.setAttribute("Y",String.valueOf(item.getY()));
					}
				} //
			};
			
			pipeline.addComponent(nodeFilter);
			pipeline.addComponent(edgeFilter);
			pipeline.addComponent(locationSaver);
			
			// initialize user interface components
			container = new PrefuseContainer(display);
			container.setFont(new Font("SansSerif",Font.PLAIN,10));
			container.setBackground(Color.WHITE);
			container.getTextEditor().addKeyListener(controller);
			
			MenuBar  menubar    = new MenuBar();
			Menu     fileMenu   = new Menu("File");
			MenuItem openItem   = new MenuItem(OPEN);
				     saveItem   = new MenuItem(SAVE);
			MenuItem saveAsItem = new MenuItem(SAVE_AS);
			MenuItem exitItem   = new MenuItem(EXIT);
			
			openItem.setActionCommand(OPEN);
			saveItem.setActionCommand(SAVE);
			saveAsItem.setActionCommand(SAVE_AS);
			exitItem.setActionCommand(EXIT);
			
			openItem.addActionListener(controller);
			saveItem.addActionListener(controller);
			saveAsItem.addActionListener(controller);
			exitItem.addActionListener(controller);
			
			fileMenu.add(openItem);
			fileMenu.add(saveItem);
			fileMenu.add(saveAsItem);
			fileMenu.add(exitItem);
			
			menubar.add(fileMenu);
			
			frame = new Frame(TITLE);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			frame.setMenuBar(menubar);
			frame.add(container, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
	public static void setLookAndFeel() {
		try {
			String laf = UIManager.getSystemLookAndFeelClassName();				
			UIManager.setLookAndFeel(laf);	
		} catch ( Exception e ) {}
	} //
	
	private static void setLocations(Graph g) {
		Iterator nodeIter = g.getNodes();
		while ( nodeIter.hasNext() ) {
			Node n = (Node)nodeIter.next();
			NodeItem item = registry.getNodeItem(n,true);
			item.setColor(Color.BLACK);
			item.setFillColor(Color.WHITE);
			try {
				int x = (int)Double.parseDouble(n.getAttribute("X"));
				int y = (int)Double.parseDouble(n.getAttribute("Y"));
				item.setLocation(x,y);
			} catch ( Exception e ) {
				System.err.println("!!");
			}
		}
	}
	
	/**
	 * Input controller for interacting with the application.
	 * 
	 * @version 1.0
	 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
	 */
	static class Controller extends ControlAdapter 
		implements MouseListener, KeyListener, ActionListener
	{
		private int xDown, yDown, xCur, yCur;
		
		private boolean directed = true;
		private boolean drag     = false;
		private boolean editing  = false;
		
		private GraphItem activeItem;
		private GraphItem edgeItem;
		
		private boolean edited   = false;
		private File    saveFile = null;
		
		public void itemEntered(GraphItem item, MouseEvent e) {
			e.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
		} //
		
		public void itemExited(GraphItem item, MouseEvent e) {
			e.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} //
		
		public void itemPressed(GraphItem item, MouseEvent e) {
			xDown = e.getX();
			yDown = e.getY();
			item.setColor(Color.RED);
			item.setFillColor(Color.WHITE);
			pipeline.runPipeline();
		} //
		
		public void itemReleased(GraphItem item, MouseEvent e) {
			boolean runPipeline = false;
			if ( item instanceof NodeItem ) {
				if ( activeItem == null && !drag ) {
					activeItem = item;
				} else if ( activeItem == null ) {
					item.setColor(Color.BLACK);
					item.setFillColor(Color.WHITE);
					runPipeline = true;
				} else if ( activeItem == item && !drag ) {
					editing = true;
					container.editText(item, nameField);
					container.getTextEditor().selectAll();
					setEdited(true);
					runPipeline = true;
				} else if ( activeItem != item ) {
					// add edge
					addEdge(activeItem, item);
					
					item.setColor(Color.BLACK);
					item.setFillColor(Color.WHITE);
					activeItem.setColor(Color.BLACK);
					activeItem.setFillColor(Color.WHITE);
					activeItem = null;
					runPipeline = true;
				}
			}
			drag = false;
			pipeline.runPipeline();
		} //
		
		public void itemDragged(GraphItem item, MouseEvent e) {
			drag = true;
			int dx = e.getX() - xDown;
			int dy = e.getY() - yDown;
			Point2D p = item.getLocation();
			item.setLocation(p.getX()+dx,p.getY()+dy);
			pipeline.runPipeline();
			xDown = e.getX();
			yDown = e.getY();
			setEdited(true);
		} //
		
		public void itemKeyTyped(GraphItem item, KeyEvent e) {
			if ( item == activeItem && e.getKeyChar() == '\b' ) {				
				activeItem = null;
				removeNode(item);
				pipeline.runPipeline();
				setEdited(true);
			}
		} //

		/**
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			boolean runPipeline = false;
			if ( editing ) {
				stopEditing();
				runPipeline = true;
			}
			if ( activeItem != null ) {
				activeItem.setColor(Color.BLACK);
				activeItem.setFillColor(Color.WHITE);
				activeItem = null;
				runPipeline = true;
			}
			boolean rightClick = (e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0;
			if ( rightClick ) {
				addNode(e.getX(), e.getY());
				setEdited(true);
				runPipeline = true;
			}
			if ( runPipeline ) {
				pipeline.runPipeline();
			}
		} //
		
		public void mouseMoved(MouseEvent e) {
			if ( !editing )
				display.requestFocus();
			xCur = e.getX();
			yCur = e.getY();
		} //

		public void keyPressed(KeyEvent e) {
			Object src = e.getSource();
			char c = e.getKeyChar();
			if ( Character.isLetterOrDigit(c) && 
				src == display && activeItem == null ) {
				GraphItem item = addNode(xCur, yCur);
				item.setAttribute(nameField,String.valueOf(c));
				editing = true;
				Rectangle r = item.getBounds();
				r.width = 52; r.height += 2;
				r.x -= 1+r.width/2; r.y -= 1; 
				container.editText(item, nameField, r);
				setEdited(true);
				pipeline.runPipeline();
			}
		} //
		
		public void keyReleased(KeyEvent e) {
			Object src = e.getSource();
			if ( src == container.getTextEditor() && 
				e.getKeyCode() == KeyEvent.VK_ENTER ) {
				stopEditing();
				pipeline.runPipeline();
			}
		} //

		private NodeItem addNode(int x, int y) {
			Node n = new Node();
			n.setAttribute(nameField, DEFAULT_LABEL);
			((SimpleGraph)g).addNode(n);
			NodeItem item = registry.getNodeItem(n,true);
			item.setColor(Color.BLACK);
			item.setFillColor(Color.WHITE);
			item.setLocation(x, y);
			return item;
		} //
		
		private void addEdge(GraphItem item1, GraphItem item2) {
			Node n1 = (Node)item1.getEntity();
			Node n2 = (Node)item2.getEntity();
			if ( n1.getNeighborIndex(n2) < 0 ) {
				Edge e = new Edge(n1, n2, directed);
				n1.addEdge(e);
				if ( !directed )
					n2.addEdge(e);
			}
		} //
		
		private void removeNode(GraphItem item) {
			Node n = (Node)item.getEntity();
			((SimpleGraph)g).removeNode(n);
		} //

		private void stopEditing() {
			container.stopEditing();
			if ( activeItem != null ) {
				activeItem.setColor(Color.BLACK);
				activeItem.setFillColor(Color.WHITE);
				activeItem = null;
			}
			editing = false;
		} //

		// == MENU CALLBACKS =====================================================

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if ( OPEN.equals(cmd) ) {
				JFileChooser chooser = new JFileChooser();
				if ( chooser.showOpenDialog(container) == JFileChooser.APPROVE_OPTION ) {
					 File f = chooser.getSelectedFile();
					 GraphReader gr = new XMLGraphReader();
					 try {					 
						g = gr.loadGraph(f);
						pipeline.setGraph(g);
						setLocations(g);
						pipeline.runPipeline();
						saveFile = f;
						setEdited(false);
					 } catch ( Exception ex ) {
						JOptionPane.showMessageDialog(
							container,
							"Sorry, an error occurred while loading the graph.",
							"Error Loading Graph",
							JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					 }
				}
			} else if ( SAVE.equals(cmd) ) {
				if ( saveFile == null ) {
					JFileChooser chooser = new JFileChooser();
					if ( chooser.showSaveDialog(container) == JFileChooser.APPROVE_OPTION ) {
						File f = chooser.getSelectedFile();
						save(f);
					}
				} else {
					save(saveFile);
				}
			} else if ( SAVE_AS.equals(cmd) ) {
				JFileChooser chooser = new JFileChooser();
				if ( chooser.showSaveDialog(container) == JFileChooser.APPROVE_OPTION ) {
					 File f = chooser.getSelectedFile();
					 save(f);
				}
			} else if ( EXIT.equals(cmd) ) {
				System.exit(0);
			} else {
				throw new IllegalStateException();
			}
		} //
		
		private void save(File f) {
			GraphWriter gw = new XMLGraphWriter();
			 try {					 
				gw.writeGraph(g, f);
				saveFile = f;
				setEdited(false);
			 } catch ( Exception ex ) {
				JOptionPane.showMessageDialog(
					container,
					"Sorry, an error occurred while saving the graph.",
					"Error Saving Graph",
					JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			 }
		} //
		
		private void setEdited(boolean s) {
			if ( edited == s ) return;
			edited = s;
			saveItem.setEnabled(s);
			String titleString;
			if ( saveFile == null ) {
				titleString = TITLE;
			} else {
				titleString = TITLE + " - " + saveFile.getName() +
								( s ? "*" : "" );
			}
			if ( !titleString.equals(frame.getTitle()) )
				frame.setTitle(titleString);
		} //
		
	} // end of inner class Controller

} // end of classs PrefuseDemo
