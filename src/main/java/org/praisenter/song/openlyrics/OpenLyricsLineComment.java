package org.praisenter.song.openlyrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsLineComment {
	/** The comment text */
	@XmlValue
	String text;

	@Override
	public String toString() {
		return text;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
