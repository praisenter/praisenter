package org.praisenter.utility;

public final class Colors {
	private Colors() {}
	
	// if we need a better difference calculation see these:
	// https://stackoverflow.com/questions/9018016/how-to-compare-two-colors-for-similarity-difference
	// https://www.compuphase.com/cmetric.htm
	
	/**
	 * Returns the euclidian distance between the two colors.
	 * @param r1 the red component of the first color
	 * @param g1 the green component of the first color
	 * @param b1 the blue component of the first color
	 * @param a1 the alpha component of the first color
	 * @param r2 the red component of the second color
	 * @param g2 the green component of the second color
	 * @param b2 the blue component of the second color
	 * @param a2 the alpha component of the second color
	 * @return double
	 */
	public static final double distanceSquared(double r1, double g1, double b1, double a1, double r2, double g2, double b2, double a2) {
		double r = r2 - r1;
		double g = g2 - g1;
		double b = b2 - b1;
		double a = a2 - a1;
		return r * r + g * g + b * b + a * a;
	}
}
