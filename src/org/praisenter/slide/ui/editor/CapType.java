package org.praisenter.slide.ui.editor;

import java.awt.BasicStroke;

/**
 * Enumeration of the supported cap types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum CapType {
	/** The rounded cap type */
	ROUND(BasicStroke.CAP_ROUND),
	
	/** The squared cap type */
	SQUARE(BasicStroke.CAP_SQUARE);
	
	/** The Java2D stroke value */
	private final int strokeValue;
	
	/**
	 * Minimal constructor.
	 * @param strokeValue the Java2D stroke value
	 */
	private CapType(int strokeValue) {
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
	 * @return {@link CapType}
	 */
	public static CapType getCapType(int value) {
		if (value == BasicStroke.CAP_ROUND) {
			return CapType.ROUND;
		} else {
			return CapType.SQUARE;
		}
	}
}
