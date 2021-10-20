package org.praisenter.utility;

public final class Numbers {
	private Numbers() {}
	
	/**
	 * Clamps the given value between the given max and min.
	 * @param value the value
	 * @param min the min value
	 * @param max the max value
	 * @return int the clamped value
	 */
	public static final int clamp(int value, int min, int max) {
		return value < min 
				? min
				: value > max 
					? max
					: value;
	}
	
	/**
	 * Clamps the given value between the given max and min.
	 * @param value the value
	 * @param min the min value
	 * @param max the max value
	 * @return int the clamped value
	 */
    public static double clamp(double value, double min, double max) {
        return value < min 
        		? min 
        		: value > max 
        			? max 
        			: value;
    }
}
