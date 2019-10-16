package org.praisenter.ui.slide.transition;

import java.util.function.Function;

import org.praisenter.data.slide.effects.ease.EasingFunction;
import org.praisenter.data.slide.effects.ease.EasingType;

import javafx.animation.Interpolator;

public class CustomInterpolator extends Interpolator {
	final EasingFunction function;
	final EasingType type;
	final Function<Double, Double> fn;
	
	public CustomInterpolator(EasingFunction function, EasingType type) {
		this.function = function == null ? EasingFunction.LINEAR : function;
		this.type = type == null ? EasingType.IN : type;
		
		Function<Double, Double> fn = this::linear;
		switch (this.function) {
			case LINEAR: fn = this::linear; break;
			case QUADRATIC: fn = this::quadratic; break;
			case CUBIC: fn = this::cubic; break;
			case QUARTIC: fn = this::quartic; break;
			case QUINTIC: fn = this::quintic; break;
			case SINUSOIDAL: fn = this::sinusoidal; break;
			case CIRCULAR: fn = this::circular; break;
			case EXPONENTIAL: fn = this::exponential; break;
			case BACK: fn = this::back; break;
			case BOUNCE: fn = this::bounce; break;
			case ELASTIC: fn = this::elastic; break;
			default: fn = this::linear; break;
		}
		this.fn = fn;
	}
	
	@Override
	protected double curve(final double v) {
        switch (this.type) {
            case IN:
                return this.fn.apply(v);
            case OUT:
                return 1.0 - this.fn.apply(1.0 - v);
            case BOTH:
                if (v <= 0.5) {
                    return this.fn.apply(2.0 * v) / 2.0;
                } else {
                    return (2.0 - this.fn.apply(2.0 * (1.0 - v))) / 2.0;
                }

        }
        return this.fn.apply(v);
    }
	
	protected double sinusoidal(double v) {
		return -Math.cos(v * Math.PI * 0.5) + 1.0;
	}
	
	protected double circular(double v) {
		return -(Math.sqrt(1.0 - (v * v)) - 1.0);
	}

	protected double exponential(double v) {
		// 2^(10 * (t - 1))
		return Math.pow(2.0, 10.0 * (v - 1.0));
	}
	
	protected double linear(double v) {
		return v;
	}
	
	protected double quadratic(double v) {
		return v * v;
	}
	
	protected double cubic(double v) {
		return v * v * v;
	}
	
	protected double quartic(double v) {
		return v * v * v * v;
	}
	
	protected double quintic(double v) {
		return v * v * v * v * v;
	}

	protected double bounce(double v) {
		for (double a = 0, b = 1; true; a += b, b /= 2) {
            if (v >= (7 - 4 * a) / 11) {
            	double d = (11 - 6 * a - 11 * v) / 4;
                return -(d * d) + (b * b);
            }
        }
	}
	
	private static final double BACK_S = 1.70158;
	protected double back(double v) {
		return v * v * ((BACK_S + 1) * v - BACK_S);
	}
	
	private static final double ELASTIC_OVERSHOOT_FACTOR = 1;
	private static final double ELASTIC_NUMBER_OF_OSCILLATIONS = 3;
	protected double elastic(double v) {
		if (v == 0) {
            return 0;
        }
        if (v == 1) {
            return 1;
        }
        double p = 1.0 / ELASTIC_NUMBER_OF_OSCILLATIONS;
        double a = ELASTIC_OVERSHOOT_FACTOR;
        double s;
        if (a < Math.abs(1)) {
            a = 1;
            s = p / 4;
        } else {
            s = p / (2 * Math.PI) * Math.asin(1 / a);
        }
        return -(a * Math.pow(2, 10 * (v -= 1)) * Math.sin((v - s) * (2 * Math.PI) / p));
	}
	
}
