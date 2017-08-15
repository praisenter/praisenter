package org.praisenter.slide.effects;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SlideColorAdjust {
	/** The hue */
	@JsonProperty
	final double hue;
	
	/** The saturation */
	@JsonProperty
	final double saturation;
	
	/** The brightness */
	@JsonProperty
	final double brightness;

	/** The contrast */
	@JsonProperty
	final double contrast;
	
	public SlideColorAdjust() {
		this(0.0, 0.0, 0.0, 0.0);
	}

	public SlideColorAdjust(double hue, double saturation, double brightness, double contrast) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
		this.contrast = contrast;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 23;
		hash = 31 * hash + Double.hashCode(this.hue);
		hash = 31 * hash + Double.hashCode(this.saturation);
		hash = 31 * hash + Double.hashCode(this.brightness);
		hash = 31 * hash + Double.hashCode(this.contrast);
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideColorAdjust) {
			SlideColorAdjust adjust = (SlideColorAdjust)obj;
			return adjust.hue == this.hue &&
				   adjust.saturation == this.saturation &&
				   adjust.brightness == this.brightness &&
				   adjust.contrast == this.contrast;
		}
		return false;
	}

	public double getHue() {
		return this.hue;
	}

	public double getSaturation() {
		return this.saturation;
	}

	public double getBrightness() {
		return this.brightness;
	}

	public double getContrast() {
		return this.contrast;
	}
}
