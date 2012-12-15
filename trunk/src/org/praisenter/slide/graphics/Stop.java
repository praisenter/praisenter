package org.praisenter.slide.graphics;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a stop in a gradient fill operation.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "Stop")
@XmlAccessorType(XmlAccessType.NONE)
public class Stop {
	/** The stop location from 0.0 - 1.0 inclusive */
	@XmlElement(name = "Fraction")
	protected float fraction;
	
	/** The stop color */
	@XmlElement(name = "Color")
	protected ColorFill color;
	
	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected Stop() {
		this(0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	/**
	 * Full constructor.
	 * @param fraction the stop location from 0 to 1 inclusive
	 * @param color the stop color
	 */
	public Stop(float fraction, Color color) {
		this.fraction = fraction;
		this.color = new ColorFill(color);
	}
	
	/**
	 * Full constructor.
	 * @param fraction the stop location from 0 to 1 inclusive
	 * @param red the red color component
	 * @param green the green color component
	 * @param blue the blue color component
	 * @param alpha the alpha color component
	 */
	public Stop(float fraction, float red, float green, float blue, float alpha) {
		this.fraction = fraction;
		this.color = new ColorFill(red, green, blue, alpha);
	}
	
	/**
	 * Full constructor.
	 * @param fraction the stop location from 0 to 1 inclusive
	 * @param red the red color component
	 * @param green the green color component
	 * @param blue the blue color component
	 * @param alpha the alpha color component
	 */
	public Stop(float fraction, int red, int green, int blue, int alpha) {
		this(fraction, new Color(red, green, blue, alpha));
	}

	/**
	 * Returns the stop position.
	 * @return float
	 */
	public float getFraction() {
		return this.fraction;
	}

	/**
	 * Returns the stop color.
	 * @return Color
	 */
	public Color getColor() {
		return this.color.getColor();
	}
}
