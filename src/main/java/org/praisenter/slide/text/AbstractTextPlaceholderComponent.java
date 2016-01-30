package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractTextPlaceholderComponent extends AbstractTextComponent implements SlideRegion, SlideComponent, TextComponent, TextPlaceholderComponent {
	@XmlAttribute(name = "name", required = false)
	String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
