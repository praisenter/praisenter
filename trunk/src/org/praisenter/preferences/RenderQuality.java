package org.praisenter.preferences;

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

/**
 * The render quality settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RenderQuality {
	/** The highest quality graphics */
	HIGH,
	
	/** The default quality graphics */
	MEDIUM,
	
	/** The lowest quality graphics */
	LOW;
	
	/** Best quality rendering hints */
	private static final RenderingHints BEST_QUALITY;
	static {
		Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
		map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		map.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		map.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		BEST_QUALITY = new RenderingHints(map);
	}
	
	/** Medium quality rendering hints */
	private static final RenderingHints MEDIUM_QUALITY;
	static {
		Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
		map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		map.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		map.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
		MEDIUM_QUALITY = new RenderingHints(map);
	}
	
	/** Low quality rendering hints */
	private static final RenderingHints LOW_QUALITY;
	static {
		Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
		map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		map.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		map.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		LOW_QUALITY = new RenderingHints(map);
	}
	
	/**
	 * Returns the Java2D rendering hints for this quality.
	 * @return RenderingHints
	 */
	public RenderingHints getRenderingHints() {
		if (this == HIGH) {
			return BEST_QUALITY;
		} else if (this == LOW) {
			return LOW_QUALITY;
		} else {
			return MEDIUM_QUALITY;
		}
	}
}
