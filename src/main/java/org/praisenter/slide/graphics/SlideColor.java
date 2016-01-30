package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "color")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideColor implements SlidePaint {
	/** The red component */
	@XmlAttribute(name = "r", required = false)
	final int red;
	
	/** The green component */
	@XmlAttribute(name = "g", required = false)
	final int green;
	
	/** The blue component */
	@XmlAttribute(name = "b", required = false)
	final int blue;
	
	/** The alpha component */
	@XmlAttribute(name = "a", required = false)
	final int alpha;
	
	private SlideColor() {
		// for jaxb
		this.red = 0;
		this.green = 0;
		this.blue = 0;
		this.alpha = 0;
	}
	
	public SlideColor(int red, int green, int blue, int alpha) {
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
	
	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public int getAlpha() {
		return alpha;
	}
}
