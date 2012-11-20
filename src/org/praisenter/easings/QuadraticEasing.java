package org.praisenter.easings;

import org.praisenter.resources.Messages;

/**
 * Quadratic easing from http://gizma.com/easing/.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class QuadraticEasing extends AbstractEasing {
	/** The id for the easing */
	public static final int ID = 20;

	/**
	 * Default constructor.
	 */
	public QuadraticEasing() {
		super(Messages.getString("easing.quadratic"));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeIn(long, long)
	 */
	@Override
	public double easeIn(long time, long duration) {
		if (time > duration) return 1.0;
		double t = (double)time / (double)duration;
		return t * t;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeOut(long, long)
	 */
	@Override
	public double easeOut(long time, long duration) {
		if (time > duration) return 1.0;
		double t = (double)time / (double)duration;
		return -t * (t - 2.0);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeInOut(long, long)
	 */
	@Override
	public double easeInOut(long time, long duration) {
		if (time > duration) return 1.0;
		double t = (double)time / ((double)duration * 0.5);
		if (t < 1.0) {
			return t * t * 0.5;
		}
		t -= 1.0;
		return -(t * (t - 2.0) - 1.0) * 0.5;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getEasingId() {
		return ID;
	}
}
