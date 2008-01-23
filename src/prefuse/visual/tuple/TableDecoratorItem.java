package prefuse.visual.tuple;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;

/**
 * DecoratorItem implementation that uses data values from a backing
 * VisualTable.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TableDecoratorItem extends TableVisualItem<TableDecoratorItem>
    implements DecoratorItem<TableDecoratorItem>
{
    /**
     * Initialize a new TableDecoratorItem for the given table and row. This
     * method is used by the appropriate TupleManager instance, and should
     * not be called directly by client code, unless by a client-supplied
     * custom TupleManager.
     * @param table the data Table
     * @param graph ignored by this class
     * @param row the table row index
     */
    @Override
	public void init(Table table, Graph graph, int row) {
        m_table = table;
        m_row = m_table.isValidRow(row) ? row : -1;
    }

    /**
     * @see prefuse.visual.DecoratorItem#getDecoratedItem()
     */
    public VisualItem<?> getDecoratedItem() {
        VisualTable<VisualItem<?>,TableDecoratorItem> vt = (VisualTable<VisualItem<?>,TableDecoratorItem>)getTable();
        int prow = vt.getParentRow(getRow());
        return vt.getParentTable().getTuple(prow);
    }

} // end of class TableDecoratorItem
