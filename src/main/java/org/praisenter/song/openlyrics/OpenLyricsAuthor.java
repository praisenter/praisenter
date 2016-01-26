package org.praisenter.song.openlyrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "author")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsAuthor {
	@XmlAttribute(name = "type", required = false)
	String type;
	
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlValue
	String name;

	@Override
	public String toString() {
		return name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
