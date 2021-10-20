package org.praisenter.data.slide.effects;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public final class SlideColorAdjust implements ReadOnlySlideColorAdjust {
	private final DoubleProperty hue;
	private final DoubleProperty saturation;
	private final DoubleProperty brightness;
	private final DoubleProperty contrast;
	
	public SlideColorAdjust() {
		this.hue = new SimpleDoubleProperty(0);
		this.saturation = new SimpleDoubleProperty(0);
		this.brightness = new SimpleDoubleProperty(0);
		this.contrast = new SimpleDoubleProperty(0);
	}

	@Override
	public SlideColorAdjust copy() {
		SlideColorAdjust adjust = new SlideColorAdjust();
		adjust.hue.set(this.hue.get());
		adjust.saturation.set(this.saturation.get());
		adjust.brightness.set(this.brightness.get());
		adjust.contrast.set(this.contrast.get());
		return adjust;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("COLOR_ADJUST")
		  .append("[")
		  .append(this.hue.get()).append(", ")
		  .append(this.saturation.get()).append(", ")
		  .append(this.brightness.get()).append(", ")
		  .append(this.contrast.get())
		  .append("]");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(
				this.hue.get(),
				this.saturation.get(),
				this.brightness.get(),
				this.contrast.get());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideColorAdjust) {
			SlideColorAdjust adjust = (SlideColorAdjust)obj;
			return adjust.hue.get() == this.hue.get() &&
				   adjust.saturation.get() == this.saturation.get() &&
				   adjust.brightness.get() == this.brightness.get() &&
				   adjust.contrast.get() == this.contrast.get();
		}
		return false;
	}

	@Override
	@JsonProperty
	public double getHue() {
		return this.hue.get();
	}
	
	@JsonProperty
	public void setHue(double hue) {
		this.hue.set(hue);
	}
	
	@Override
	public DoubleProperty hueProperty() {
		return this.hue;
	}

	@Override
	@JsonProperty
	public double getSaturation() {
		return this.saturation.get();
	}
	
	@JsonProperty
	public void setSaturation(double saturation) {
		this.saturation.set(saturation);
	}
	
	@Override
	public DoubleProperty saturationProperty() {
		return this.saturation;
	}
	
	@Override
	@JsonProperty
	public double getBrightness() {
		return this.brightness.get();
	}
	
	@JsonProperty
	public void setBrightness(double brightness) {
		this.brightness.set(brightness);
	}
	
	@Override
	public DoubleProperty brightnessProperty() {
		return this.brightness;
	}
	
	@Override
	@JsonProperty
	public double getContrast() {
		return this.contrast.get();
	}
	
	@JsonProperty
	public void setContrast(double contrast) {
		this.contrast.set(contrast);
	}
	
	@Override
	public DoubleProperty contrastProperty() {
		return this.contrast;
	}
}
