package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "songSlideLyrics")
@XmlAccessorType(XmlAccessType.NONE)
public final class SongSlideLyrics {
	// BCP 47
	@XmlAttribute(name = "language", required = false)
	final String language;
	
	// BCP 47
	@XmlAttribute(name = "transliteration", required = false)
	final String transliteration;
	
	public SongSlideLyrics() {
		this(null, null);
	}
	
	public SongSlideLyrics(String language) {
		this(language, null);
	}
	
	public SongSlideLyrics(String language, String transliteration) {
		this.language = language;
		this.transliteration = transliteration;
	}
	
	public String getLanguage() {
		return this.language;
	}
	
	public String getTransliteration() {
		return this.transliteration;
	}
}
