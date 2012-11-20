package org.praisenter.easings;

/**
 * Interface representing an easing function.
 * <p>
 * Easing function provide smooth transtitions from one state to another.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Easing {
	/**
	 * Eases in.
	 * <p>
	 * Returns the percentage of completion of the easing function.
	 * @param time the current time (the total elapsed time)
	 * @param duration the easing duration
	 * @return double
	 */
	public double easeIn(long time, long duration);
	
	/**
	 * Eases out.
	 * <p>
	 * Returns the percentage of completion of the easing function.
	 * @param time the current time (the total elapsed time)
	 * @param duration the easing duration
	 * @return double
	 */
	public double easeOut(long time, long duration);
	
	/**
	 * Eases in half way and eases out half way.
	 * <p>
	 * Returns the percentage of completion of the easing function.
	 * @param time the current time (the total elapsed time)
	 * @param duration the easing duration
	 * @return double
	 */
	public double easeInOut(long time, long duration);

	/**
	 * Returns the easing name.
	 * @return String
	 */
	public abstract String getName();
	
	/**
	 * Returns a unique easing id for an easing sub class.
	 * @return int
	 */
	public abstract int getEasingId();
}
