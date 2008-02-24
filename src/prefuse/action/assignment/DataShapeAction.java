package prefuse.action.assignment;

import java.util.Map;

import prefuse.ShapeBuilder;
import prefuse.PredefinedShape;
import prefuse.data.tuple.TupleSet;
import prefuse.util.DataLib;
import prefuse.visual.VisualItem;

/**
 * <p>Assignment Action that assigns ShapeBuilder values to VisualItems based on a
 * data field.</p>
 * <p>ShapeBuilders know how to draw certain shapes. The default ShapeBuilders are
 * in the PredefinedShape class. Of course, clients can always create their own
 * ShapeBuilders.
 * </p>
 *
 * <p>The data field will be assumed to be of type nominal, and ShapeBuilders will
 * be assigned to unique values in the order they are encountered. Note that
 * if the number of unique values is greater than the the length of a specified palette
 * then duplicate shapes will start being assigned.</p>
 *
 * <p>This Action only sets the shapeBuilder field of the VisualItem. For this value
 * to have an effect, a renderer instance that takes this shapeBuilder value
 * into account must be used (e.g., {@link prefuse.render.ShapeRenderer}).
 * </p>
 * 
 * @see PredefinedShape
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DataShapeAction extends ShapeAction {

    protected String m_dataField;
    protected ShapeBuilder[]  m_palette;

    protected Map<Object,Integer>    m_ordinalMap;


    /**
     * Create a new DataShapeAction
     * @param group the data group to process
     * @param field the data field to base shape assignments on
     */
    public DataShapeAction(String group, String field) {
        super(group, PredefinedShape.NONE);
        m_dataField = field;
        m_palette = null;
    }

    /**
     * Create a new DataShapeAction.
     * @param group the data group to process
     * @param field the data field to base shape assignments on
     * @param palette a palette of shape values to use for the encoding.
     */
    public DataShapeAction(String group, String field, ShapeBuilder[] palette) {
        super(group, PredefinedShape.NONE);
        m_dataField = field;
        m_palette = palette;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the data field used to encode shape values.
     * @return the data field that is mapped to shape values
     */
    public String getDataField() {
        return m_dataField;
    }

    /**
     * Set the data field used to encode shape values.
     * @param field the data field to map to shape values
     */
    public void setDataField(String field) {
        m_dataField = field;
    }

    /**
     * This operation is not supported by the DataShapeAction type.
     * Calling this method will result in a thrown exception.
     * @see prefuse.action.assignment.ShapeAction#setDefaultShape(PredefinedShape)
     * @throws UnsupportedOperationException
     */
    public void setDefaultShape(ShapeBuilder defaultShape) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.EncoderAction#setup()
     */
    @Override
	protected void setup() {
        TupleSet<? extends VisualItem<?>> ts = m_vis.getGroup(m_group);
        m_ordinalMap = DataLib.ordinalMap(ts, m_dataField);
    }

    /**
     * @see prefuse.action.assignment.ShapeAction#getShape(prefuse.visual.VisualItem)
     */
    @Override
	public ShapeBuilder getShape(VisualItem<?> item) {
        // check for any cascaded rules first
    	ShapeBuilder shape = super.getShape(item);
        if ( shape != PredefinedShape.NONE ) {
            return shape;
        }

        // otherwise perform data-driven assignment
        Object v = item.get(m_dataField);
        int idx = m_ordinalMap.get(v);

        if ( m_palette == null ) {
        	// use the PredefinedShapes as the palette
        	PredefinedShape[] shapes = PredefinedShape.values();
        	// exclude ShapeType.NONE
        	return shapes[idx % (shapes.length - 1) + 1];
        } else {
            return m_palette[idx % m_palette.length];
        }
    }

} // end of class DataShapeAction
