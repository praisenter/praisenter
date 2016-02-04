package org.praisenter.javafx.easing;

public final class Easings {
	private Easings() {}
	
	public static final Easing getLinear() { return new Linear(EasingType.IN); }
	public static final Easing getQuadratic(EasingType type) { return new Quadratic(type); }
	public static final Easing getCubic(EasingType type) { return new Cubic(type); }
	public static final Easing getQuartic(EasingType type) { return new Quartic(type); }
	public static final Easing getQintic(EasingType type) { return new Quintic(type); }
	public static final Easing getCircular(EasingType type) { return new Circular(type); }
	public static final Easing getExponential(EasingType type) { return new Exponential(type); }
	public static final Easing getSinusoidal(EasingType type) { return new Sinusoidal(type); }
	public static final Easing getBack(EasingType type) { return new Back(type); }
	public static final Easing getBounce(EasingType type) { return new Bounce(type); }
	public static final Easing getElastic(EasingType type) { return new Elastic(type); }
	
	public static final Easing get(int id, EasingType type) { 
		if (Quadratic.ID == id) {
			return getQuadratic(type);
		} else if (Cubic.ID == id) {
			return getCubic(type);
		} else if (Quartic.ID == id) {
			return getQuartic(type);
		} else if (Quintic.ID == id) {
			return getQintic(type);
		} else if (Circular.ID == id) {
			return getCircular(type);
		} else if (Exponential.ID == id) {
			return getExponential(type);
		} else if (Sinusoidal.ID == id) {
			return getSinusoidal(type);
		} else if (Back.ID == id) {
			return getBack(type);
		} else if (Bounce.ID == id) {
			return getBounce(type);
		} else if (Elastic.ID == id) {
			return getElastic(type);
		}
		return getLinear();
	}
}
