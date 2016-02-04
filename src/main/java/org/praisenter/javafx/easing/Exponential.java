package org.praisenter.javafx.easing;

class Exponential extends Easing {
	/** The id for the easing */
	static final int ID = 70;
	
	public Exponential(EasingType type) {
		super(type);
	}
	
	@Override
	protected double baseCurve(double v) {
		// 2^(10 * (t - 1))
		return Math.pow(2.0, 10.0 * (v - 1));
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
