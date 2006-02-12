package prefuse.data.query;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prefuse.data.Table;
import prefuse.data.column.ColumnMetadata;
import prefuse.data.expression.BooleanLiteral;
import prefuse.data.expression.ColumnExpression;
import prefuse.data.expression.ComparisonPredicate;
import prefuse.data.expression.Expression;
import prefuse.data.expression.Literal;
import prefuse.data.expression.OrPredicate;
import prefuse.data.expression.Predicate;
import prefuse.util.ui.JToggleGroup;

/**
 * DynamicQueryBinding supporting queries based on a list of included
 * data values.
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ListQueryBinding extends DynamicQueryBinding {

    /** String used to indicate inclusion of all data values. */
    private static final String ALL = "All";
    
    private Class m_type;
    private ListModel m_model;
    private Listener m_lstnr;
    
    /**
     * Create a new ListQueryBinding over the given table and data field.
     * @param t the Table to query
     * @param field the data field (Table column) to query
     */
    public ListQueryBinding(Table t, String field) {
        super(t, field);
        m_type = t.getColumnType(field);
        m_lstnr = new Listener();
        initPredicate();
        initModel();
    }
    
    private void initPredicate() {
        // set up predicate
        OrPredicate orP = new OrPredicate();
        orP.add(BooleanLiteral.TRUE);
        setPredicate(orP);
    }
    
    private void initModel() {        
        if ( m_model != null )
            m_model.removeListSelectionListener(m_lstnr);
        
        // set up data / selection model
        ColumnMetadata md = m_table.getMetadata(m_field);
        Object[] o = md.getOrdinalArray();
        m_model = new ListModel(o);
        m_model.addListSelectionListener(m_lstnr);
        m_model.insertElementAt(ALL, 0);
        m_model.setSelectedItem(ALL);
    }

    // ------------------------------------------------------------------------    
    
    /**
     * Creates a new group of check boxes for interacting with the query.
     * @return a {@link prefuse.util.ui.JToggleGroup} of check boxes bound to
     * this dynamic query.
     * @see prefuse.data.query.DynamicQueryBinding#createComponent()
     */
    public JComponent createComponent() {
        return createCheckboxGroup();
    }
    
    /**
     * Create a new interactive list for interacting with the query.
     * @return a {@link javax.swing.JList} bound to this dynamic query.
     */
    public JList createList() {
        JList list = new JList(m_model);
        list.setSelectionModel(m_model);
        return list;
    }
    
    /**
     * Create a new drop-down combo box for interacting with the query.
     * @return a {@link javax.swing.JComboBox} bound to this dynamic query.
     */
    public JComboBox createComboBox() {
        return new JComboBox(m_model);
    }

    /**
     * Creates a new group of check boxes for interacting with the query.
     * @return a {@link prefuse.util.ui.JToggleGroup} of check boxes bound to
     * this dynamic query.
     */
    public JToggleGroup createCheckboxGroup() {
        return createToggleGroup(JToggleGroup.CHECKBOX);
    }
    
    /**
     * Creates a new group of radio buttons for interacting with the query.
     * @return a {@link prefuse.util.ui.JToggleGroup} of radio buttons bound to
     * this dynamic query.
     */
    public JToggleGroup createRadioGroup() {
        return createToggleGroup(JToggleGroup.RADIO);
    }
    
    private JToggleGroup createToggleGroup(int type) {
        return new JToggleGroup(type, m_model, m_model);
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Create a comparison predicate fof the given data value
     */
    private ComparisonPredicate getComparison(Object o) {
        Expression left = new ColumnExpression(m_field);
        Expression right = Literal.getLiteral(o, m_type);
        return new ComparisonPredicate(ComparisonPredicate.EQ, left, right);
    }
    
    private class Listener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            ListModel model = (ListModel)e.getSource();
            OrPredicate orP = (OrPredicate)m_query;
            
            if ( model.isSelectionEmpty() )
            {
                orP.clear();
            }
            else if ( model.isSelectedIndex(0) )
            {
                orP.set(BooleanLiteral.TRUE);
            }
            else
            {
                int min   = model.getMinSelectionIndex();
                int max   = model.getMaxSelectionIndex();
                int count = 0;
                for ( int i=min; i<=max; ++i ) {
                    if ( model.isSelectedIndex(i) )
                        ++count;
                }
            
                if ( count == model.getSize() ) {
                    orP.set(BooleanLiteral.TRUE);
                } else if ( count == 1 ) {
                    orP.set(getComparison(model.getElementAt(min)));
                } else {
                    Predicate[] p = new Predicate[count];
                    for ( int i=min, j=0; i<=max; ++i ) {
                        if ( model.isSelectedIndex(i) )
                            p[j++] = getComparison(model.getElementAt(i));
                    }
                    orP.set(p);
                }
            }
        }
    }
    
} // end of class ListQueryBinding
