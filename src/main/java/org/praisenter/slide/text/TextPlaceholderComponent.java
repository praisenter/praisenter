package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

@XmlRootElement(name = "textPlaceholderComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class TextPlaceholderComponent extends BasicTextComponent implements SlideRegion, SlideComponent, TextComponent {
	public static final int TYPE_ALL = Integer.MAX_VALUE;
	public static final int TYPE_PRIMARY = 2;
	public static final int TYPE_SECONDARY = 4;
	public static final int TYPE_TERTIARY = 8;
	public static final int TYPE_QUATERNARY = 16;
	public static final int TYPE_QUINARY = 32;
	public static final int TYPE_SENARY = 64;
	
	@XmlAttribute(name = "type", required = false)
	int type;

	public TextPlaceholderComponent() {
		// by default, all
		this.type = TYPE_ALL;
	}
	
	public boolean isType(int type) {
		return (this.type & type) == type;
	}
	
	public void addType(int type) {
		this.type = this.type | type;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
