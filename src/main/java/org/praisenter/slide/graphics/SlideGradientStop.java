package org.praisenter.slide.graphics;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stop")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideGradientStop {
	/** The stop location from 0.0 - 1.0 inclusive */
	@XmlAttribute(name = "offset", required = false)
	final double offset;
	
	/** The stop color */
	@XmlElement(name = "color", required = false)
	final SlideColor color;
	
	private SlideGradientStop() {
		// for jaxb
		this(0.0f, 0, 0, 0, 1.0);
	}
	
	public SlideGradientStop(double offset, SlideColor color) {
		this.offset = offset;
		this.color = color;
	}
	
	public SlideGradientStop(double offset, int red, int green, int blue, double alpha) {
		this.offset = offset;
		this.color = new SlideColor(red / 255.0, green / 255.0, blue / 255.0, alpha);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideGradientStop) {
			SlideGradientStop s = (SlideGradientStop)obj;
			if (Objects.equals(this.color, s.color) &&
				this.offset == s.offset) {
				return true;
			}
		}
		return false;
	}
	
	public double getOffset() {
		return offset;
	}

	public SlideColor getColor() {
		return color;
	}
}
