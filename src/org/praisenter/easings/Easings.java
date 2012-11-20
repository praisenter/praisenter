package org.praisenter.easings;

/**
 * Static class for managing easings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Easings {
	/** The array of supported easings */
	public static final Easing[] EASINGS = new Easing[] {
		new LinearEasing(),
		new QuadraticEasing(),
		new CubicEasing(),
		new QuarticEasing(),
		new QuinticEasing(),
		new SinusoidalEasing(),
		new ExponentialEasing(),
		new CircularEasing()
	};
	
	/**
	 * Returns a easing for the given id.
	 * <p>
	 * Returns null if the easing id is not found.
	 * @param id the easing id
	 * @return {@link Easing}
	 */
	public static final Easing getEasingForId(int id) {
		for (Easing easing : EASINGS) {
			if (easing.getEasingId() == id) {
				return easing;
			}
		}
		return null;
	}
}
