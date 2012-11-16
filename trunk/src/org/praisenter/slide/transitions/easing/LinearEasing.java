package org.praisenter.slide.transitions.easing;

import org.praisenter.resources.Messages;

/**
 * Standard linear easing.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class LinearEasing extends AbstractEasing {
	/** The id for the easing */
	public static final int ID = 10;

	/**
	 * Default constructor.
	 */
	public LinearEasing() {
		super(Messages.getString("easing.linear"));
	}
	
	/**
	 * The linear easing function.
	 * <p>
	 * Returns the percentage of completion of the easing function.
	 * @param time the current time (the total elapsed time)
	 * @param duration the easing duration
	 * @return double
	 */
	private static final double ease(long time, long duration) {
		return (double)time / (double)duration;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeIn(long, long)
	 */
	@Override
	public double easeIn(long time, long duration) {
		return ease(time, duration);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeOut(long, long)
	 */
	@Override
	public double easeOut(long time, long duration) {
		return ease(time, duration);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeInOut(long, long)
	 */
	@Override
	public double easeInOut(long time, long duration) {
		return ease(time, duration);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getEasingId() {
		return ID;
	}
}
