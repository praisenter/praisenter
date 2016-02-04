package org.praisenter.javafx.easing;

class Bounce extends Easing {
	/** The id for the easing */
	static final int ID = 100;
	
	public Bounce(EasingType type) {
		super(type);
	}

	@Override
	protected double baseCurve(double v) {
		for (double a = 0, b = 1; true; a += b, b /= 2) {
            if (v >= (7 - 4 * a) / 11) {
            	double d = (11 - 6 * a - 11 * v) / 4;
                return -(d * d) + (b * b);
            }
        }
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
