package org.praisenter.easings;

import org.praisenter.resources.Messages;

/**
 * Exponential easing from http://gizma.com/easing/.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExponentialEasing extends AbstractEasing {
	/** The id for the easing */
	public static final int ID = 70;

	/**
	 * Default constructor.
	 */
	public ExponentialEasing() {
		super(Messages.getString("easing.exponential"));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeIn(long, long)
	 */
	@Override
	public double easeIn(long time, long duration) {
		double t = (double)time / (double)duration;
		// 2^(10 * (t - 1))
		return Math.pow(2.0, 10.0 * (t - 1));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeOut(long, long)
	 */
	@Override
	public double easeOut(long time, long duration) {
		double t = (double)time / (double)duration;
		return -Math.pow(2.0, -10.0 * t) + 1.0;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeInOut(long, long)
	 */
	@Override
	public double easeInOut(long time, long duration) {
		double t = (double)time / ((double)duration * 0.5);
		if (t < 1.0) {
			return Math.pow(2.0, 10.0 * (t - 1)) * 0.5;
		}
		t -= 1.0;
		return (-Math.pow(2.0, 10.0 * t) + 2.0) * 0.5;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getEasingId() {
		return ID;
	}
}
