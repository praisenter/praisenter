package org.praisenter.javafx.easing;

class Back extends Easing {
	/** The id for the easing */
	static final int ID = 90;
	
	/** The over-shoot factor */
	private static final double s = 1.70158;
	
	public Back(EasingType type) {
		super(type);
	}

	@Override
	protected double baseCurve(double v) {
		return v * v * ((s + 1) * v - s);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
