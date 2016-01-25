package org.praisenter.song.openlyrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "theme")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsTheme {
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlAttribute(name = "translit", required = false)
	String transliteration;
	
	@XmlValue
	String text;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTransliteration() {
		return transliteration;
	}

	public void setTransliteration(String transliteration) {
		this.transliteration = transliteration;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
