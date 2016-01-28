package org.praisenter.song;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.praisenter.Localized;

@XmlRootElement(name = "theme")
@XmlAccessorType(XmlAccessType.NONE)
public final class Theme implements Localized {
	@XmlValue
	String text;
	
	@XmlAttribute(name = "language", required = false)
	String language;
	
	@XmlAttribute(name = "transliteration", required = false)
	String transliteration;
	
	/* (non-Javadoc)
	 * @see org.praisenter.Localized#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return Song.getLocale(this.language);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
}
