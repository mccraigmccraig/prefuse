/*
 * Created on Jul 12, 2004
 */
package edu.berkeley.guir.prefuse.timeline;

/**
 * A bag of constants common to all Timeline applications.
 * 
 * @author Jack Li jack(AT)cs_D0Tberkeley_D0Tedu
 */
public interface TimelineConstants {
	// Attribute types (in addition to ID and Label)
	public static final String NODE_TYPE = "nodetype";
	public static final String START_YEAR = "startyear";
	public static final String END_YEAR = "endyear";
	
	// Attribute type: NODE_TYPE
	public static final String NOTCH_TYPE = "notch";
	public static final String PERIOD_TYPE = "period";
	public static final String EVENT_TYPE = "event";
	public static final String PERSON_TYPE = "person";
	public static final String PIECE_TYPE = "piece";
	
	// Attribute type: START_YEAR
	public static final String TIMELINE_START = "timeline_start";
	
	// Attribute type: END_YEAR
	public static final String TIMELINE_END = "timeline_end";
	
	// Label types for attribute type NOTCH_TYPE	
	public static final String NOTCH = "notch"; // (these are helpers, not actual NOTCH_TYPEs)
	public static final String START = "start"; // (these are helpers, not actual NOTCH_TYPEs)
	public static final String END = "end"; // (these are helpers, not actual NOTCH_TYPEs)
	public static final String NOTCH_START = NOTCH+START;
	public static final String NOTCH_END = NOTCH+END;

	// Visual Node Attributes
	public static final String LEFT_NORMAL = "left_normal";
	public static final String RIGHT_NORMAL = "right_normal";
	public static final String LEFT_DISTORTED = "left_distorted";
	public static final String RIGHT_DISTORTED = "right_distorted";
}
