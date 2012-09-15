package org.praisenter.display;

/**
 * A simple bounds object storing x and y position and width and height.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Bounds {
	/** The x coordinate */
	public int x;
	
	/** The y coordinate */
	public int y;
	
	/** The width */
	public int w;
	
	/** The height */
	public int h;
	
	/** Default constructor */
	public Bounds() {}
	
	/**
	 * Full constructor.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 */
	public Bounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
}
