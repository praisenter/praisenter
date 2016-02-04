package org.praisenter.javafx.easing;

class Sinusoidal extends Easing {
	/** The id for the easing */
	static final int ID = 60;
	
	public Sinusoidal(EasingType type) {
		super(type);
	}
	
	@Override
	protected double baseCurve(double v) {
		return -Math.cos(v * Math.PI * 0.5) + 1.0;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
