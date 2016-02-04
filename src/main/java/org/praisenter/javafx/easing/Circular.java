package org.praisenter.javafx.easing;

class Circular extends Easing {
	/** The id for the easing */
	static final int ID = 80;
	
	public Circular(EasingType type) {
		super(type);
	}
	
	@Override
	protected double baseCurve(double v) {
		return -(Math.sqrt(1.0 - (v * v)) - 1);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
