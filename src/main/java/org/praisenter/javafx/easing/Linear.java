package org.praisenter.javafx.easing;

class Linear extends Easing {
	/** The id for the easing */
	public static final int ID = 10;

	public Linear(EasingType type) {
		super(type);
	}
	
	@Override
	protected double baseCurve(double v) {
		return v;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
