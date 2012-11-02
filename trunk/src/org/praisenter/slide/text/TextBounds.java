package org.praisenter.slide.text;

/**
 * Represents the bounds of some rendered text.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TextBounds {
	/** The text width */
	protected float width;
	
	/** The text height */
	protected float height;
	
	/**
	 * Default constructor.
	 */
	public TextBounds() {}
	
	/**
	 * Full constructor.
	 * @param width the width
	 * @param height the height
	 */
	public TextBounds(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Returns the text width.
	 * @return float
	 */
	public float getWidth() {
		return this.width;
	}
	
	/**
	 * Sets the text width.
	 * @param width the width
	 */
	public void setWidth(float width) {
		this.width = width;
	}
	
	/**
	 * Returns the text height.
	 * @return float
	 */
	public float getHeight() {
		return this.height;
	}
	
	/**
	 * Sets the text height.
	 * @param height the height
	 */
	public void setHeight(float height) {
		this.height = height;
	}
}
