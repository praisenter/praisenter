package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "color")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideColor extends AbstractSlidePaint implements SlidePaint {
	/** The red component */
	@XmlAttribute(name = "r", required = false)
	final double red;
	
	/** The green component */
	@XmlAttribute(name = "g", required = false)
	final double green;
	
	/** The blue component */
	@XmlAttribute(name = "b", required = false)
	final double blue;
	
	/** The alpha component */
	@XmlAttribute(name = "a", required = false)
	final double alpha;
	
	private SlideColor() {
		// for jaxb
		this.red = 0;
		this.green = 0;
		this.blue = 0;
		this.alpha = 0;
	}

	public SlideColor(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideColor) {
			SlideColor c = (SlideColor)obj;
			if (c.red == this.red &&
				c.green == this.green &&
				c.blue == this.blue &&
				c.alpha == this.alpha) {
				return true;
			}
		}
		return false;
	}
	
	public double getRed() {
		return red;
	}

	public double getGreen() {
		return green;
	}

	public double getBlue() {
		return blue;
	}

	public double getAlpha() {
		return alpha;
	}
}
