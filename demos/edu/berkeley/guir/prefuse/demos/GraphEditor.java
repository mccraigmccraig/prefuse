package edu.berkeley.guir.prefuse.demos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
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
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.GraphItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.GarbageCollector;
import edu.berkeley.guir.prefuse.action.GraphEdgeFilter;
import edu.berkeley.guir.prefuse.action.GraphNodeFilter;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.activity.ActionPipeline;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.ActivityMap;
import edu.berkeley.guir.prefuse.event.ActivityAdapter;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.SimpleGraph;
import edu.berkeley.guir.prefuse.graph.io.GraphReader;
import edu.berkeley.guir.prefuse.graph.io.GraphWriter;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphWriter;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * GraphEditor Application, an editor for hand creating directed graphs
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> - prefuse(AT)jheer.org
 */
public class GraphEditor extends JFrame {

	public static final String OPEN    = "Open";
	public static final String SAVE    = "Save";
	public static final String SAVE_AS = "Save As...";
	public static final String EXIT    = "Exit";
    public static final String RANDOM  = "Random Layout";
    public static final String FORCE   = "Force-Directed Layout";

	private JMenuItem saveItem;

	public static final String TITLE = "Graph Editor";
	public static final String DEFAULT_LABEL = "???";

	public static final String nameField = "label";
	public static final String idField   = "id";
		
	private ItemRegistry registry;
	private Display display;
	private Graph g;
    private ActivityMap actmap = new ActivityMap();
		
    public static void main(String argv[]) {
        new GraphEditor();
    } //
    
