package org.praisenter.javafx.easing;

import javafx.animation.Interpolator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class Easing extends Interpolator {
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
}
