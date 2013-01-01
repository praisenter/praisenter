package org.praisenter.easings;

import org.praisenter.resources.Messages;

/**
 * Back easing from http://www.robertpenner.com.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// FIXME this should be a transition more than an easing
public class BackEasing extends AbstractEasing {
	/** The id for the easing */
	public static final int ID = 90;
	
	/** The over-shoot factor */
	private static final double s = 1.70158;
	
	/**
	 * Default constructor.
	 */
	public BackEasing() {
		super(Messages.getString("easing.back"));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.easings.Easing#getEasingId()
	 */
	@Override
	public int getEasingId() {
		return BackEasing.ID;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.easings.Easing#easeOut(long, long)
	 */
	@Override
	public double easeOut(long time, long duration) {
		double t = (double)time / (double)duration;
		return t * t * ((s + 1) * t - s);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.easings.Easing#easeIn(long, long)
	 */
	@Override
	public double easeIn(long time, long duration) {
		double t = (double)time / (double)duration;
		t-=1.0;
		return t * t * ((s + 1) * t + s) + 1;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.easings.Easing#easeInOut(long, long)
	 */
	@Override
	public double easeInOut(long time, long duration) {
		double t = (double)time / (double)duration;
		double s = BackEasing.s * 1.525;
		t *= 2.0;
		if (t < 1.0) {
			return t * t* ((s + 1) * t - s) * 0.5;
		}
		t -= 2;
		return (t * t * ((s + 1) * t + s) + 2) * 0.5;
	}
}
