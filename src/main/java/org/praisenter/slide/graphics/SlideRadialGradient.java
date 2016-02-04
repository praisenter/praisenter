package org.praisenter.slide.graphics;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "radialGradient")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideRadialGradient extends SlideGradient implements SlidePaint {
	@XmlAttribute(name = "cx", required = false)
	final double centerX;
	
	@XmlAttribute(name = "cy", required = false)
	final double centerY;
	
	@XmlAttribute(name = "r", required = false)
	final double radius;
	
	@XmlAttribute(name = "cycle", required = false)
	final SlideGradientCycleType cycleType;
	
	private SlideRadialGradient() {
		this(0, 0, 0, SlideGradientCycleType.NONE, (SlideGradientStop[])null);
	}
	
	public SlideRadialGradient(double centerX, double centerY, double radius, SlideGradientCycleType cycleType, List<SlideGradientStop> stops) {
		super(stops);
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.cycleType = cycleType;
	}
	
	public SlideRadialGradient(double centerX, double centerY, double radius, SlideGradientCycleType cycleType, SlideGradientStop... stops) {
		super(stops);
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.cycleType = cycleType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideRadialGradient) {
			SlideRadialGradient g = (SlideRadialGradient)obj;
			if (this.centerX != g.centerX ||
				this.centerY != g.centerY ||
				this.radius != g.radius ||
				this.cycleType != g.cycleType) {
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}
	
	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public double getRadius() {
		return radius;
	}

	public SlideGradientCycleType getCycleType() {
		return this.cycleType;
	}
}
