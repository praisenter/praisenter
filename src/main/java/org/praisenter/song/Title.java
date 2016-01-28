package org.praisenter.song;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.praisenter.Localized;

@XmlRootElement(name = "title")
@XmlAccessorType(XmlAccessType.NONE)
public final class Title implements Localized {
	@XmlAttribute(name = "original", required = false)
	boolean original;
	
	@XmlAttribute(name = "language", required = false)
	String language;
	
	@XmlAttribute(name = "transliteration", required = false)
	String transliteration;

	@XmlValue
	String text;
	
	/* (non-Javadoc)
	 * @see org.praisenter.Localized#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return Song.getLocale(this.language);
	}
	
	public boolean isOriginal() {
		return original;
	}

	public void setOriginal(boolean original) {
		this.original = original;
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
