package org.praisenter.javafx.easing;

class Quartic extends Easing {
	/** The id for the easing */
	static final int ID = 40;

	public Quartic(EasingType type) {
		super(type);
	}
	
	@Override
	protected double baseCurve(double v) {
		return v * v * v * v;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
