package org.praisenter.javafx.easing;

class Quadratic extends Easing {
	/** The id for the easing */
	public static final int ID = 20;
	
	public Quadratic(EasingType type) {
		super(type);
	}
	
	@Override
	protected double baseCurve(double v) {
		return v * v;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
