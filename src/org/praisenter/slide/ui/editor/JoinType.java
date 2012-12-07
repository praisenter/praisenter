package org.praisenter.slide.ui.editor;

import java.awt.BasicStroke;

/**
 * Enumeration of the supported join types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum JoinType {
	/** The default bevel join type */
	BEVEL(BasicStroke.JOIN_BEVEL),
	
	/** The miter join type */
	MITER(BasicStroke.JOIN_MITER),
	
	/** The round join type */
	ROUND(BasicStroke.JOIN_ROUND);
	
	/** The Java2D stroke value */
	private final int strokeValue;
	
	/**
	 * Minimal constructor.
	 * @param strokeValue the Java2D stroke value
	 */
	private JoinType(int strokeValue) {
		this.strokeValue = strokeValue;
	}

	/**
	 * Returns the Java2D stroke value.
	 * @return int
	 */
	public int getStrokeValue() {
		return this.strokeValue;
	}
	
	/**
	 * Returns the enum for the given Java2D stroke value.
	 * @param value the Java2D stroke value
	 * @return {@link JoinType}
	 */
	public static JoinType getJoinType(int value) {
		if (value == BasicStroke.JOIN_MITER) {
			return JoinType.MITER;
		} else if (value == BasicStroke.JOIN_ROUND) {
			return JoinType.ROUND;
		} else {
			return JoinType.BEVEL;
		}
	}
}
