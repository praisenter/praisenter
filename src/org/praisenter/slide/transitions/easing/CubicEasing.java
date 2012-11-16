package org.praisenter.slide.transitions.easing;

import org.praisenter.resources.Messages;

/**
 * Cubic easing from http://gizma.com/easing/.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class CubicEasing extends AbstractEasing {
	/** The id for the easing */
	public static final int ID = 30;

	/**
	 * Default constructor.
	 */
	public CubicEasing() {
		super(Messages.getString("easing.cubic"));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeIn(long, long)
	 */
	@Override
	public double easeIn(long time, long duration) {
		double t = (double)time / (double)duration;
		return t * t * t;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeOut(long, long)
	 */
	@Override
	public double easeOut(long time, long duration) {
		double t = (double)time / (double)duration;
		t -= 1.0;
		return t * t * t + 1;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeInOut(long, long)
	 */
	@Override
	public double easeInOut(long time, long duration) {
		double t = (double)time / ((double)duration * 0.5);
		if (t < 1.0) {
			return t * t * t * 0.5;
		}
		t -= 2.0;
		return (t * t * t + 2.0) * 0.5;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getEasingId() {
		return ID;
	}
}
