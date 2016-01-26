package org.praisenter.song.openlyrics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "verse")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsVerse {
	/** The verse name */
	@XmlAttribute(name = "name", required = false)
	String name;
	
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlAttribute(name = "translit", required = false)
	String transliteration;
	
	/** The verse lines */
	@XmlElement(name = "lines")
	List<OpenLyricsLine> lines;
	
	public OpenLyricsVerse() {
		this.name = "c1";
		this.lines = new ArrayList<OpenLyricsLine>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public List<OpenLyricsLine> getLines() {
		return lines;
	}

	public void setLines(List<OpenLyricsLine> lines) {
		this.lines = lines;
	}
}
