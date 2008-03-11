package prefuse.visual;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import prefuse.ShapeBuilder;
import prefuse.Visualization;
import prefuse.data.CascadedTable;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.EventConstants;
import prefuse.data.expression.Predicate;
import prefuse.visual.tuple.TableVisualItem;

/**
 * A visual abstraction of a Table data structure. Serves as a backing table for VisualItem tuples.
 * VisualTable derives from CascadedTable, so can inherit another table's values. Commonly, a
 * VisualTable is used to take a raw data table and "strap" visual properties on top of it.
 * VisualTables should not be created directly, they are created automatically by adding data to a
 * Visualization, for example by using the {@link Visualization#addTable(String, Table)} method.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public abstract class VisualTable<T extends Tuple<?>, C extends VisualItem<?>> extends CascadedTable<T, C> implements VisualTupleSet<C> {

	public static <T extends Tuple<?>> VisualTable<T, TableVisualItem<?>> createVisualTable(final Table<T> parent,
			final Visualization vis, final String group) {
		return createVisualTable(parent, vis, group, null, VisualItem.SCHEMA);
	}

	public static <T extends Tuple<?>> VisualTable<T, TableVisualItem<?>> createVisualTable(final Table<T> parent,
			final Visualization vis, final String group, final Predicate rowFilter) {
		return createVisualTable(parent, vis, group, rowFilter, VisualItem.SCHEMA);
	}

	public static <T extends Tuple<?>> VisualTable<T, TableVisualItem<?>> createVisualTable(final Table<T> parent,
			final Visualization vis, final String group, final Predicate rowFilter, final Schema schema) {
		return new VisualTable<T, TableVisualItem<?>>(parent, vis, group, rowFilter, schema) {

			@Override
			public TableVisualItem<?> createTupleInstance() {
				return new TableVisualItem<TableVisualItem<?>>();
			}

		};
	}

	public static VisualTable<TableVisualItem<?>, TableVisualItem<?>> createVisualTable(final Visualization vis,
			final String group) {
		return createVisualTable(vis, group, VisualItem.SCHEMA);
	}

	public static VisualTable<TableVisualItem<?>, TableVisualItem<?>> createVisualTable(final Visualization vis,
			final String group, final Schema schema) {
		return new VisualTable<TableVisualItem<?>, TableVisualItem<?>>(vis, group, schema) {

			@Override
			public TableVisualItem<?> createTupleInstance() {
				return new TableVisualItem<TableVisualItem<?>>();
			}

		};
	}

	private Visualization m_vis;
	private String m_group;

	// ------------------------------------------------------------------------
	// Constructors

	/**
	 * Create a new VisualTable.
	 *
	 * @param parent
	 *            the parent table whose values this table should inherit
	 * @param vis
	 *            the Visualization associated with this table
	 * @param group
	 *            the data group of this table
	 */
	public VisualTable(final Table<T> parent, final Visualization vis, final String group) {
		this(parent, vis, group, null, VisualItem.SCHEMA);
	}

	/**
	 * Create a new VisualTable.
	 *
	 * @param parent
	 *            the parent table whose values this table should inherit
	 * @param vis
	 *            the Visualization associated with this table
	 * @param group
	 *            the data group of this table
	 * @param rowFilter
	 *            a predicate determining which rows of the parent table should be inherited by this
	 *            table and which should be filtered out
	 */
	public VisualTable(final Table<T> parent, final Visualization vis, final String group, final Predicate rowFilter) {
		this(parent, vis, group, rowFilter, VisualItem.SCHEMA);
	}

	/**
	 * Create a new VisualTable.
	 *
	 * @param parent
	 *            the parent table whose values this table should inherit
	 * @param vis
	 *            the Visualization associated with this table
	 * @param group
	 *            the data group of this table
	 * @param rowFilter
	 *            a predicate determining which rows of the parent table should be inherited by this
	 *            table and which should be filtered out
	 * @param schema
	 *            the data schema to use for the table's local columns
	 */
	public VisualTable(final Table<T> parent, final Visualization vis, final String group, final Predicate rowFilter,
			final Schema schema) {
		super(parent, rowFilter, null);
		init(vis, group, schema);
	}

	// -- non-cascaded visual table -------------------------------------------

	/**
	 * Create a new VisualTable without a parent table.
	 *
	 * @param vis
	 *            the Visualization associated with this table
	 * @param group
	 *            the data group of this table
	 */
	public VisualTable(final Visualization vis, final String group) {
		init(vis, group, VisualItem.SCHEMA);
	}

	/**
	 * Create a new VisualTable without a parent table.
	 *
	 * @param vis
	 *            the Visualization associated with this table
	 * @param group
	 *            the data group of this table
	 * @param schema
	 *            the data schema to use for the table's local columns
	 */
	public VisualTable(final Visualization vis, final String group, final Schema schema) {
		init(vis, group, schema);
	}

	/**
	 * Initialize this VisualTable
	 *
	 * @param vis
	 *            the Visualization associated with this table
	 * @param group
	 *            the data group of this table
	 * @param schema
	 *            the data schema to use for the table's local columns
	 */
	protected void init(final Visualization vis, final String group, final Schema schema) {
		setVisualization(vis);
		setGroup(group);
		addColumns(schema);
		if (canGetBoolean(VisualItem.VISIBLE)) {
			index(VisualItem.VISIBLE);
		}
		if (canGetBoolean(VisualItem.STARTVISIBLE)) {
			index(VisualItem.STARTVISIBLE);
		}
		if (canGetBoolean(VisualItem.VALIDATED)) {
			index(VisualItem.VALIDATED);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Relay table events. Ensures that updated visual items are invalidated and that damage reports
	 * are issued for deleted items.
	 */
	@Override
	protected void fireTableEvent(final int row0, final int row1, final int col, final int type) {
		// table attributes changed, so we invalidate the bounds
		if (type == EventConstants.UPDATE) {
			if (col != VisualItem.IDX_VALIDATED) {
				for (int r = row0; r <= row1; ++r) {
					setValidated(r, false);
				}
			} else {
				// change in validated status
				for (int r = row0; r <= row1; ++r) {
					if (!isValidated(r)) {
						// retrieve the old bounds to report damage
						m_vis.damageReport(getItem(r), getBounds(r));
					}
				}
			}
		} else if (type == EventConstants.DELETE && col == EventConstants.ALL_COLUMNS) {
			for (int r = row0; r <= row1; ++r) {
				if (isVisible(r) && isValidated(r)) {
					final VisualItem<?> item = getTuple(r);
					m_vis.damageReport(item, getBounds(r));
				}
			}
		}
		// now propagate the change event
		super.fireTableEvent(row0, row1, col, type);
	}

	// ------------------------------------------------------------------------
	// VisualItemTable Methods

	/**
	 * @see prefuse.visual.VisualTupleSet#getVisualization()
	 */
	public Visualization getVisualization() {
		return m_vis;
	}

	/**
	 * Set the visualization associated with this VisualTable
	 *
	 * @param vis
	 *            the visualization to set
	 */
	public void setVisualization(final Visualization vis) {
		m_vis = vis;
	}

	/**
	 * Get the visualization data group name for this table
	 *
	 * @return the data group name
	 */
	public String getGroup() {
		return m_group;
	}

	/**
	 * Set the visualization data group name for this table
	 *
	 * @param group
	 *            the data group name for this table
	 */
	public void setGroup(final String group) {
		m_group = group;
	}

	/**
	 * Get the VisualItem for the given table row.
	 *
	 * @param row
	 *            a table row index
	 * @return the VisualItem for the given table row
	 */
	public C getItem(final int row) {
		return getTuple(row);
	}

	/**
	 * Add a new row to the table and return the VisualItem for that row. Only allowed if there is
	 * no parent table, otherwise an exception will result.
	 *
	 * @return the VisualItem for the newly added table row.
	 */
	public C addItem() {
		return getItem(addRow());
	}

	// ------------------------------------------------------------------------
	// VisualItem Data Access

	/**
	 * Indicates if the given row is currently validated. If not, validateBounds() must be run to
	 * update the bounds to a current value.
	 *
	 * @param row
	 *            the table row
	 * @return true if validated, false otherwise
	 */
	public boolean isValidated(final int row) {
		return getBoolean(row, VisualItem.VALIDATED);
	}

	/**
	 * Set the given row's validated flag. This is for internal use by prefuse and, in general,
	 * should not be called by application code.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            the value of the validated flag to set.
	 */
	public void setValidated(final int row, final boolean value) {
		setBoolean(row, VisualItem.VALIDATED, value);
	}

	/**
	 * Indicates if the given row is currently set to be visible. Items with the visible flag set
	 * false will not be drawn by a display. Invisible items are also by necessity not interactive,
	 * regardless of the value of the interactive flag.
	 *
	 * @param row
	 *            the table row
	 * @return true if visible, false if invisible
	 */
	public boolean isVisible(final int row) {
		return getBoolean(row, VisualItem.VISIBLE);
	}

	/**
	 * Set the given row's visibility.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true to make the item visible, false otherwise.
	 */
	public void setVisible(final int row, final boolean value) {
		setBoolean(row, VisualItem.VISIBLE, value);
	}

	/**
	 * Indicates if the start visible flag is set to true. This is the visibility value consulted
	 * for the staring value of the visibility field at the beginning of an animated transition.
	 *
	 * @param row
	 *            the table row
	 * @return true if this item starts out visible, false otherwise.
	 */
	public boolean isStartVisible(final int row) {
		return getBoolean(row, VisualItem.STARTVISIBLE);
	}

	/**
	 * Set the start visible flag.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true to set the start visible flag, false otherwise
	 */
	public void setStartVisible(final int row, final boolean value) {
		setBoolean(row, VisualItem.STARTVISIBLE, value);
	}

	/**
	 * Indictes if the end visible flag is set to true. This is the visibility value consulted for
	 * the ending value of the visibility field at the end of an animated transition.
	 *
	 * @param row
	 *            the table row
	 * @return true if this items ends visible, false otherwise.
	 */
	public boolean isEndVisible(final int row) {
		return getBoolean(row, VisualItem.ENDVISIBLE);
	}

	/**
	 * Set the end visible flag.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true to set the end visible flag, false otherwise
	 */
	public void setEndVisible(final int row, final boolean value) {
		setBoolean(row, VisualItem.ENDVISIBLE, value);
	}

	/**
	 * Indicates if this item is interactive, meaning it can potentially respond to mouse and
	 * keyboard input events.
	 *
	 * @param row
	 *            the table row
	 * @return true if the item is interactive, false otherwise
	 */
	public boolean isInteractive(final int row) {
		return getBoolean(row, VisualItem.INTERACTIVE);
	}

	/**
	 * Set the interactive status of the given row.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true for interactive, false for non-interactive
	 */
	public void setInteractive(final int row, final boolean value) {
		setBoolean(row, VisualItem.INTERACTIVE, value);
	}

	/**
	 * Indicates the given row is expanded. Only used for items that are part of a graph structure.
	 *
	 * @param row
	 *            the table row
	 * @return true if expanded, false otherwise
	 */
	public boolean isExpanded(final int row) {
		return getBoolean(row, VisualItem.EXPANDED);
	}

	/**
	 * Set the expanded flag.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true to set as expanded, false as collapsed.
	 */
	public void setExpanded(final int row, final boolean value) {
		setBoolean(row, VisualItem.EXPANDED, value);
	}

	/**
	 * Indicates if the given row is fixed, and so will not have its position changed by any layout
	 * or distortion actions.
	 *
	 * @param row
	 *            the table row
	 * @return true if the item has a fixed position, false otherwise
	 */
	public boolean isFixed(final int row) {
		return getBoolean(row, VisualItem.FIXED);
	}

	/**
	 * Sets if the given row is fixed in its position.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true to fix the item, false otherwise
	 */
	public void setFixed(final int row, final boolean value) {
		setBoolean(row, VisualItem.FIXED, value);
	}

	/**
	 * Indicates if the given row is highlighted.
	 *
	 * @param row
	 *            the table row
	 * @return true for highlighted, false for not highlighted
	 */
	public boolean isHighlighted(final int row) {
		return getBoolean(row, VisualItem.HIGHLIGHT);
	}

	/**
	 * Set the highlighted status of the given row. How highlighting values are interpreted by the
	 * system depends on the various processing actions set up for an application (e.g., how a
	 * {@link prefuse.action.assignment.ColorAction} might assign colors based on the flag).
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true to highlight the item, false for no highlighting.
	 */
	public void setHighlighted(final int row, final boolean value) {
		setBoolean(row, VisualItem.HIGHLIGHT, value);
	}

	/**
	 * Indicates if the given row currently has the mouse pointer over it.
	 *
	 * @param row
	 *            the table row
	 * @return true if the mouse pointer is over this item, false otherwise
	 */
	public boolean isHover(final int row) {
		return getBoolean(row, VisualItem.HOVER);
	}

	/**
	 * Set the hover flag. This is set automatically by the prefuse framework, so should not need to
	 * be set explicitly by application code.
	 *
	 * @param row
	 *            the table row to set
	 * @param value
	 *            true to set the hover flag, false otherwise
	 */
	public void setHover(final int row, final boolean value) {
		setBoolean(row, VisualItem.HOVER, value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the current x-coordinate of the given row.
	 *
	 * @param row
	 *            the table row
	 * @return the current x-coordinate
	 */
	public double getX(final int row) {
		return getDouble(row, VisualItem.X);
	}

	/**
	 * Set the current x-coordinate of the given row.
	 *
	 * @param row
	 *            the table row to set
	 * @param x
	 *            the new current x-coordinate
	 */
	public void setX(final int row, final double x) {
		setDouble(row, VisualItem.X, x);
	}

	/**
	 * Get the current y-coordinate of the given row.
	 *
	 * @param row
	 *            the table row
	 * @return the current y-coordinate
	 */
	public double getY(final int row) {
		return getDouble(row, VisualItem.Y);
	}

	/**
	 * Set the current y-coordinate of the given row.
	 *
	 * @param row
	 *            the table row to set
	 * @param y
	 *            the new current y-coordinate
	 */
	public void setY(final int row, final double y) {
		setDouble(row, VisualItem.Y, y);
	}

	/**
	 * Get the starting x-coordinate of the given row.
	 *
	 * @param row
	 *            the table row
	 * @return the starting x-coordinate
	 */
	public double getStartX(final int row) {
		return getDouble(row, VisualItem.STARTX);
	}

	/**
	 * Set the starting x-coordinate of the given row.
	 *
	 * @param row
	 *            the table row to set
	 * @param x
	 *            the new starting x-coordinate
	 */
	public void setStartX(final int row, final double x) {
		setDouble(row, VisualItem.STARTX, x);
	}

	/**
	 * Get the starting y-coordinate of the given row.
	 *
	 * @param row
	 *            the table row
	 * @return the starting y-coordinate
	 */
	public double getStartY(final int row) {
		return getDouble(row, VisualItem.STARTY);
	}

	/**
	 * Set the starting y-coordinate of the given row.
	 *
	 * @param row
	 *            the table row to set
	 * @param y
	 *            the new starting y-coordinate
	 */
	public void setStartY(final int row, final double y) {
		setDouble(row, VisualItem.STARTY, y);
	}

	/**
	 * Get the ending x-coordinate of the given row.
	 *
	 * @param row
	 *            the table row
	 * @return the ending x-coordinate
	 */
	public double getEndX(final int row) {
		return getDouble(row, VisualItem.ENDX);
	}

	/**
	 * Set the ending x-coordinate of the given row.
	 *
	 * @param row
	 *            the table row to set
	 * @param x
	 *            the new ending x-coordinate
	 */
	public void setEndX(final int row, final double x) {
		setDouble(row, VisualItem.ENDX, x);
	}

	/**
	 * Get the ending y-coordinate of the given row.
	 *
	 * @param row
	 *            the table row
	 * @return the ending y-coordinate
	 */
	public double getEndY(final int row) {
		return getDouble(row, VisualItem.ENDY);
	}

	/**
	 * Set the ending y-coordinate of the given row.
	 *
	 * @param row
	 *            the table row to set
	 * @param y
	 *            the new ending y-coordinate
	 */
	public void setEndY(final int row, final double y) {
		setDouble(row, VisualItem.ENDY, y);
	}

	/**
	 * Returns the bounds for the VisualItem at the given row index. The returned reference is for
	 * the actual bounds object used by the system -- do <b>NOT</b> directly edit the values in
	 * this returned object!! This will corrupt the state of the system.
	 *
	 * @param row
	 *            the table row
	 * @return the bounding box for the item at the given row
	 */
	public Rectangle2D getBounds(final int row) {
		return (Rectangle2D) get(row, VisualItem.BOUNDS);
	}

	/**
	 * Set the bounding box for an item. This method is used by Renderer modules when the bounds are
	 * validated, or set by processing Actions used in conjunction with Renderers that do not
	 * perform bounds management.
	 *
	 * @param row
	 *            the table row to set
	 * @param x
	 *            the minimum x-coordinate
	 * @param y
	 *            the minimum y-coordinate
	 * @param w
	 *            the width of this item
	 * @param h
	 *            the height of this item
	 * @see VisualItem#BOUNDS
	 */
	public void setBounds(final int row, final double x, final double y, final double w, final double h) {
		Rectangle2D rect = getBounds(row);

		// occassionally the bounds, when queried here, is null. To counteract this, we actually
		// create a new rectangle and set this as the bounds. We then use this rectangle to notify
		// the system.
		if (rect == null) {
			rect = new Rectangle();
			rect.setRect(x, y, w, h);
			set(row, VisualItem.BOUNDS, rect);
		} else {
			rect.setRect(x, y, w, h);
		}
		fireTableEvent(row, row, getColumnNumber(VisualItem.BOUNDS), EventConstants.UPDATE);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the current stroke color of the row. The stroke color is used to draw lines and the
	 * outlines of shapes. Color values as represented as an integer containing the red, green,
	 * blue, and alpha (transparency) color channels. A color with a zero alpha component is fully
	 * transparent and will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the current stroke color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getStrokeColor(final int row) {
		return getInt(row, VisualItem.STROKECOLOR);
	}

	/**
	 * Set the current stroke color of the row. The stroke color is used to draw lines and the
	 * outlines of shapes. Color values as represented as an integer containing the red, green,
	 * blue, and alpha (transparency) color channels. A color with a zero alpha component is fully
	 * transparent and will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the current stroke color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setStrokeColor(final int row, final int color) {
		setInt(row, VisualItem.STROKECOLOR, color);
	}

	/**
	 * Get the starting stroke color of the row. The stroke color is used to draw lines and the
	 * outlines of shapes. Color values as represented as an integer containing the red, green,
	 * blue, and alpha (transparency) color channels. A color with a zero alpha component is fully
	 * transparent and will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the starting stroke color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getStartStrokeColor(final int row) {
		return getInt(row, VisualItem.STARTSTROKECOLOR);
	}

	/**
	 * Set the starting stroke color of the row. The stroke color is used to draw lines and the
	 * outlines of shapes. Color values as represented as an integer containing the red, green,
	 * blue, and alpha (transparency) color channels. A color with a zero alpha component is fully
	 * transparent and will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the starting stroke color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setStartStrokeColor(final int row, final int color) {
		setInt(row, VisualItem.STARTSTROKECOLOR, color);
	}

	/**
	 * Get the ending stroke color of the row. The stroke color is used to draw lines and the
	 * outlines of shapes. Color values as represented as an integer containing the red, green,
	 * blue, and alpha (transparency) color channels. A color with a zero alpha component is fully
	 * transparent and will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the ending stroke color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getEndStrokeColor(final int row) {
		return getInt(row, VisualItem.ENDSTROKECOLOR);
	}

	/**
	 * Set the ending stroke color of the row. The stroke color is used to draw lines and the
	 * outlines of shapes. Color values as represented as an integer containing the red, green,
	 * blue, and alpha (transparency) color channels. A color with a zero alpha component is fully
	 * transparent and will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the ending stroke color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setEndStrokeColor(final int row, final int color) {
		setInt(row, VisualItem.ENDSTROKECOLOR, color);
	}

	/**
	 * Get the current fill color of the row. The fill color is used to fill the interior of shapes.
	 * Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with a zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the current fill color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getFillColor(final int row) {
		return getInt(row, VisualItem.FILLCOLOR);
	}

	/**
	 * Set the current fill color of the row. The stroke color is used to fill the interior of
	 * shapes. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with a zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the current fill color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setFillColor(final int row, final int color) {
		setInt(row, VisualItem.FILLCOLOR, color);
	}

	/**
	 * Get the starting fill color of the row. The fill color is used to fill the interior of
	 * shapes. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the starting fill color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getStartFillColor(final int row) {
		return getInt(row, VisualItem.STARTFILLCOLOR);
	}

	/**
	 * Set the starting fill color of the row. The stroke color is used to fill the interior of
	 * shapes. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with a zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the starting fill color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setStartFillColor(final int row, final int color) {
		setInt(row, VisualItem.STARTFILLCOLOR, color);
	}

	/**
	 * Get the ending fill color of the row. The fill color is used to fill the interior of shapes.
	 * Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the ending fill color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getEndFillColor(final int row) {
		return getInt(row, VisualItem.ENDFILLCOLOR);
	}

	/**
	 * Set the ending fill color of the row. The stroke color is used to fill the interior of
	 * shapes. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with a zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the ending fill color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setEndFillColor(final int row, final int color) {
		setInt(row, VisualItem.ENDFILLCOLOR, color);
	}

	/**
	 * Get the current text color of the row. The text color is used to draw text strings for the
	 * item. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the current text color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getTextColor(final int row) {
		return getInt(row, VisualItem.TEXTCOLOR);
	}

	/**
	 * Set the current text color of the row. The text color is used to draw text strings for the
	 * item. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with a zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the current text color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setTextColor(final int row, final int color) {
		setInt(row, VisualItem.TEXTCOLOR, color);
	}

	/**
	 * Get the starting text color of the row. The text color is used to draw text strings for the
	 * item. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the starting text color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getStartTextColor(final int row) {
		return getInt(row, VisualItem.STARTTEXTCOLOR);
	}

	/**
	 * Set the starting text color of the row. The text color is used to draw text strings for the
	 * item. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with a zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the starting text color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setStartTextColor(final int row, final int color) {
		setInt(row, VisualItem.STARTTEXTCOLOR, color);
	}

	/**
	 * Get the ending text color of the row. The text color is used to draw text strings for the
	 * item. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row
	 * @return the ending text color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public int getEndTextColor(final int row) {
		return getInt(row, VisualItem.ENDTEXTCOLOR);
	}

	/**
	 * Set the ending text color of the row. The text color is used to draw text strings for the
	 * item. Color values as represented as an integer containing the red, green, blue, and alpha
	 * (transparency) color channels. A color with a zero alpha component is fully transparent and
	 * will not be drawn.
	 *
	 * @param row
	 *            the table row to set
	 * @param color
	 *            the ending text color, represented as an integer
	 * @see prefuse.util.ColorLib
	 */
	public void setEndTextColor(final int row, final int color) {
		setInt(row, VisualItem.ENDTEXTCOLOR, color);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the current size value of the row. Size values are typically used to scale an item,
	 * either in one-dimension (e.g., a bar chart length) or two-dimensions (e.g., using pixel area
	 * to encode a quantitative value).
	 *
	 * @param row
	 *            the table row
	 * @return the current size value
	 */
	public double getSize(final int row) {
		return getDouble(row, VisualItem.SIZE);
	}

	/**
	 * Set the current size value of the row. Size values are typically used to scale an item,
	 * either in one-dimension (e.g., a bar chart length) or two-dimensions (e.g., using pixel area
	 * to encode a quantitative value).
	 *
	 * @param row
	 *            the table row to set
	 * @param size
	 *            the current size value
	 */
	public void setSize(final int row, final double size) {
		setDouble(row, VisualItem.SIZE, size);
	}

	/**
	 * Get the starting size value of the row. Size values are typically used to scale an item,
	 * either in one-dimension (e.g., a bar chart length) or two-dimensions (e.g., using pixel area
	 * to encode a quantitative value).
	 *
	 * @param row
	 *            the table row
	 * @return the starting size value
	 */
	public double getStartSize(final int row) {
		return getDouble(row, VisualItem.STARTSIZE);
	}

	/**
	 * Set the starting size value of the row. Size values are typically used to scale an item,
	 * either in one-dimension (e.g., a bar chart length) or two-dimensions (e.g., using pixel area
	 * to encode a quantitative value).
	 *
	 * @param row
	 *            the table row to set
	 * @param size
	 *            the starting size value
	 */
	public void setStartSize(final int row, final double size) {
		setDouble(row, VisualItem.STARTSIZE, size);
	}

	/**
	 * Get the ending size value of the row. Size values are typically used to scale an item, either
	 * in one-dimension (e.g., a bar chart length) or two-dimensions (e.g., using pixel area to
	 * encode a quantitative value).
	 *
	 * @param row
	 *            the table row
	 * @return the ending size value
	 */
	public double getEndSize(final int row) {
		return getDouble(row, VisualItem.ENDSIZE);
	}

	/**
	 * Set the ending size value of the row. Size values are typically used to scale an item, either
	 * in one-dimension (e.g., a bar chart length) or two-dimensions (e.g., using pixel area to
	 * encode a quantitative value).
	 *
	 * @param row
	 *            the table row to set
	 * @param size
	 *            the ending size value
	 */
	public void setEndSize(final int row, final double size) {
		setDouble(row, VisualItem.ENDSIZE, size);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the current shape builder of the row. This value only has an effect
	 * if a Renderer that supports different shapes is used (e.g.,
	 * {@link prefuse.render.ShapeRenderer}.
	 *
	 * @param row
	 *            the table row
	 * @return the current ShapeBuilder
	 */
	public ShapeBuilder getShapeBuilder(final int row) {
	    return (ShapeBuilder) get(row, VisualItem.SHAPEBUILDER);
	}

	/**
	 * Set the current shape value of the row. This value only has an effect if a Renderer that
	 * supports different shapes is used (e.g., {@link prefuse.render.ShapeRenderer}.
	 *
	 * @param row
	 *            the table row to set
	 * @param shapeBuilder
	 *            the ShapeBuilder to use
	 */
	public void setShapeBuilder(final int row, final ShapeBuilder shapeBuilder) {
		set(row, VisualItem.SHAPEBUILDER, shapeBuilder);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the current stroke used to draw lines and shape outlines for the item at the given row.
	 *
	 * @return the stroke used to draw lines and shape outlines
	 */
	public BasicStroke getStroke(final int row) {
		return (BasicStroke) get(row, VisualItem.STROKE);
	}

	/**
	 * Set the current stroke used to draw lines and shape outlines.
	 *
	 * @param stroke
	 *            the stroke to use to draw lines and shape outlines
	 */
	public void setStroke(final int row, final BasicStroke stroke) {
		set(row, VisualItem.STROKE, stroke);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the current font for the row. The font is used as the default typeface for drawing text
	 * for this item.
	 *
	 * @param row
	 *            the table row
	 * @return the current font value
	 */
	public Font getFont(final int row) {
		return (Font) get(row, VisualItem.FONT);
	}

	/**
	 * Set the current font for the the row. The font is used as the default typeface for drawing
	 * text for this item.
	 *
	 * @param row
	 *            the table row to set
	 * @param font
	 *            the current font value
	 */
	public void setFont(final int row, final Font font) {
		set(row, VisualItem.FONT, font);
	}

	/**
	 * Get the starting font for the row. The font is used as the default typeface for drawing text
	 * for this item.
	 *
	 * @param row
	 *            the table row
	 * @return the starting font value
	 */
	public Font getStartFont(final int row) {
		return (Font) get(row, VisualItem.STARTFONT);
	}

	/**
	 * Set the starting font for the row. The font is used as the default typeface for drawing text
	 * for this item.
	 *
	 * @param row
	 *            the table row to set
	 * @param font
	 *            the starting font value
	 */
	public void setStartFont(final int row, final Font font) {
		set(row, VisualItem.STARTFONT, font);
	}

	/**
	 * Get the ending font for the row. The font is used as the default typeface for drawing text
	 * for this item.
	 *
	 * @param row
	 *            the table row
	 * @return the ending font value
	 */
	public Font getEndFont(final int row) {
		return (Font) get(row, VisualItem.ENDFONT);
	}

	/**
	 * Set the ending font for the row. The font is used as the default typeface for drawing text
	 * for this item.
	 *
	 * @param row
	 *            the table row to set
	 * @param font
	 *            the ending font value
	 */
	public void setEndFont(final int row, final Font font) {
		set(row, VisualItem.ENDFONT, font);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the degree-of-interest (DOI) value. The degree-of-interet is an optional value that can
	 * be used to sort items by importance, control item visibility, or influence particular visual
	 * encodings. A common example is to use the DOI to store the graph distance of a node from the
	 * nearest selected focus node.
	 *
	 * @param row
	 *            the table row
	 * @return the DOI value of this item
	 */
	public double getDOI(final int row) {
		return getDouble(row, VisualItem.DOI);
	}

	/**
	 * Set the degree-of-interest (DOI) value. The degree-of-interet is an optional value that can
	 * be used to sort items by importance, control item visibility, or influence particular visual
	 * encodings. A common example is to use the DOI to store the graph distance of a node from the
	 * nearest selected focus node.
	 *
	 * @param row
	 *            the table row to set
	 * @param doi
	 *            the DOI value of this item
	 */
	public void setDOI(final int row, final double doi) {
		setDouble(row, VisualItem.DOI, doi);
	}

} // end of class VisualTable
