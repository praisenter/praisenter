package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "color")
@XmlAccessorType(XmlAccessType.NONE)
public class ColorFill implements Fill {
	/** The red component */
	@XmlAttribute(name = "r")
	protected int red;
	
	/** The green component */
	@XmlAttribute(name = "g")
	protected int green;
	
	/** The blue component */
	@XmlAttribute(name = "b")
	protected int blue;
	
	/** The alpha component */
	@XmlAttribute(name = "a")
	protected int alpha;
	
	public ColorFill(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
}