	public GraphEditor() {
        super(TITLE);
        
		setLookAndFeel();
		try {
			g = new SimpleGraph(Collections.EMPTY_LIST);

			registry = new ItemRegistry(g);
            display  = new Display();
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
			display.setRegistry(registry);
			display.setSize(600,600);
			display.setBackground(Color.WHITE);
            display.setFont(new Font("SansSerif",Font.PLAIN,10));
            display.getTextEditor().addKeyListener(controller);
			display.addControlListener(controller);
			
			// initialize filter
            ActionPipeline filter = new ActionPipeline(registry);
            filter.add(new GraphNodeFilter());
            filter.add(new GarbageCollector(ItemRegistry.DEFAULT_NODE_CLASS));
            filter.add(new GraphEdgeFilter());
            filter.add(new GarbageCollector(ItemRegistry.DEFAULT_EDGE_CLASS));
            actmap.put("filter", filter);
            
            ActionPipeline update = new ActionPipeline(registry);
            update.add(new AbstractAction() {
				public void run(ItemRegistry registry, double frac) {
					Iterator nodeIter = registry.getNodeItems();
					while ( nodeIter.hasNext() ) {
						NodeItem item = (NodeItem)nodeIter.next();
						item.setAttribute("X",String.valueOf(item.getX()));
						item.setAttribute("Y",String.valueOf(item.getY()));
					}
				} //
			});
            update.add(new RepaintAction());
            actmap.put("update", update);
            
            ActionPipeline randomLayout = new ActionPipeline(registry);
            randomLayout.add(new RandomLayout(30));
            randomLayout.add(update);
            actmap.put("randomLayout", randomLayout);
            
            ActionPipeline forceLayout = new ActionPipeline(registry,5000,20);
            forceLayout.add(new ForceDirectedLayout(true));
            forceLayout.add(update);
            forceLayout.addActivityListener(new ActivityAdapter() {
                public void activityFinished(Activity a) {
                    ((ForceDirectedLayout)((ActionPipeline)a).get(0))
                        .reset(registry);
                } //
            });
            actmap.put("forceLayout", forceLayout);
			
			// initialize menus
			JMenuBar  menubar    = new JMenuBar();
			JMenu     fileMenu   = new JMenu("File");
            JMenu     layoutMenu = new JMenu("Layout");
			JMenuItem openItem   = new JMenuItem(OPEN);
				      saveItem   = new JMenuItem(SAVE);
			JMenuItem saveAsItem = new JMenuItem(SAVE_AS);
			JMenuItem exitItem   = new JMenuItem(EXIT);
            JMenuItem randomItem = new JMenuItem(RANDOM);
            JMenuItem forceItem  = new JMenuItem(FORCE);
			
			openItem.setActionCommand(OPEN);
			saveItem.setActionCommand(SAVE);
			saveAsItem.setActionCommand(SAVE_AS);
			exitItem.setActionCommand(EXIT);
            randomItem.setActionCommand(RANDOM);
            forceItem.setActionCommand(FORCE);
			
			openItem.addActionListener(controller);
			saveItem.addActionListener(controller);
			saveAsItem.addActionListener(controller);
			exitItem.addActionListener(controller);
            randomItem.addActionListener(controller);
            forceItem.addActionListener(controller);
			
			fileMenu.add(openItem);
			fileMenu.add(saveItem);
			fileMenu.add(saveAsItem);
			fileMenu.add(exitItem);
            
            layoutMenu.add(randomItem);
            layoutMenu.add(forceItem);
			
			menubar.add(fileMenu);
            menubar.add(layoutMenu);
			
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			setJMenuBar(menubar);
			getContentPane().add(display, BorderLayout.CENTER);
			pack();
			setVisible(true);
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
	
	private void setLocations(Graph g) {
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
	class Controller extends ControlAdapter 
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
            if ( item instanceof NodeItem ) {
                e.getComponent().setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
		} //
		
		public void itemExited(GraphItem item, MouseEvent e) {
            if ( item instanceof NodeItem ) {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
		} //
		
		public void itemPressed(GraphItem item, MouseEvent e) {
			if ( item instanceof NodeItem ) {
			    xDown = e.getX();
			    yDown = e.getY();
			    item.setColor(Color.RED);
			    item.setFillColor(Color.WHITE);
			    actmap.scheduleNow("update");
            }
		} //
		
		public void itemReleased(GraphItem item, MouseEvent e) {
            if ( !(item instanceof NodeItem) )
                return;
            
			boolean update = false;
			if ( item instanceof NodeItem ) {
				if ( activeItem == null && !drag ) {
					activeItem = item;
				} else if ( activeItem == null ) {
					item.setColor(Color.BLACK);
					item.setFillColor(Color.WHITE);
					update = true;
				} else if ( activeItem == item && !drag ) {
					editing = true;
					display.editText(item, nameField);
					display.getTextEditor().selectAll();
					setEdited(true);
					update = true;
				} else if ( activeItem != item ) {
					// add edge
					addEdge(activeItem, item);
					
					item.setColor(Color.BLACK);
					item.setFillColor(Color.WHITE);
					activeItem.setColor(Color.BLACK);
					activeItem.setFillColor(Color.WHITE);
					activeItem = null;
					update = true;
                    actmap.scheduleNow("filter");
				}
			}
			drag = false;
            if ( update )
                actmap.scheduleNow("update");
		} //
		
		public void itemDragged(GraphItem item, MouseEvent e) {
            if ( !(item instanceof NodeItem) )
                return;
            
			drag = true;
			int dx = e.getX() - xDown;
			int dy = e.getY() - yDown;
			Point2D p = item.getLocation();
			item.setLocation(p.getX()+dx,p.getY()+dy);
            actmap.scheduleNow("update");
			xDown = e.getX();
			yDown = e.getY();
			setEdited(true);
		} //
		
		public void itemKeyTyped(GraphItem item, KeyEvent e) {
			if ( e.getKeyChar() == '\b' ) {				
				if (item == activeItem) activeItem = null;
				removeNode(item);
                actmap.scheduleNow("filter");
                actmap.scheduleNow("update");
				setEdited(true);
			}
		} //

		/**
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			boolean update = false;
			if ( editing ) {
				stopEditing();
				update = true;
			}
			if ( activeItem != null ) {
				activeItem.setColor(Color.BLACK);
				activeItem.setFillColor(Color.WHITE);
				activeItem = null;
				update = true;
			}
			boolean rightClick = (e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0;
			if ( rightClick ) {
				addNode(e.getX(), e.getY());
				setEdited(true);
                actmap.scheduleNow("filter");
				update = true;
			}
			if ( update ) {
                actmap.scheduleNow("update");
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
				display.editText(item, nameField, r);
				setEdited(true);
                actmap.scheduleNow("update");
			}
		} //
		
		public void keyReleased(KeyEvent e) {
			Object src = e.getSource();
			if ( src == display.getTextEditor() && 
				e.getKeyCode() == KeyEvent.VK_ENTER ) {
				stopEditing();
                actmap.scheduleNow("update");
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
			display.stopEditing();
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
				if ( chooser.showOpenDialog(display) == JFileChooser.APPROVE_OPTION ) {
					 File f = chooser.getSelectedFile();
					 GraphReader gr = new XMLGraphReader();
					 try {					 
						g = gr.loadGraph(f);
						registry.setGraph(g);
						setLocations(g);
                        actmap.scheduleNow("filter");
                        actmap.scheduleNow("update");
						saveFile = f;
						setEdited(false);
					 } catch ( Exception ex ) {
						JOptionPane.showMessageDialog(
							display,
							"Sorry, an error occurred while loading the graph.",
							"Error Loading Graph",
							JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					 }
				}
			} else if ( SAVE.equals(cmd) ) {
				if ( saveFile == null ) {
					JFileChooser chooser = new JFileChooser();
					if ( chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION ) {
						File f = chooser.getSelectedFile();
						save(f);
					}
				} else {
					save(saveFile);
				}
			} else if ( SAVE_AS.equals(cmd) ) {
				JFileChooser chooser = new JFileChooser();
				if ( chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION ) {
					 File f = chooser.getSelectedFile();
					 save(f);
				}
			} else if ( EXIT.equals(cmd) ) {
				System.exit(0);
            } else if ( RANDOM.equals(cmd) ) {
                actmap.scheduleNow("randomLayout");
            } else if ( FORCE.equals(cmd) ) {
                actmap.scheduleNow("forceLayout");
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
					display,
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
			if ( !titleString.equals(getTitle()) )
				setTitle(titleString);
		} //
		
	} // end of inner class MouseOverControl

} // end of classs GraphEditor
