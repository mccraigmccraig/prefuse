/*
 * Created on Aug 19, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

import java.awt.Color;
import java.awt.Paint;

import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;

/**
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public class MusicHistoryColorFunction extends ColorFunction implements TimelineConstants{
    private Color pastelOrange = new Color(255,200,125);

    public Paint getColor(VisualItem item) {
        if ( item instanceof NodeItem ) {
            if (item.isFocus()) 
            	return Color.MAGENTA;
            else if ( item.isHighlighted() )
                return pastelOrange;
            else
                return Color.BLACK;//Color.LIGHT_GRAY;
        } else {
            return Color.BLACK;
        }
    }
    
    public Paint getFillColor(VisualItem item) {
        if (item instanceof NodeItem) {
            final String nodeType = item.getAttribute(NODE_TYPE);
            if (nodeType.equals(PERIOD_TYPE)) {
                return Color.BLUE;
            } else if (nodeType.equals(EVENT_TYPE)) {
                return Color.RED;
            } else if (nodeType.equals(PERSON_TYPE)) {
                return Color.GRAY;
            } else if (nodeType.equals(PIECE_TYPE)) {
                return Color.DARK_GRAY;
            } else {
                return Color.MAGENTA;
            }
        } else {
            return super.getFillColor(item);
        }
    }
}
