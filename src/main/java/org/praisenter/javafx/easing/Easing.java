package org.praisenter.javafx.easing;

import javafx.animation.Interpolator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class Easing extends Interpolator {
	public static final Easing LINEAR = new Linear(EasingType.IN);
	
	public static final Easing IN_QUADRATIC = new Quadratic(EasingType.IN);
	public static final Easing IN_CUBIC = new Cubic(EasingType.IN);
	public static final Easing IN_QUARTIC = new Quartic(EasingType.IN);
	public static final Easing IN_QUINTIC = new Quintic(EasingType.IN);
	public static final Easing IN_CIRCULAR = new Circular(EasingType.IN);
	public static final Easing IN_EXPONENTIAL = new Exponential(EasingType.IN);
	public static final Easing IN_SINUSOIDAL = new Sinusoidal(EasingType.IN);
	public static final Easing IN_BACK = new Back(EasingType.IN);
	public static final Easing IN_BOUNCE = new Bounce(EasingType.IN);
	public static final Easing IN_ELASTIC = new Elastic(EasingType.IN);
	
	public static final Easing OUT_QUADRATIC = new Quadratic(EasingType.OUT);
	public static final Easing OUT_CUBIC = new Cubic(EasingType.OUT);
	public static final Easing OUT_QUARTIC = new Quartic(EasingType.OUT);
	public static final Easing OUT_QUOUTTIC = new Quintic(EasingType.OUT);
	public static final Easing OUT_CIRCULAR = new Circular(EasingType.OUT);
	public static final Easing OUT_EXPONENTIAL = new Exponential(EasingType.OUT);
	public static final Easing OUT_SOUTUSOIDAL = new Sinusoidal(EasingType.OUT);
	public static final Easing OUT_BACK = new Back(EasingType.OUT);
	public static final Easing OUT_BOUNCE = new Bounce(EasingType.OUT);
	public static final Easing OUT_ELASTIC = new Elastic(EasingType.OUT);
	
	final EasingType type;

    public Easing(EasingType type) {
        this.type = type;
    }

    public EasingType getEasingMode() {
        return this.type;
    }

    public abstract int getId();
    
    /**
     * Defines the base curve for the interpolator.
     * The base curve is then transformed into an easing-in, easing-out easing-both curve.
     *
     * @param v The normalized value/time/progress of the interpolation (between 0 and 1).
     * @return The resulting value of the function, should return a value between 0 and 1.
     * @see Interpolator#curve(double)
     */
    protected abstract double baseCurve(final double v);

    /**
     * Curves the function depending on the easing mode.
     *
     * @param v The normalized value (between 0 and 1).
     * @return The resulting value of the function.
     */
    @Override
    protected final double curve(final double v) {
        switch (this.type) {
            case IN:
                return baseCurve(v);
            case OUT:
                return 1 - baseCurve(1 - v);
            case BOTH:
                if (v <= 0.5) {
                    return baseCurve(2 * v) / 2;
                } else {
                    return (2 - baseCurve(2 * (1 - v))) / 2;
                }

        }
        return baseCurve(v);
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Easing) {
			Easing o = (Easing)obj;
			if (o.getId() == this.getId()) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getId();
	}
}
