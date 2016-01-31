package org.praisenter.javafx.easing;

class Elastic extends Easing {
	/** The id for the easing */
	public static final int ID = 110;
	
	private static final double s = 1;
	private static final double o = 3;
	
	public Elastic(EasingType type) {
		super(type);
	}

	@Override
	protected double baseCurve(double v) {
		if (v == 0) {
            return 0;
        }
        if (v == 1) {
            return 1;
        }
        double p = 1.0 / o;
        double a = Elastic.s;
        double s;
        if (a < Math.abs(1)) {
            a = 1;
            s = p / 4;
        } else {
            s = p / (2 * Math.PI) * Math.asin(1 / a);
        }
        return -(a * Math.pow(2, 10 * (v -= 1)) * Math.sin((v - s) * (2 * Math.PI) / p));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
