package org.praisenter.slide.graphics;

import java.awt.Color;
import java.awt.LinearGradientPaint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a linear gradient {@link Fill} .
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "LinearGradientFill")
@XmlAccessorType(XmlAccessType.NONE)
public class LinearGradientFill extends AbstractGradientFill implements Fill {
	/** The gradient direction */
	@XmlElement(name = "Direction")
	protected LinearGradientDirection direction;
	
	/**
	 * Default constructor.
	 */
	public LinearGradientFill() {
		super();
		this.direction = LinearGradientDirection.TOP;
	}
	
	/**
	 * Full constructor.
	 * @param direction the gradient direction
	 * @param stops the stops
	 */
	public LinearGradientFill(LinearGradientDirection direction, Stop... stops) {
		super(stops);
		this.direction = direction;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Fill#getPaint(int, int, int, int)
	 */
	@Override
	public LinearGradientPaint getPaint(int x, int y, int w, int h) {
		int size = this.stops.length;
		
		// offset the gradient a bit
		// this allows the middle stop to be all the way to either end of
		// the gradient without a nasty line of the first color being visible
		x -= OFFSET;
		y -= OFFSET;
		w += OFFSET * 2;
		h += OFFSET * 2;
		
		// get the fractions and colors
		float[] fractions = new float[size];
		Color[] colors = new Color[size];
		for (int i = 0; i < size; i++) {
			Stop stop = this.stops[i];
			fractions[i] = stop.fraction;
			colors[i] = stop.getColor();
		}
		
		// get the start and end points
		float sx, sy, ex, ey;
		if (this.direction == LinearGradientDirection.BOTTOM) {
			sx = x; sy = y + h;
			ex = x; ey = y;
		} else if (this.direction == LinearGradientDirection.BOTTOM_LEFT) {
			sx = x; sy = y + h;
			ex = x + w; ey = y;
		} else if (this.direction == LinearGradientDirection.BOTTOM_RIGHT) {
			sx = x + w; sy = y + h;
			ex = x; ey = y;
		} else if (this.direction == LinearGradientDirection.LEFT) {
			sx = x; sy = y;
			ex = x + w; ey = y;
		} else if (this.direction == LinearGradientDirection.RIGHT) {
			sx = x + w; sy = y;
			ex = x; ey = y;
		} else if (this.direction == LinearGradientDirection.TOP) {
			sx = x; sy = y;
			ex = x; ey = y + h;
		} else if (this.direction == LinearGradientDirection.TOP_LEFT) {
			sx = x; sy = y;
			ex = x + w; ey = y + h;
		} else { //if (direction == LinearGradientDirection.TOP_RIGHT) {
			sx = x + w; sy = y;
			ex = x; ey = y + h;
		}
		
		return new LinearGradientPaint(sx, sy, ex, ey, fractions, colors);
	}

	/**
	 * Returns the gradient direction.
	 * @return {@link LinearGradientDirection}
	 */
	public LinearGradientDirection getDirection() {
		return this.direction;
	}
}
