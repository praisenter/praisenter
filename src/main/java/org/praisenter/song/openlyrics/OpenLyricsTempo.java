package org.praisenter.song.openlyrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "tempo")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsTempo {
	@XmlAttribute(name = "type", required = false)
	String type;
	
	@XmlValue
	String text;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
