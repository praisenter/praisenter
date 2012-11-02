package org.praisenter.slide.text;

/**
 * Represents metrics for some rendered text.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TextMetrics extends TextBounds {
	/** The font size */
	protected float fontSize;
	
	/**
	 * Default constructor.
	 */
	public TextMetrics() {}
	
	/**
	 * Full constructor.
	 * @param fontSize the font size
	 * @param width the text width at the given font size
	 * @param height the text height at the given font size
	 */
	public TextMetrics(float fontSize, float width, float height) {
		super(width, height);
		this.fontSize = fontSize;
	}
	
	/**
	 * Returns the font size that produces
	 * the width and height.
	 * @return float
	 */
	public float getFontSize() {
		return this.fontSize;
	}
	
	/**
	 * Sets the font size that produces the width and height.
	 * @param fontSize the font size
	 */
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}
}
