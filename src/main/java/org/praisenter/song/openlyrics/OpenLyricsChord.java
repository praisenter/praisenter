package org.praisenter.song.openlyrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chord")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsChord {
	/** The chord */
	@XmlAttribute(name = "name")
	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
