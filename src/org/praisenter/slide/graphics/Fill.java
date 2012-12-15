package org.praisenter.slide.graphics;

import java.awt.Paint;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface for the various types of fills (color, gradient, texture, etc.).
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "Fill")
public interface Fill {
	/**
	 * Returns a paint for the given coordinates and width and height.
	 * <p>
	 * Some paints require the positioning and width/height of what they will
	 * paint to be used properly.
	 * @param x the x coordinate
	 * @param y the y coorindate
	 * @param w the width
	 * @param h the height
	 * @return Paint
	 */
	public abstract Paint getPaint(int x, int y, int w, int h);
}
