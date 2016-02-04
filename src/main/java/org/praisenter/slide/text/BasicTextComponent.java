package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

@XmlRootElement(name = "textComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class BasicTextComponent extends AbstractTextComponent implements SlideRegion, SlideComponent, TextComponent {
	/** The text */
	@XmlElement(name = "text", required = false)
	protected String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
