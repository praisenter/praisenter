package org.praisenter.slide.graphics;

import java.awt.Color;
import java.awt.RadialGradientPaint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a radial gradient {@link Fill} .
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "RadialGradientFill")
@XmlAccessorType(XmlAccessType.NONE)
public class RadialGradientFill extends AbstractGradientFill implements Fill {
	/** The gradient direction */
	@XmlElement(name = "Direction")
	protected RadialGradientDirection direction;
	
	/**
	 * Default constructor.
	 */
	public RadialGradientFill() {
		super();
		this.direction = RadialGradientDirection.CENTER;
	}
	
	/**
	 * Full constructor.
	 * @param direction the center location
	 * @param stops the stops
	 */
	public RadialGradientFill(RadialGradientDirection direction, Stop... stops) {
		super(stops);
		this.direction = direction;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Fill#getPaint(int, int, int, int)
	 */
	@Override
	public RadialGradientPaint getPaint(int x, int y, int w, int h) {
		int size = this.stops.length;
		
		// get the fractions and colors
		float[] fractions = new float[size];
		Color[] colors = new Color[size];
		for (int i = 0; i < size; i++) {
			Stop stop = this.stops[i];
			fractions[i] = stop.fraction;
			colors[i] = stop.color.getPaint(x, y, w, h);
		}
		
		// get the start and end points
		float cx, cy, r = (float)Math.hypot(w, h);
		if (this.direction == RadialGradientDirection.BOTTOM_LEFT) {
			cx = x;
			cy = y + h;
		} else if (this.direction == RadialGradientDirection.BOTTOM_RIGHT) {
			cx = x + w;
			cy = y + h;
		} else if (this.direction == RadialGradientDirection.TOP_LEFT) {
			cx = x;
			cy = y;
		} else if (direction == RadialGradientDirection.TOP_RIGHT) {
			cx = x + w;
			cy = y;
		} else {
			cx = x + w / 2;
			cy = y + h / 2;
			r = r / 2;
		}
		r += OFFSET;
		
		return new RadialGradientPaint(cx, cy, r, fractions, colors);
	}

	/**
	 * Returns the center location.
	 * @return {@link RadialGradientDirection}
	 */
	public RadialGradientDirection getDirection() {
		return this.direction;
	}
}
