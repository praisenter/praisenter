package org.praisenter.javafx.easing;

class Cubic extends Easing {
	/** The id for the easing */
	public static final int ID = 30;
	
	public Cubic(EasingType type) {
		super(type);
	}

	@Override
	protected double baseCurve(double v) {
		return v * v * v;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
